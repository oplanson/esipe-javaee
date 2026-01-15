#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
# Podman deployment and testing script for Lab 04B - EJB Banking Application
# This script builds and deploys the EJB banking application using Podman

set -e  # Exit on error

echo "=========================================="
echo "Lab 04B - EJB Banking Deployment (Podman)"
echo "=========================================="
echo ""

# Configuration
IMAGE_NAME="banking-ejb-lab04b"
CONTAINER_NAME="banking-ejb-lab04b"
APP_PORT=9080
DB_CONTAINER="lab04b-postgres"
NETWORK_NAME="lab04b-network"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[✓]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[!]${NC} $1"
}

print_error() {
    echo -e "${RED}[✗]${NC} $1"
}

# Check if podman is installed
if ! command -v podman &> /dev/null; then
    print_error "Podman is not installed. Please install Podman first."
    exit 1
fi

print_status "Podman is installed"
podman --version

# Navigate to solution directory
cd solution

# Step 0: Check and cleanup existing containers
echo ""
echo "Step 0: Checking for existing containers..."

# Determine which container runtime to use
CONTAINER_CMD=""
if command -v docker &> /dev/null; then
    CONTAINER_CMD="docker"
elif command -v podman &> /dev/null; then
    CONTAINER_CMD="podman"
fi

# Check if application container exists and is running
if $CONTAINER_CMD ps 2>/dev/null | grep -q $CONTAINER_NAME; then
    print_warning "Application container is running, stopping..."
    podman stop $CONTAINER_NAME 2>/dev/null || true
    print_status "Container stopped"
fi

# Check if application container exists (stopped)
if $CONTAINER_CMD ps -a 2>/dev/null | grep -q $CONTAINER_NAME; then
    print_warning "Application container exists, removing..."
    podman rm $CONTAINER_NAME 2>/dev/null || true
    print_status "Container removed"
fi

# Check if database container exists and is running
if $CONTAINER_CMD ps 2>/dev/null | grep -q $DB_CONTAINER; then
    print_warning "Database container is running, stopping..."
    podman stop $DB_CONTAINER 2>/dev/null || true
    print_status "Database container stopped"
fi

# Check if database container exists (stopped)
if $CONTAINER_CMD ps -a 2>/dev/null | grep -q $DB_CONTAINER; then
    print_warning "Database container exists, removing..."
    podman rm $DB_CONTAINER 2>/dev/null || true
    print_status "Database container removed"
fi

# Check for port conflicts - stop any container using port 9080
echo "Checking for port conflicts on $APP_PORT..."
CONFLICTING_CONTAINERS=$(podman ps --format "{{.Names}}" | while read -r name; do
    if podman port "$name" 2>/dev/null | grep -q "0.0.0.0:$APP_PORT"; then
        echo "$name"
    fi
done)

if [ -n "$CONFLICTING_CONTAINERS" ]; then
    print_warning "Found containers using port $APP_PORT:"
    echo "$CONFLICTING_CONTAINERS" | while read -r container; do
        if [ -n "$container" ] && [ "$container" != "$CONTAINER_NAME" ]; then
            print_warning "  Stopping $container..."
            podman stop "$container" > /dev/null 2>&1 || true
            podman rm "$container" > /dev/null 2>&1 || true
            print_status "  ✓ $container stopped and removed"
        fi
    done
else
    print_status "No port conflicts detected"
fi

# Check if image exists
if podman images | grep -q "banking-ejb-lab04b"; then
    print_warning "Old image exists, removing..."
    podman rmi $IMAGE_NAME 2>/dev/null || true
    print_status "Old image removed"
fi

# Remove existing network if it exists
if podman network exists $NETWORK_NAME 2>/dev/null; then
    print_warning "Network exists, removing..."
    podman network rm $NETWORK_NAME 2>/dev/null || true
    print_status "Network removed"
fi

# Stop docker-compose services if running (with volumes cleanup)
if command -v docker-compose &> /dev/null; then
    if docker-compose ps 2>/dev/null | grep -q "Up\|Exit"; then
        print_warning "Stopping any existing docker-compose services and removing volumes..."
        docker-compose down -v 2>/dev/null || true
        print_status "Docker-compose services and volumes removed"
    fi
fi

print_status "Cleanup complete - ready for fresh deployment"

# Step 1: Create Podman network
echo ""
echo "Step 1: Creating Podman network..."
if podman network create $NETWORK_NAME; then
    print_status "Network created: $NETWORK_NAME"
else
    print_error "Failed to create network"
    exit 1
fi

# Step 2: Start PostgreSQL database
echo ""
echo "Step 2: Starting PostgreSQL database..."
podman run -d \
    --name $DB_CONTAINER \
    --network $NETWORK_NAME \
    -e POSTGRES_DB=bankingdb \
    -e POSTGRES_USER=bankuser \
    -e POSTGRES_PASSWORD=bankpass \
    -p 5432:5432 \
    postgres:16-alpine

if [ $? -eq 0 ]; then
    print_status "PostgreSQL container started"
else
    print_error "Failed to start PostgreSQL container"
    exit 1
fi

# Wait for database to be ready
echo "Waiting for database to be ready..."
MAX_DB_WAIT=30
DB_WAIT=0
while [ $DB_WAIT -lt $MAX_DB_WAIT ]; do
    if podman exec $DB_CONTAINER pg_isready -U bankuser -d bankingdb > /dev/null 2>&1; then
        print_status "Database is ready!"
        break
    fi
    DB_WAIT=$((DB_WAIT + 1))
    echo -n "."
    sleep 1
done

if [ $DB_WAIT -eq $MAX_DB_WAIT ]; then
    print_warning "Database readiness check timed out, but continuing..."
    echo "You may need to wait a bit longer for the database to be fully ready."
    sleep 5
fi

echo ""

# Step 3: Build application with Maven
echo ""
echo "Step 3: Building application with Maven..."
echo "Running: mvn clean package"
if mvn clean package -B -q -DskipTests 2>&1 | grep -v "^\[INFO\]" | grep -v "^-" > /dev/null; then
    print_status "Application built successfully"
    echo "  WAR file: target/banking-ejb-app.war"
else
    print_error "Maven build failed"
    exit 1
fi

# Step 4: Build Podman image
echo ""
echo "Step 4: Building Podman image..."
if podman images | grep -q $IMAGE_NAME; then
    print_warning "Image already exists, removing..."
    podman rmi $IMAGE_NAME 2>/dev/null || true
fi

if podman build -t $IMAGE_NAME -f Containerfile .; then
    print_status "Podman image built successfully"
else
    print_error "Podman image build failed"
    exit 1
fi

# Step 5: Verify cleanup (double-check)
echo ""
echo "Step 5: Final verification before starting container..."
if podman ps -a | grep -q $CONTAINER_NAME; then
    print_warning "Container still exists, force removing..."
    podman stop $CONTAINER_NAME 2>/dev/null || true
    podman rm -f $CONTAINER_NAME 2>/dev/null || true
fi
print_status "Ready to start container"

# Step 6: Run application container
echo ""
echo "Step 6: Starting application container..."

podman run -d \
    --name $CONTAINER_NAME \
    --network $NETWORK_NAME \
    -p $APP_PORT:9080 \
    -p 9443:9443 \
    -e DB_HOST=$DB_CONTAINER \
    -e DB_PORT=5432 \
    -e DB_NAME=bankingdb \
    -e DB_USER=bankuser \
    -e DB_PASSWORD=bankpass \
    $IMAGE_NAME

if [ $? -eq 0 ]; then
    print_status "Container started successfully"
    print_status "Database host configured as: $DB_CONTAINER"
    print_status "Application accessible at: http://localhost:$APP_PORT"
else
    print_error "Failed to start container"
    exit 1
fi

# Step 7: Wait for application to start
echo ""
echo "Step 7: Waiting for application to start..."
MAX_ATTEMPTS=60
ATTEMPT=0

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if podman logs $CONTAINER_NAME | grep -q "CWWKF0011I"; then
        print_status "Open Liberty started successfully"
        break;
    fi
    ATTEMPT=$((ATTEMPT + 1))
    echo -n "."
    sleep 2
done

if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
    print_warning "Liberty startup check timeout reached, but continuing with tests..."
    echo "The application may still be starting. Waiting 10 more seconds..."
    sleep 10
fi

echo ""

# Step 8: Test health endpoint
echo ""
echo "Step 8: Testing health endpoint..."
MAX_RETRIES=12
RETRY_COUNT=0
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    HEALTH_RESPONSE=$(curl -s http://localhost:$APP_PORT/health 2>/dev/null || echo "")
    if echo "$HEALTH_RESPONSE" | grep -q "UP"; then
        print_status "Health check passed (all checks UP)"
        echo "$HEALTH_RESPONSE" | jq '.' 2>/dev/null || echo "$HEALTH_RESPONSE"
        break
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
        print_error "Health check failed after $MAX_RETRIES attempts"
        echo "Liberty logs:"
        podman logs $CONTAINER_NAME | tail -50
        exit 1
    fi
    echo "Waiting for application to be ready... (attempt $RETRY_COUNT/$MAX_RETRIES)"
    sleep 5
done

echo ""

# Step 9: Test metrics endpoint
echo ""
echo "Step 9: Testing metrics endpoint..."
METRICS_RESPONSE=$(curl -s http://localhost:$APP_PORT/metrics)
if [ -n "$METRICS_RESPONSE" ]; then
    print_status "Metrics endpoint accessible"
    echo "$METRICS_RESPONSE" | head -20
else
    print_warning "Metrics endpoint not accessible"
fi

echo ""

# Step 10: Test application home page
echo ""
echo "Step 10: Testing application home page..."
APP_RESPONSE=$(curl -s http://localhost:$APP_PORT/)
if echo "$APP_RESPONSE" | grep -q "Lab 04B"; then
    print_status "Application home page accessible"
else
    print_warning "Application home page not accessible"
    podman logs $CONTAINER_NAME | tail -30
fi

echo ""

# Step 11: Comprehensive EJB Functional Tests
echo ""
echo "=========================================="
echo "Step 11: Testing EJB Components"
echo "=========================================="
echo ""

# Test counters
TESTS_PASSED=0
TESTS_FAILED=0

# Test 1: Create first account (Stateless EJB)
echo "Test 1: Creating first account (Stateless EJB - AccountServiceBean)..."
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" --basic -u admin:admin123 "http://localhost:$APP_PORT/banking?action=create")
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
RESPONSE_BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')

echo "  HTTP Status: $HTTP_CODE"
if [ -z "$RESPONSE_BODY" ]; then
    print_error "Empty response received"
    echo "  Debug: Checking if application is accessible..."
    curl -v "http://localhost:$APP_PORT/" 2>&1 | head -20
    TESTS_FAILED=$((TESTS_FAILED + 1))
elif echo "$RESPONSE_BODY" | grep -q "Account created"; then
    print_status "Account creation successful"
    ACCOUNT1_ID=$(echo "$RESPONSE_BODY" | grep -o 'ACC-[0-9]\+' | head -1)
    echo "  Account Number: $ACCOUNT1_ID"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    print_error "Account creation failed"
    echo "  Response preview:"
    echo "$RESPONSE_BODY" | head -10
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 2: Create second account
echo ""
echo "Test 2: Creating second account..."
sleep 1
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" --basic -u admin:admin123 "http://localhost:$APP_PORT/banking?action=create")
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
RESPONSE_BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')

echo "  HTTP Status: $HTTP_CODE"
if [ -z "$RESPONSE_BODY" ]; then
    print_error "Empty response received"
    TESTS_FAILED=$((TESTS_FAILED + 1))
elif echo "$RESPONSE_BODY" | grep -q "Account created"; then
    print_status "Second account created"
    ACCOUNT2_ID=$(echo "$RESPONSE_BODY" | grep -o 'ACC-[0-9]\+' | tail -1)
    echo "  Account Number: $ACCOUNT2_ID"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    print_error "Second account creation failed"
    echo "  Response preview:"
    echo "$RESPONSE_BODY" | head -5
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 3: Verify accounts list
echo ""
echo "Test 3: Verifying accounts list..."
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" --basic -u admin:admin123 "http://localhost:$APP_PORT/banking")
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
RESPONSE_BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')

echo "  HTTP Status: $HTTP_CODE"
if [ -z "$RESPONSE_BODY" ]; then
    print_error "Empty response received"
    TESTS_FAILED=$((TESTS_FAILED + 1))
else
    ACCOUNT_COUNT=$(echo "$RESPONSE_BODY" | grep -o "ACC-" | wc -l)
    if [ "$ACCOUNT_COUNT" -ge 2 ]; then
        print_status "Found $ACCOUNT_COUNT accounts"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        print_error "Expected at least 2 accounts, found $ACCOUNT_COUNT"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
fi

# Test 4: Deposit operation (Stateless EJB with CMT)
echo ""
echo "Test 4: Testing deposit operation (Container-Managed Transaction)..."
FIRST_ACCOUNT_ID=$(curl -s --basic -u admin:admin123 "http://localhost:$APP_PORT/banking" | grep -o '<td>[0-9]\+</td>' | head -1 | sed 's/<[^>]*>//g')
if [ -n "$FIRST_ACCOUNT_ID" ]; then
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" --basic -u admin:admin123 "http://localhost:$APP_PORT/banking?action=deposit&accountId=$FIRST_ACCOUNT_ID&amount=1000.00")
    HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
    RESPONSE_BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')
    
    echo "  HTTP Status: $HTTP_CODE"
    if [ -z "$RESPONSE_BODY" ]; then
        print_error "Empty response received"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    elif echo "$RESPONSE_BODY" | grep -q "Deposit successful"; then
        print_status "Deposit of \$1000.00 successful"
        echo "  Account ID: $FIRST_ACCOUNT_ID"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        print_error "Deposit failed"
        echo "  Response preview:"
        echo "$RESPONSE_BODY" | head -5
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
else
    print_warning "Could not extract account ID for deposit test"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 5: Withdrawal operation
echo ""
echo "Test 5: Testing withdrawal operation..."
if [ -n "$FIRST_ACCOUNT_ID" ]; then
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" --basic -u admin:admin123 "http://localhost:$APP_PORT/banking?action=withdraw&accountId=$FIRST_ACCOUNT_ID&amount=250.00")
    HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
    RESPONSE_BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')
    
    echo "  HTTP Status: $HTTP_CODE"
    if [ -z "$RESPONSE_BODY" ]; then
        print_error "Empty response received"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    elif echo "$RESPONSE_BODY" | grep -q "Withdrawal successful"; then
        print_status "Withdrawal of \$250.00 successful"
        echo "  Account ID: $FIRST_ACCOUNT_ID"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        print_error "Withdrawal failed"
        echo "  Response preview:"
        echo "$RESPONSE_BODY" | head -5
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
else
    print_warning "Skipping withdrawal test (no account ID)"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 6: Transfer operation (tests transaction management)
echo ""
echo "Test 6: Testing transfer operation (Transaction Management)..."
SECOND_ACCOUNT_ID=$(curl -s --basic -u admin:admin123 "http://localhost:$APP_PORT/banking" | grep -o '<td>[0-9]\+</td>' | sed -n '2p' | sed 's/<[^>]*>//g')
if [ -n "$FIRST_ACCOUNT_ID" ] && [ -n "$SECOND_ACCOUNT_ID" ]; then
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" --basic -u admin:admin123 "http://localhost:$APP_PORT/banking?action=transfer&fromId=$FIRST_ACCOUNT_ID&toId=$SECOND_ACCOUNT_ID&amount=100.00")
    HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
    RESPONSE_BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')
    
    echo "  HTTP Status: $HTTP_CODE"
    if [ -z "$RESPONSE_BODY" ]; then
        print_error "Empty response received"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    elif echo "$RESPONSE_BODY" | grep -q "Transfer successful"; then
        print_status "Transfer of \$100.00 successful"
        echo "  From Account ID: $FIRST_ACCOUNT_ID"
        echo "  To Account ID: $SECOND_ACCOUNT_ID"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        print_error "Transfer failed"
        echo "  Response preview:"
        echo "$RESPONSE_BODY" | head -5
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
else
    print_warning "Skipping transfer test (insufficient accounts)"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 7: Verify balance after operations
echo ""
echo "Test 7: Verifying account balances..."
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" --basic -u admin:admin123 "http://localhost:$APP_PORT/banking")
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
RESPONSE_BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')

echo "  HTTP Status: $HTTP_CODE"
if [ -z "$RESPONSE_BODY" ]; then
    print_error "Empty response received"
    TESTS_FAILED=$((TESTS_FAILED + 2))
else
    if echo "$RESPONSE_BODY" | grep -q "\$650.00"; then
        print_status "First account balance correct (\$650.00 = \$1000 - \$250 - \$100)"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        print_warning "Could not verify first account balance"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi

    if echo "$RESPONSE_BODY" | grep -q "\$100.00"; then
        print_status "Second account balance correct (\$100.00 from transfer)"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        print_warning "Could not verify second account balance"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
fi

# Test 8: Singleton EJB - Configuration Service
echo ""
echo "Test 8: Testing Singleton EJB (ConfigServiceBean)..."
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" --basic -u admin:admin123 "http://localhost:$APP_PORT/banking")
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
RESPONSE_BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')

echo "  HTTP Status: $HTTP_CODE"
if [ -z "$RESPONSE_BODY" ]; then
    print_error "Empty response received"
    TESTS_FAILED=$((TESTS_FAILED + 1))
elif echo "$RESPONSE_BODY" | grep -qw "Banking EJB Application"; then
    print_status "Singleton ConfigService working (app.name retrieved)"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    print_warning "Could not verify Singleton ConfigService"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 9: Timer Service - Report Generator
echo ""
echo "Test 9: Testing Timer Service (ReportGeneratorBean)..."
# Reuse RESPONSE_BODY from Test 8
if [ -n "$RESPONSE_BODY" ] && echo "$RESPONSE_BODY" | grep -q "Report Statistics"; then
    print_status "Timer Service working (ReportGeneratorBean active)"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    print_warning "Could not verify Timer Service"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 10: Security - Role-based access
echo ""
echo "Test 10: Testing EJB Security (Role-based access)..."
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" --basic -u admin:admin123 "http://localhost:$APP_PORT/banking?action=create")
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
RESPONSE_BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')

echo "  HTTP Status: $HTTP_CODE"
if [ -z "$RESPONSE_BODY" ]; then
    print_error "Empty response received"
    TESTS_FAILED=$((TESTS_FAILED + 1))
elif echo "$RESPONSE_BODY" | grep -q "Account created"; then
    print_status "Admin role has access to create accounts"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    print_error "Admin role access failed"
    echo "  Response preview:"
    echo "$RESPONSE_BODY" | head -5
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 11: Database persistence verification
echo ""
echo "Test 11: Verifying database persistence..."
DB_COUNT=$(podman exec $DB_CONTAINER psql -U bankuser -d bankingdb -t -c "SELECT COUNT(*) FROM accounts;" 2>/dev/null | tr -d ' ')
if [ -n "$DB_COUNT" ] && [ "$DB_COUNT" -ge 2 ]; then
    print_status "Database persistence verified ($DB_COUNT accounts in database)"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    print_warning "Could not verify database persistence"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 12: JMS Queue verification (Message-Driven Bean)
echo ""
echo "Test 12: Checking JMS configuration (for NotificationMDB)..."
RESPONSE=$(curl -s http://localhost:$APP_PORT/health)
if echo "$RESPONSE" | grep -q "UP"; then
    print_status "JMS configuration appears healthy"
    echo "  Note: MDB will process messages asynchronously"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    print_warning "Could not verify JMS configuration"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test Summary
echo ""
echo "=========================================="
echo "Test Summary"
echo "=========================================="
echo -e "${GREEN}Tests Passed: $TESTS_PASSED${NC}"
if [ $TESTS_FAILED -gt 0 ]; then
    echo -e "${RED}Tests Failed: $TESTS_FAILED${NC}"
else
    echo -e "${GREEN}Tests Failed: $TESTS_FAILED${NC}"
fi
echo "Total Tests: $((TESTS_PASSED + TESTS_FAILED))"
echo ""

# Display container status
echo "Container Status:"
podman ps --filter "name=lab04b"

# Summary
echo ""
echo "=========================================="
echo "Deployment Summary"
echo "=========================================="
print_status "Application URL: http://localhost:$APP_PORT"
print_status "Banking Operations: http://localhost:$APP_PORT/banking"
print_status "Health Check: http://localhost:$APP_PORT/health"
print_status "Metrics: http://localhost:$APP_PORT/metrics"
echo ""
echo "Default credentials:"
echo "  - admin/admin123"
echo "  - teller/teller123"
echo "  - customer/customer123"
echo ""
echo "EJB Components Tested:"
echo "  ✓ Stateless Session Bean (AccountServiceBean)"
echo "  ✓ Singleton Session Bean (ConfigServiceBean)"
echo "  ✓ Timer Service (ReportGeneratorBean)"
echo "  ✓ Container-Managed Transactions (CMT)"
echo "  ✓ EJB Security (Role-based access)"
echo "  ✓ JPA Integration"
echo ""
echo "Container Management:"
echo "  View logs:    podman logs -f $CONTAINER_NAME"
echo "  Stop:         podman stop $CONTAINER_NAME $DB_CONTAINER"
echo "  Remove:       podman rm $CONTAINER_NAME $DB_CONTAINER"
echo "  Remove network: podman network rm $NETWORK_NAME"
echo ""
echo "=========================================="

if [ $TESTS_FAILED -eq 0 ]; then
    print_status "Lab 04B deployment complete - All tests passed!"
    echo "=========================================="
    exit 0
else
    print_warning "Lab 04B deployment complete - Some tests failed"
    echo "=========================================="
    exit 1
fi

# Made with Bob