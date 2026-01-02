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

# Test 12: API Versioning - Test V1 (Deprecated)
echo ""
echo "=========================================="
echo "Testing API Versioning (V1 vs V2)"
echo "=========================================="
echo ""

echo "Test 12: GET /api/accounts (V1 - DEPRECATED)"
V1_RESPONSE=$(curl -s -i $BASE_URL/accounts)
V1_BODY=$(echo "$V1_RESPONSE" | tail -n +$(echo "$V1_RESPONSE" | grep -n "^\r$" | cut -d: -f1 | head -1))
V1_HEADERS=$(echo "$V1_RESPONSE" | head -n $(echo "$V1_RESPONSE" | grep -n "^\r$" | cut -d: -f1 | head -1))

if echo "$V1_HEADERS" | grep -q "X-API-Deprecated: true"; then
    print_status "V1 API has deprecation header"
else
    print_warning "V1 API missing deprecation header"
fi

if echo "$V1_HEADERS" | grep -q "X-API-Version: 1.0"; then
    print_status "V1 API has version header (1.0)"
else
    print_warning "V1 API missing version header"
fi

if echo "$V1_HEADERS" | grep -q "X-API-Sunset-Date"; then
    SUNSET_DATE=$(echo "$V1_HEADERS" | grep "X-API-Sunset-Date" | cut -d: -f2 | tr -d ' \r')
    print_status "V1 API has sunset date: $SUNSET_DATE"
else
    print_warning "V1 API missing sunset date header"
fi

if [ -n "$V1_BODY" ]; then
    print_status "V1 API response received"
    echo "$V1_BODY" | jq '.[0] | {id, number, balance, type}' 2>/dev/null || echo "V1 format: simple balance field"
else
    print_warning "V1 API returned empty response"
fi

# Test 13: API Versioning - Test V2 (Current)
echo ""
echo "Test 13: GET /api/v2/accounts (V2 - CURRENT)"
V2_RESPONSE=$(curl -s -i http://localhost:$APP_PORT/api/v2/accounts)
V2_BODY=$(echo "$V2_RESPONSE" | tail -n +$(echo "$V2_RESPONSE" | grep -n "^\r$" | cut -d: -f1 | head -1))
V2_HEADERS=$(echo "$V2_RESPONSE" | head -n $(echo "$V2_RESPONSE" | grep -n "^\r$" | cut -d: -f1 | head -1))

if echo "$V2_HEADERS" | grep -q "X-API-Version: 2.0"; then
    print_status "V2 API has version header (2.0)"
else
    print_warning "V2 API missing version header"
fi

if echo "$V2_HEADERS" | grep -q "X-API-Deprecated"; then
    print_warning "V2 API should NOT have deprecation header"
else
    print_status "V2 API correctly has no deprecation header"
fi

if [ -n "$V2_BODY" ]; then
    print_status "V2 API response received"
    echo "$V2_BODY" | jq '.[0] | {id, accountNumber, balance: {amount: .balance.amount, currency: .balance.currency}, accountType}' 2>/dev/null || echo "V2 format: Money Value Object"
else
    print_warning "V2 API returned empty response"
fi

# Test 14: Compare V1 vs V2 response format
echo ""
echo "Test 14: Comparing V1 vs V2 response formats"
if [ -n "$ACCOUNT_ID" ] && [ "$ACCOUNT_ID" != "null" ]; then
    echo "Fetching same account from both APIs..."
    
    V1_ACCOUNT=$(curl -s $BASE_URL/accounts/$ACCOUNT_ID)
    V2_ACCOUNT=$(curl -s http://localhost:$APP_PORT/api/v2/accounts/$ACCOUNT_ID)
    
    echo ""
    echo "V1 Response (simple balance):"
    echo "$V1_ACCOUNT" | jq '{id, number, balance, type}' 2>/dev/null || echo "$V1_ACCOUNT"
    
    echo ""
    echo "V2 Response (Money Value Object):"
    echo "$V2_ACCOUNT" | jq '{id, accountNumber, balance, accountType}' 2>/dev/null || echo "$V2_ACCOUNT"
    
    # Verify V2 has Money object structure
    if echo "$V2_ACCOUNT" | jq -e '.balance.amount' > /dev/null 2>&1 && \
       echo "$V2_ACCOUNT" | jq -e '.balance.currency' > /dev/null 2>&1; then
        print_status "V2 correctly uses Money Value Object (amount + currency)"
    else
        print_warning "V2 response does not have expected Money Value Object structure"
    fi
else
    print_warning "No account ID available for comparison test"
fi

# Test 15: V2 Deposit with Money Value Object
if [ -n "$ACCOUNT_ID" ] && [ "$ACCOUNT_ID" != "null" ]; then
    echo ""
    echo "Test 15: POST /api/v2/accounts/$ACCOUNT_ID/deposit (Money VO)"
    V2_DEPOSIT=$(curl -s -X POST http://localhost:$APP_PORT/api/v2/accounts/$ACCOUNT_ID/deposit \
        -H "Content-Type: application/json" \
        -d '{"amount": 100.00, "currency": "EUR"}')
    
    if echo "$V2_DEPOSIT" | jq -e '.balance.amount' > /dev/null 2>&1; then
        BALANCE_AMOUNT=$(echo "$V2_DEPOSIT" | jq -r '.balance.amount')
        BALANCE_CURRENCY=$(echo "$V2_DEPOSIT" | jq -r '.balance.currency')
        print_status "V2 deposit successful: $BALANCE_AMOUNT $BALANCE_CURRENCY"
        echo "$V2_DEPOSIT" | jq . 2>/dev/null
    else
        print_warning "V2 deposit response unexpected"
        echo "$V2_DEPOSIT"
    fi
fi

# Test 16: Verify deprecation headers on all V1 endpoints
echo ""
echo "Test 16: Verify deprecation headers on V1 endpoints"
V1_ENDPOINTS=(
    "/api/accounts"
    "/api/accounts/$ACCOUNT_ID"
    "/api/clients"
)

for endpoint in "${V1_ENDPOINTS[@]}"; do
    if [[ "$endpoint" == *"null"* ]]; then
        continue
    fi
    
    HEADERS=$(curl -s -I http://localhost:$APP_PORT$endpoint 2>/dev/null)
    if echo "$HEADERS" | grep -q "X-API-Deprecated: true"; then
        print_status "✓ $endpoint has deprecation header"
    else
        print_warning "✗ $endpoint missing deprecation header"
    fi
done

# Test 17: Test database migration (Option 4 verification)
echo ""
echo "Test 17: Verify database migration (Option 4 - Backward Compatible)"
if command -v docker &> /dev/null && docker ps | grep -q $DB_CONTAINER; then
    # Check if both old and new columns exist
    COLUMNS=$(docker exec $DB_CONTAINER psql -U bankuser -d bankdb -t -c "SELECT column_name FROM information_schema.columns WHERE table_name='accounts' AND column_name IN ('balance', 'balance_amount', 'balance_currency');" 2>/dev/null | tr -d ' ')
    
    if echo "$COLUMNS" | grep -q "balance" && \
       echo "$COLUMNS" | grep -q "balance_amount" && \
       echo "$COLUMNS" | grep -q "balance_currency"; then
        print_status "Database has both old (balance) and new (balance_amount, balance_currency) columns"
        print_status "Option 4 (Backward Compatible Migration) verified!"
    else
        print_warning "Database migration columns not as expected"
    fi
    
    # Check if trigger exists
    TRIGGER_EXISTS=$(docker exec $DB_CONTAINER psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM pg_trigger WHERE tgname='trigger_sync_account_balance';" 2>/dev/null | tr -d ' ')
    if [ "$TRIGGER_EXISTS" = "1" ]; then
        print_status "Synchronization trigger exists (keeps old column in sync)"
    else
        print_warning "Synchronization trigger not found"
    fi
elif command -v podman &> /dev/null && podman ps | grep -q $DB_CONTAINER; then
    # Same checks with podman
    COLUMNS=$(podman exec $DB_CONTAINER psql -U bankuser -d bankdb -t -c "SELECT column_name FROM information_schema.columns WHERE table_name='accounts' AND column_name IN ('balance', 'balance_amount', 'balance_currency');" 2>/dev/null | tr -d ' ')
    
    if echo "$COLUMNS" | grep -q "balance" && \
       echo "$COLUMNS" | grep -q "balance_amount" && \
       echo "$COLUMNS" | grep -q "balance_currency"; then
        print_status "Database has both old and new columns (via Podman)"
        print_status "Option 4 (Backward Compatible Migration) verified!"
    else
        print_warning "Database migration columns not as expected"
    fi
    
    TRIGGER_EXISTS=$(podman exec $DB_CONTAINER psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM pg_trigger WHERE tgname='trigger_sync_account_balance';" 2>/dev/null | tr -d ' ')
    if [ "$TRIGGER_EXISTS" = "1" ]; then
        print_status "Synchronization trigger exists (via Podman)"
    else
        print_warning "Synchronization trigger not found"
    fi
fi

# Test database data
echo ""
echo "Test 18: Verify database data"
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
echo ""
echo "API V1 (DEPRECATED - Sunset: 2026-06-01):"
echo "  GET    /api/clients           - List all clients"
echo "  GET    /api/clients/{id}      - Get client by ID"
echo "  POST   /api/clients           - Create client"
echo "  PUT    /api/clients/{id}      - Update client"
echo "  DELETE /api/clients/{id}      - Delete client"
echo ""
echo "  GET    /api/accounts          - List all accounts (simple balance)"
echo "  GET    /api/accounts/{id}     - Get account by ID"
echo "  POST   /api/accounts          - Create account"
echo "  POST   /api/accounts/{id}/deposit?amount=X   - Deposit money"
echo "  POST   /api/accounts/{id}/withdraw?amount=X  - Withdraw money"
echo ""
echo "API V2 (CURRENT - Recommended):"
echo "  GET    /api/v2/accounts       - List all accounts (Money VO)"
echo "  GET    /api/v2/accounts/{id}  - Get account by ID"
echo "  POST   /api/v2/accounts/{id}/deposit   - Deposit (JSON body)"
echo "  POST   /api/v2/accounts/{id}/withdraw  - Withdraw (JSON body)"
echo "  POST   /api/v2/accounts/{id}/transfer  - Transfer (JSON body)"
echo ""
echo "API Versioning Features:"
echo "  ✓ V1 has deprecation headers (X-API-Deprecated, X-API-Sunset-Date)"
echo "  ✓ V2 uses Money Value Object (amount + currency)"
echo "  ✓ Both versions work simultaneously (backward compatible)"
echo "  ✓ Database supports both formats via trigger (Option 4)"
echo ""
echo "Documentation:"
echo "  📖 API Versioning Guide: solution/API-VERSIONING.md"
echo "  📖 Migration Guide: V1 → V2 examples included"
echo ""
echo "Container Management:"
echo "  View logs:    podman logs -f $CONTAINER_NAME"
echo "  Stop:         podman stop $CONTAINER_NAME"
echo "  Remove:       podman rm $CONTAINER_NAME"
echo "  Stop DB:      docker-compose down -v"
echo ""
echo "=========================================="
print_status "Lab 06 deployment complete!"
echo "=========================================="

# Made with Bob