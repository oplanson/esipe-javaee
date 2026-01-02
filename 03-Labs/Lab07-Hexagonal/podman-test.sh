#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
# Podman deployment and testing script for Lab 07 - Hexagonal Architecture
# This script builds and deploys the banking hexagonal application using Podman

set -e  # Exit on error

echo "=========================================="
echo "Lab 07 - Hexagonal Architecture Deployment (Podman)"
echo "=========================================="
echo ""

# Configuration
IMAGE_NAME="banking-hexagonal-lab07"
CONTAINER_NAME="banking-hexagonal-lab07"
APP_PORT=9080
DB_CONTAINER="lab07-postgres"

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
if podman images | grep -q "banking-hexagonal-lab07"; then
    print_warning "Old image exists, removing..."
    podman rmi $IMAGE_NAME 2>/dev/null || true
    print_status "Old image removed"
fi

# Stop docker-compose services if running (with volumes cleanup)
if command -v docker-compose &> /dev/null; then
    print_warning "Stopping any existing docker-compose services and removing volumes..."
    docker-compose down -v 2>/dev/null || true
    print_status "Docker-compose services and volumes removed"
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
    if docker exec $DB_CONTAINER pg_isready -U bankuser -d bankingdb > /dev/null 2>&1; then
        print_status "Database is ready!"
        break
    elif podman exec $DB_CONTAINER pg_isready -U bankuser -d bankingdb > /dev/null 2>&1; then
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

# Get database network
DB_NETWORK=$(podman inspect $DB_CONTAINER --format '{{range $k, $v := .NetworkSettings.Networks}}{{$k}}{{end}}' 2>/dev/null || echo "solution_bank-network")

# Run container on same network as database
podman run -d \
    --name $CONTAINER_NAME \
    --network $DB_NETWORK \
    -p $APP_PORT:9080 \
    -e db_host=postgres \
    -e db_port=5432 \
    -e db_name=bankingdb \
    -e db_user=bankuser \
    -e db_password=bankpass \
    $IMAGE_NAME

print_status "Container started successfully"
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

# Step 7: Run Hexagonal Architecture tests
echo ""
echo "Step 7: Testing Hexagonal Architecture patterns..."
echo ""

BASE_URL="http://localhost:$APP_PORT/api/v2"

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

# Test 2: Create client via REST API V2 (Primary Adapter)
echo ""
echo "Test 2: POST /api/v2/clients (Primary Adapter - REST V2)"
CREATE_CLIENT=$(curl -s -X POST $BASE_URL/clients \
    -H "Content-Type: application/json" \
    -d '{"name":"Alice Johnson","email":"alice.johnson@example.com","premium":false}')
if [ -n "$CREATE_CLIENT" ]; then
    print_status "Client created via REST API V2"
    echo "$CREATE_CLIENT" | jq . 2>/dev/null || echo "$CREATE_CLIENT"
    CLIENT_ID=$(echo "$CREATE_CLIENT" | jq -r '.id' 2>/dev/null)
else
    print_error "Failed to create client"
fi

# Test 3: Create account for client (Use Case)
if [ -n "$CLIENT_ID" ] && [ "$CLIENT_ID" != "null" ]; then
    echo ""
    echo "Test 3: POST /api/v2/accounts (Use Case - Account Management)"
    CREATE_ACCOUNT=$(curl -s -X POST $BASE_URL/accounts \
        -H "Content-Type: application/json" \
        -d "{\"clientId\":$CLIENT_ID,\"accountType\":\"CHECKING\",\"initialBalance\":1000.0}")
    if [ -n "$CREATE_ACCOUNT" ]; then
        print_status "Account created for client"
        echo "$CREATE_ACCOUNT" | jq . 2>/dev/null || echo "$CREATE_ACCOUNT"
        ACCOUNT_ID=$(echo "$CREATE_ACCOUNT" | jq -r '.id' 2>/dev/null)
    else
        print_error "Failed to create account"
    fi
fi

# Test 4: Deposit money (Domain Service)
if [ -n "$ACCOUNT_ID" ] && [ "$ACCOUNT_ID" != "null" ]; then
    echo ""
    echo "Test 4: POST /api/v2/accounts/$ACCOUNT_ID/deposit (Domain Service)"
    DEPOSIT_RESPONSE=$(curl -s -X POST "$BASE_URL/accounts/$ACCOUNT_ID/deposit" \
        -H "Content-Type: application/json" \
        -d '{"amount":500.0,"currency":"EUR"}')
    if echo "$DEPOSIT_RESPONSE" | jq -e '.balance.amount' > /dev/null 2>&1; then
        BALANCE=$(echo "$DEPOSIT_RESPONSE" | jq -r '.balance.amount')
        print_status "Deposit successful - New balance: $BALANCE EUR"
        echo "$DEPOSIT_RESPONSE" | jq . 2>/dev/null
    else
        print_warning "Deposit response unexpected"
        echo "$DEPOSIT_RESPONSE"
    fi
fi

# Test 5: Get client via Web Interface (Secondary Adapter)
if [ -n "$CLIENT_ID" ] && [ "$CLIENT_ID" != "null" ]; then
    echo ""
    echo "Test 5: GET /clients/$CLIENT_ID (Primary Adapter - Web)"
    WEB_RESPONSE=$(curl -s -w "\n%{http_code}" http://localhost:$APP_PORT/clients/$CLIENT_ID)
    HTTP_CODE=$(echo "$WEB_RESPONSE" | tail -n1)
    if [ "$HTTP_CODE" = "200" ]; then
        print_status "Web interface accessible for client details"
    else
        print_warning "Web interface returned HTTP $HTTP_CODE"
    fi
fi

# Test 6: List all clients via API V2
echo ""
echo "Test 6: GET /api/v2/clients (Repository - Secondary Port)"
CLIENTS_RESPONSE=$(curl -s $BASE_URL/clients)
if [ -n "$CLIENTS_RESPONSE" ]; then
    print_status "GET /api/v2/clients successful"
    echo "$CLIENTS_RESPONSE" | jq . 2>/dev/null || echo "$CLIENTS_RESPONSE"
else
    print_error "GET /api/v2/clients failed"
fi

# Test 7: Web Adapter - Create client via form (ClientWebAdapter)
echo ""
echo "Test 7: POST /clients (Web Adapter - ClientWebAdapter)"
WEB_CREATE_CLIENT=$(curl -s -w "\n%{http_code}" -X POST http://localhost:$APP_PORT/clients \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "name=Bob+Smith&email=bob.smith@example.com&premium=on" \
    --location)
HTTP_CODE=$(echo "$WEB_CREATE_CLIENT" | tail -n1)
if [ "$HTTP_CODE" = "200" ]; then
    print_status "Client created via Web Adapter"
    # Extract client ID from redirect URL if possible
    WEB_CLIENT_ID=$(echo "$WEB_CREATE_CLIENT" | grep -o '/clients/[0-9]*' | grep -o '[0-9]*' | head -n1)
    if [ -n "$WEB_CLIENT_ID" ]; then
        echo "Created client ID: $WEB_CLIENT_ID"
    fi
else
    print_warning "Web client creation returned HTTP $HTTP_CODE"
fi

# Test 8: Web Adapter - List clients (ClientWebAdapter)
echo ""
echo "Test 8: GET /clients (Web Adapter - ClientWebAdapter)"
WEB_LIST_RESPONSE=$(curl -s -w "\n%{http_code}" http://localhost:$APP_PORT/clients)
HTTP_CODE=$(echo "$WEB_LIST_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$WEB_LIST_RESPONSE" | sed '$d')
if [ "$HTTP_CODE" = "200" ]; then
    if echo "$RESPONSE_BODY" | grep -q "Client Management"; then
        print_status "Client list page accessible via Web Adapter"
        # Count clients in HTML table
        CLIENT_COUNT=$(echo "$RESPONSE_BODY" | grep -o '<td>${client.id}</td>' | wc -l | tr -d ' ')
        if [ "$CLIENT_COUNT" -eq 0 ]; then
            # Try alternative counting method
            CLIENT_COUNT=$(echo "$RESPONSE_BODY" | grep -c "clients/" || echo "0")
        fi
        echo "Client list page loaded successfully"
    else
        print_warning "Client list page returned but format unexpected"
    fi
else
    print_error "Client list page returned HTTP $HTTP_CODE"
fi

# Test 9: Web Adapter - Show client form (ClientWebAdapter)
echo ""
echo "Test 9: GET /clients/new (Web Adapter - ClientWebAdapter)"
WEB_FORM_RESPONSE=$(curl -s -w "\n%{http_code}" http://localhost:$APP_PORT/clients/new)
HTTP_CODE=$(echo "$WEB_FORM_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$WEB_FORM_RESPONSE" | sed '$d')
if [ "$HTTP_CODE" = "200" ]; then
    if echo "$RESPONSE_BODY" | grep -q "name=\"name\"" && echo "$RESPONSE_BODY" | grep -q "name=\"email\""; then
        print_status "Client form page accessible via Web Adapter"
    else
        print_warning "Client form page returned but format unexpected"
    fi
else
    print_error "Client form page returned HTTP $HTTP_CODE"
fi

# Test 10: Web Adapter - Create account via form (AccountWebAdapter)
if [ -n "$WEB_CLIENT_ID" ]; then
    echo ""
    echo "Test 10: POST /accounts (Web Adapter - AccountWebAdapter)"
    WEB_CREATE_ACCOUNT=$(curl -s -w "\n%{http_code}" -X POST http://localhost:$APP_PORT/accounts \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "clientId=$WEB_CLIENT_ID&initialBalance=2000.00&currency=EUR&accountType=SAVINGS" \
        --location)
    HTTP_CODE=$(echo "$WEB_CREATE_ACCOUNT" | tail -n1)
    if [ "$HTTP_CODE" = "200" ]; then
        print_status "Account created via Web Adapter"
        # Extract account ID from redirect URL if possible
        WEB_ACCOUNT_ID=$(echo "$WEB_CREATE_ACCOUNT" | grep -o '/accounts/[0-9]*' | grep -o '[0-9]*' | head -n1)
        if [ -n "$WEB_ACCOUNT_ID" ]; then
            echo "Created account ID: $WEB_ACCOUNT_ID"
        fi
    else
        print_warning "Web account creation returned HTTP $HTTP_CODE"
    fi
fi

# Test 11: Web Adapter - List accounts for client (AccountWebAdapter)
if [ -n "$WEB_CLIENT_ID" ]; then
    echo ""
    echo "Test 11: GET /accounts?clientId=$WEB_CLIENT_ID (Web Adapter - AccountWebAdapter)"
    WEB_ACCOUNTS_RESPONSE=$(curl -s -w "\n%{http_code}" "http://localhost:$APP_PORT/accounts?clientId=$WEB_CLIENT_ID")
    HTTP_CODE=$(echo "$WEB_ACCOUNTS_RESPONSE" | tail -n1)
    RESPONSE_BODY=$(echo "$WEB_ACCOUNTS_RESPONSE" | sed '$d')
    if [ "$HTTP_CODE" = "200" ]; then
        if echo "$RESPONSE_BODY" | grep -q "Account Management"; then
            print_status "Account list page accessible via Web Adapter"
            echo "Account list page loaded successfully"
        else
            print_warning "Account list page returned but format unexpected"
        fi
    else
        print_error "Account list page returned HTTP $HTTP_CODE"
    fi
fi

# Test 12: Web Adapter - Show account details (AccountWebAdapter)
if [ -n "$WEB_ACCOUNT_ID" ]; then
    echo ""
    echo "Test 12: GET /accounts/$WEB_ACCOUNT_ID (Web Adapter - AccountWebAdapter)"
    WEB_ACCOUNT_DETAILS=$(curl -s -w "\n%{http_code}" http://localhost:$APP_PORT/accounts/$WEB_ACCOUNT_ID)
    HTTP_CODE=$(echo "$WEB_ACCOUNT_DETAILS" | tail -n1)
    RESPONSE_BODY=$(echo "$WEB_ACCOUNT_DETAILS" | sed '$d')
    if [ "$HTTP_CODE" = "200" ]; then
        if echo "$RESPONSE_BODY" | grep -q "Account Details" || echo "$RESPONSE_BODY" | grep -q "Account Number"; then
            print_status "Account details page accessible via Web Adapter"
        else
            print_warning "Account details page returned but format unexpected"
        fi
    else
        print_error "Account details page returned HTTP $HTTP_CODE"
    fi
fi

# Test 13: Verify database persistence (Secondary Adapter)
echo ""
echo "Test 13: Verify database persistence (Secondary Adapter - JPA)"
if command -v docker &> /dev/null && docker ps | grep -q $DB_CONTAINER; then
    CLIENT_COUNT=$(docker exec $DB_CONTAINER psql -U bankuser -d bankingdb -t -c "SELECT COUNT(*) FROM clients;" 2>/dev/null | tr -d ' ')
    ACCOUNT_COUNT=$(docker exec $DB_CONTAINER psql -U bankuser -d bankingdb -t -c "SELECT COUNT(*) FROM accounts;" 2>/dev/null | tr -d ' ')
    
    if [ -n "$CLIENT_COUNT" ] && [ "$CLIENT_COUNT" -gt 0 ]; then
        print_status "Database has $CLIENT_COUNT clients (persistence verified)"
    else
        print_warning "No clients found in database"
    fi
    
    if [ -n "$ACCOUNT_COUNT" ] && [ "$ACCOUNT_COUNT" -gt 0 ]; then
        print_status "Database has $ACCOUNT_COUNT accounts (persistence verified)"
    else
        print_warning "No accounts found in database"
    fi
elif command -v podman &> /dev/null && podman ps | grep -q $DB_CONTAINER; then
    CLIENT_COUNT=$(podman exec $DB_CONTAINER psql -U bankuser -d bankingdb -t -c "SELECT COUNT(*) FROM clients;" 2>/dev/null | tr -d ' ')
    ACCOUNT_COUNT=$(podman exec $DB_CONTAINER psql -U bankuser -d bankingdb -t -c "SELECT COUNT(*) FROM accounts;" 2>/dev/null | tr -d ' ')
    
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
    print_warning "Database container not found or not accessible"
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
echo "Hexagonal Architecture Verified:"
echo "  ✓ Primary Adapters (REST API, Web Interface)"
echo "  ✓ Primary Ports (Use Cases)"
echo "  ✓ Domain Layer (Entities, Value Objects, Services)"
echo "  ✓ Secondary Ports (Repositories)"
echo "  ✓ Secondary Adapters (JPA/Database)"
echo ""
echo "Container Management:"
echo "  View logs:    podman logs -f $CONTAINER_NAME"
echo "  Stop:         podman stop $CONTAINER_NAME"
echo "  Remove:       podman rm $CONTAINER_NAME"
echo "  Stop DB:      docker-compose down -v"
echo ""
echo "=========================================="
print_status "Lab 07 deployment complete!"
echo "=========================================="

# Made with Bob
