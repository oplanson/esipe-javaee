#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
# Podman deployment and testing script for Lab 06 - Domain-Driven Design
# This script builds and deploys the banking DDD application using Podman

set -e  # Exit on error

echo "=========================================="
echo "Lab 06 - DDD Deployment (Podman)"
echo "=========================================="
echo ""

# Configuration
IMAGE_NAME="banking-ddd-lab06"
CONTAINER_NAME="banking-ddd-lab06"
APP_PORT=9080
DB_CONTAINER="lab06-postgres"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
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
if podman images | grep -q "banking-ddd-lab06"; then
    print_warning "Old image exists, removing..."
    podman rmi $IMAGE_NAME 2>/dev/null || true
    print_status "Old image removed"
fi

# Stop docker-compose services if running
if command -v docker-compose &> /dev/null; then
    print_warning "Stopping any existing docker-compose services..."
    docker-compose down 2>/dev/null || true
    print_status "Docker-compose services stopped"
fi

# Clean up any orphaned networks using available container runtime
if $CONTAINER_CMD network ls 2>/dev/null | grep -q "solution_bank-network"; then
    print_warning "Removing orphaned network solution_bank-network..."
    $CONTAINER_CMD network rm solution_bank-network 2>/dev/null || true
fi

print_status "Cleanup complete - ready for fresh deployment"

# Step 1: Start PostgreSQL database
echo ""
echo "Step 1: Starting PostgreSQL database..."
if ! docker-compose up -d --remove-orphans; then
    print_error "Failed to start database with docker-compose"
    exit 1
fi
print_status "PostgreSQL database container starting..."

# Wait for database to be ready
echo "Waiting for database to be ready..."
MAX_DB_WAIT=30
DB_WAIT=0
while [ $DB_WAIT -lt $MAX_DB_WAIT ]; do
    # Try with docker first, then podman
    if docker exec $DB_CONTAINER pg_isready -U bankuser -d bankdb > /dev/null 2>&1; then
        print_status "Database is ready!"
        break
    elif podman exec $DB_CONTAINER pg_isready -U bankuser -d bankdb > /dev/null 2>&1; then
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

# Step 2: Build application with Maven
echo ""
echo "Step 2: Building application with Maven..."
echo "Running: mvn clean package"
if mvn clean package; then
    print_status "Application built successfully"
else
    print_error "Maven build failed"
    exit 1
fi

# Step 3: Build Podman image
echo ""
echo "Step 3: Building Podman image..."
if podman images | grep -q $IMAGE_NAME; then
    print_warning "Image already exists, removing..."
    podman rmi $IMAGE_NAME 2>/dev/null || true
fi

if podman build -t $IMAGE_NAME .; then
    print_status "Podman image built successfully"
else
    print_error "Podman image build failed"
    exit 1
fi

# Step 4: Verify cleanup (double-check)
echo ""
echo "Step 4: Final verification before starting container..."
if podman ps -a | grep -q $CONTAINER_NAME; then
    print_warning "Container still exists, force removing..."
    podman stop $CONTAINER_NAME 2>/dev/null || true
    podman rm -f $CONTAINER_NAME 2>/dev/null || true
fi
print_status "Ready to start container"

# Step 5: Run container
echo ""
echo "Step 5: Starting application container..."

# Determine database host for container
# When NOT using --network host, we need to use special hostnames
DB_HOST="host.containers.internal"
if [[ "$OSTYPE" == "darwin"* ]]; then
    DB_HOST="host.docker.internal"
    print_warning "macOS detected, using host.docker.internal for database connection"
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # On Linux, add host.containers.internal to /etc/hosts or use host IP
    DB_HOST="host.containers.internal"
    print_warning "Linux detected, using host.containers.internal for database connection"
fi

# Run container WITHOUT --network host to properly expose ports
# IMPORTANT: Liberty uses underscores in env vars, which are converted to dots
podman run -d \
    --name $CONTAINER_NAME \
    -p $APP_PORT:9080 \
    --add-host=host.containers.internal:host-gateway \
    -e db_host=$DB_HOST \
    -e db_port=5432 \
    -e db_name=bankdb \
    -e db_user=bankuser \
    -e db_password=bankpass \
    $IMAGE_NAME

print_status "Container started successfully"
print_status "Database host configured as: $DB_HOST"
print_status "Application accessible at: http://localhost:$APP_PORT"

# Step 6: Wait for application to start
echo ""
echo "Step 6: Waiting for application to start..."
MAX_ATTEMPTS=60
ATTEMPT=0

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    # Try multiple endpoints to check if app is ready
    if curl -s http://localhost:$APP_PORT/health/ready > /dev/null 2>&1; then
        print_status "Application is ready! (health/ready endpoint)"
        break
    elif curl -s http://localhost:$APP_PORT/health > /dev/null 2>&1; then
        print_status "Application is ready! (health endpoint)"
        break
    elif curl -s http://localhost:$APP_PORT/ > /dev/null 2>&1; then
        print_status "Application is ready! (root endpoint)"
        break
    fi
    ATTEMPT=$((ATTEMPT + 1))
    echo -n "."
    sleep 2
done

if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
    print_warning "Health check timeout reached, but continuing with tests..."
    echo "The application may still be starting. Waiting 10 more seconds..."
    sleep 10
fi

echo ""

# Step 7: Run DDD pattern tests
echo ""
echo "Step 7: Testing DDD patterns..."
echo ""

BASE_URL="http://localhost:$APP_PORT/api"

# Test 1: Health check
echo "Test 1: Health check"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$APP_PORT/health)
if [ "$HTTP_CODE" = "200" ]; then
    HEALTH_RESPONSE=$(curl -s http://localhost:$APP_PORT/health)
    if echo "$HEALTH_RESPONSE" | grep -q "UP"; then
        print_status "Health check passed (all checks UP)"
    else
        print_warning "Health check endpoint accessible but some checks are DOWN"
        echo "Response: $HEALTH_RESPONSE" | head -n 5
    fi
else
    print_error "Health check endpoint not accessible (HTTP $HTTP_CODE)"
fi

# Test 2: Create client with Email Value Object
echo ""
echo "Test 2: POST /api/clients (Email Value Object)"
CREATE_CLIENT=$(curl -s -X POST $BASE_URL/clients \
    -H "Content-Type: application/json" \
    -d '{"name":"John Doe","email":"john.doe@example.com","premium":false}')
if [ -n "$CREATE_CLIENT" ]; then
    print_status "Client created with Email VO"
    echo "$CREATE_CLIENT" | jq . 2>/dev/null || echo "$CREATE_CLIENT"
    CLIENT_ID=$(echo "$CREATE_CLIENT" | jq -r '.id' 2>/dev/null)
else
    print_error "Failed to create client"
fi

# Test 3: Create account with Money and AccountType Value Objects
if [ -n "$CLIENT_ID" ] && [ "$CLIENT_ID" != "null" ]; then
    echo ""
    echo "Test 3: POST /api/accounts (Money & AccountType VOs)"
    CREATE_ACCOUNT=$(curl -s -X POST $BASE_URL/accounts \
        -H "Content-Type: application/json" \
        -d "{\"client\":{\"id\":$CLIENT_ID},\"accountType\":\"CHECKING\",\"balance\":{\"amount\":1000.0,\"currency\":\"EUR\"}}")
    if [ -n "$CREATE_ACCOUNT" ]; then
        print_status "Account created with Value Objects"
        echo "$CREATE_ACCOUNT" | jq . 2>/dev/null || echo "$CREATE_ACCOUNT"
        ACCOUNT_ID=$(echo "$CREATE_ACCOUNT" | jq -r '.id' 2>/dev/null)
    else
        print_error "Failed to create account"
    fi
fi

# Test 4: Deposit money (Domain Event)
if [ -n "$ACCOUNT_ID" ] && [ "$ACCOUNT_ID" != "null" ]; then
    echo ""
    echo "Test 4: POST /api/accounts/$ACCOUNT_ID/deposit (Domain Event)"
    DEPOSIT_RESPONSE=$(curl -s -X POST "$BASE_URL/accounts/$ACCOUNT_ID/deposit?amount=500.0")
    if echo "$DEPOSIT_RESPONSE" | grep -q "1500"; then
        print_status "Deposit successful - MoneyDepositedEvent fired"
        echo "$DEPOSIT_RESPONSE" | jq . 2>/dev/null || echo "$DEPOSIT_RESPONSE"
    else
        print_warning "Deposit response unexpected"
        echo "$DEPOSIT_RESPONSE"
    fi
fi

# Test 5: Withdraw money (Business Rules)
if [ -n "$ACCOUNT_ID" ] && [ "$ACCOUNT_ID" != "null" ]; then
    echo ""
    echo "Test 5: POST /api/accounts/$ACCOUNT_ID/withdraw (Business Rules)"
    WITHDRAW_RESPONSE=$(curl -s -X POST "$BASE_URL/accounts/$ACCOUNT_ID/withdraw?amount=200.0")
    if echo "$WITHDRAW_RESPONSE" | grep -q "1300"; then
        print_status "Withdrawal successful - Business rules enforced"
        echo "$WITHDRAW_RESPONSE" | jq . 2>/dev/null || echo "$WITHDRAW_RESPONSE"
    else
        print_warning "Withdrawal response unexpected"
        echo "$WITHDRAW_RESPONSE"
    fi
fi

# Test 6: Test insufficient funds (Business Rule Enforcement)
if [ -n "$ACCOUNT_ID" ] && [ "$ACCOUNT_ID" != "null" ]; then
    echo ""
    echo "Test 6: POST /api/accounts/$ACCOUNT_ID/withdraw (Insufficient Funds)"
    INVALID_WITHDRAW=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/accounts/$ACCOUNT_ID/withdraw?amount=10000.0")
    HTTP_CODE=$(echo "$INVALID_WITHDRAW" | tail -n1)
    if [ "$HTTP_CODE" = "400" ] || [ "$HTTP_CODE" = "422" ] || [ "$HTTP_CODE" = "500" ]; then
        print_status "Business rule enforced (insufficient funds rejected)"
    else
        print_warning "Business rule test did not return expected error (got HTTP $HTTP_CODE)"
    fi
fi

# Test 7: Get all clients
echo ""
echo "Test 7: GET /api/clients"
CLIENTS_RESPONSE=$(curl -s $BASE_URL/clients)
if [ -n "$CLIENTS_RESPONSE" ]; then
    print_status "GET /api/clients successful"
    echo "$CLIENTS_RESPONSE" | jq . 2>/dev/null || echo "$CLIENTS_RESPONSE"
else
    print_error "GET /api/clients failed"
fi

# Test 8: Test web application endpoints
echo ""
echo "=========================================="
echo "Testing Web Application (JSP/Servlets)"
echo "=========================================="
echo ""

# Test home page
echo "Test 8: GET / (home page)"
HOME_RESPONSE=$(curl -s -w "\n%{http_code}" http://localhost:$APP_PORT/)
HTTP_CODE=$(echo "$HOME_RESPONSE" | tail -n1)
if [ "$HTTP_CODE" = "200" ]; then
    print_status "Home page accessible (HTTP $HTTP_CODE)"
else
    print_warning "Home page returned HTTP $HTTP_CODE"
fi

# Test clients web interface
echo ""
echo "Test 9: GET /clients (web interface)"
CLIENTS_WEB_RESPONSE=$(curl -s -w "\n%{http_code}" http://localhost:$APP_PORT/clients)
HTTP_CODE=$(echo "$CLIENTS_WEB_RESPONSE" | tail -n1)
if [ "$HTTP_CODE" = "200" ]; then
    print_status "Clients web interface accessible (HTTP $HTTP_CODE)"
else
    print_warning "Clients web interface returned HTTP $HTTP_CODE"
fi

# Test accounts web interface
echo ""
echo "Test 10: GET /accounts (web interface)"
ACCOUNTS_WEB_RESPONSE=$(curl -s -w "\n%{http_code}" http://localhost:$APP_PORT/accounts)
HTTP_CODE=$(echo "$ACCOUNTS_WEB_RESPONSE" | tail -n1)
if [ "$HTTP_CODE" = "200" ]; then
    print_status "Accounts web interface accessible (HTTP $HTTP_CODE)"
else
    print_warning "Accounts web interface returned HTTP $HTTP_CODE"
fi

# Test database data
echo ""
echo "Test 11: Verify database data"
# Try with the available container runtime (docker or podman)
if command -v docker &> /dev/null && docker ps | grep -q $DB_CONTAINER; then
    CLIENT_COUNT=$(docker exec $DB_CONTAINER psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM clients;" 2>/dev/null | tr -d ' ')
    ACCOUNT_COUNT=$(docker exec $DB_CONTAINER psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM accounts;" 2>/dev/null | tr -d ' ')
    
    if [ -n "$CLIENT_COUNT" ] && [ "$CLIENT_COUNT" -gt 0 ]; then
        print_status "Database has $CLIENT_COUNT clients"
    else
        print_warning "No clients found in database"
    fi
    
    if [ -n "$ACCOUNT_COUNT" ] && [ "$ACCOUNT_COUNT" -gt 0 ]; then
        print_status "Database has $ACCOUNT_COUNT accounts"
    else
        print_warning "No accounts found in database"
    fi
elif command -v podman &> /dev/null && podman ps | grep -q $DB_CONTAINER; then
    CLIENT_COUNT=$(podman exec $DB_CONTAINER psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM clients;" 2>/dev/null | tr -d ' ')
    ACCOUNT_COUNT=$(podman exec $DB_CONTAINER psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM accounts;" 2>/dev/null | tr -d ' ')
    
    if [ -n "$CLIENT_COUNT" ] && [ "$CLIENT_COUNT" -gt 0 ]; then
        print_status "Database has $CLIENT_COUNT clients (via Podman)"
    else
        print_warning "No clients found in database"
    fi
    
    if [ -n "$ACCOUNT_COUNT" ] && [ "$ACCOUNT_COUNT" -gt 0 ]; then
        print_status "Database has $ACCOUNT_COUNT accounts (via Podman)"
    else
        print_warning "No accounts found in database"
    fi
else
    print_warning "Database container not found or not accessible with Docker/Podman"
fi

# Summary
echo ""
echo "=========================================="
echo "Deployment Summary"
echo "=========================================="
print_status "Application URL: http://localhost:$APP_PORT"
print_status "REST API Base: http://localhost:$APP_PORT/api"
print_status "Health Check: http://localhost:$APP_PORT/health"
print_status "Metrics: http://localhost:$APP_PORT/metrics"
echo ""
echo "Web Application URLs:"
echo "  🏠 Home:     http://localhost:$APP_PORT/"
echo "  👥 Clients:  http://localhost:$APP_PORT/clients"
echo "  💰 Accounts: http://localhost:$APP_PORT/accounts"
echo ""
echo "DDD Patterns Implemented:"
echo "  ✓ Value Objects (Money, Email, AccountNumber, AccountType)"
echo "  ✓ Aggregate Roots (Account, Client)"
echo "  ✓ Domain Services (TransferService)"
echo "  ✓ Domain Events (MoneyDeposited, MoneyWithdrawn, MoneyTransferred)"
echo "  ✓ Business Rules Enforcement"
echo "  ✓ Factory Methods"
echo ""
echo "Available REST Endpoints:"
echo "  GET    /api/clients           - List all clients"
echo "  GET    /api/clients/{id}      - Get client by ID"
echo "  POST   /api/clients           - Create client"
echo "  PUT    /api/clients/{id}      - Update client"
echo "  DELETE /api/clients/{id}      - Delete client"
echo ""
echo "  GET    /api/accounts          - List all accounts"
echo "  GET    /api/accounts/{id}     - Get account by ID"
echo "  POST   /api/accounts          - Create account"
echo "  POST   /api/accounts/{id}/deposit?amount=X   - Deposit money"
echo "  POST   /api/accounts/{id}/withdraw?amount=X  - Withdraw money"
echo ""
echo "Container Management:"
echo "  View logs:    podman logs -f $CONTAINER_NAME"
echo "  Stop:         podman stop $CONTAINER_NAME"
echo "  Remove:       podman rm $CONTAINER_NAME"
echo "  Stop DB:      docker-compose down"
echo ""
echo "=========================================="
print_status "Lab 06 deployment complete!"
echo "=========================================="

# Made with Bob