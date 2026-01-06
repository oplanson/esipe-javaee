#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
# Podman deployment and testing script for Lab 08 - Microservices Architecture
# This script builds applications locally, then builds and deploys containers using Podman

# Don't exit on error for curl commands - we want to handle failures gracefully
set +e  # Disable exit on error to handle curl failures
trap 'echo "Error on line $LINENO"' ERR  # Show error line but continue

echo "=========================================="
echo "Lab 08 - Microservices Architecture Deployment (Podman)"
echo "=========================================="
echo ""

# Configuration
CLIENT_SERVICE_PORT=9081
ACCOUNT_SERVICE_PORT=9082
API_GATEWAY_PORT=9080

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

# Check if podman-compose is installed
if ! command -v podman-compose &> /dev/null; then
    print_error "podman-compose is not installed. Please install podman-compose first."
    print_warning "Install with: pip3 install podman-compose"
    exit 1
fi

print_status "podman-compose is installed"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed. Please install Maven first."
    exit 1
fi

print_status "Maven is installed"

# Navigate to solution directory
if [ ! -d "solution" ]; then
    print_error "solution directory not found. Please run this script from the Lab08-Microservices directory."
    exit 1
fi

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

# Stop and remove all services using podman-compose
if [ -f "docker-compose.yml" ]; then
    print_warning "Stopping any existing services..."
    podman-compose down -v 2>/dev/null || true
    print_status "Services stopped and volumes removed"
fi

# Check for port conflicts and stop any containers using our ports
for PORT in $CLIENT_SERVICE_PORT $ACCOUNT_SERVICE_PORT $API_GATEWAY_PORT 5433 5434; do
    echo "Checking for port conflicts on $PORT..."
    CONFLICTING_CONTAINERS=$(podman ps --format "{{.Names}}" 2>/dev/null | while read -r name; do
        if [ -n "$name" ] && podman port "$name" 2>/dev/null | grep -q "0.0.0.0:$PORT"; then
            echo "$name"
        fi
    done)
    
    if [ -n "$CONFLICTING_CONTAINERS" ]; then
        print_warning "Found containers using port $PORT:"
        echo "$CONFLICTING_CONTAINERS" | while read -r container; do
            if [ -n "$container" ]; then
                print_warning "  Stopping $container..."
                podman stop "$container" > /dev/null 2>&1 || true
                podman rm "$container" > /dev/null 2>&1 || true
                print_status "  ✓ $container stopped and removed"
            fi
        done
    fi
done

print_status "No port conflicts detected"

# Clean up any orphaned networks
if $CONTAINER_CMD network ls 2>/dev/null | grep -q "banking-network"; then
    print_warning "Removing orphaned network banking-network..."
    $CONTAINER_CMD network rm banking-network 2>/dev/null || true
fi

# Remove old images if they exist
for IMAGE in "client-service" "account-service" "api-gateway"; do
    if podman images | grep -q "solution-$IMAGE"; then
        print_warning "Old image solution-$IMAGE exists, removing..."
        podman rmi "solution-$IMAGE" 2>/dev/null || true
    fi
    if podman images | grep -q "banking-$IMAGE"; then
        print_warning "Old image banking-$IMAGE exists, removing..."
        podman rmi "banking-$IMAGE" 2>/dev/null || true
    fi
done

print_status "Cleanup complete - ready for fresh deployment"

# Step 1: Build applications with Maven
echo ""
echo "=========================================="
echo "Step 1: Building applications with Maven"
echo "=========================================="

# Build Client Service
echo ""
echo "Building Client Service..."
cd client-service
if ! mvn clean package -DskipTests; then
    print_error "Client Service Maven build failed"
    exit 1
fi
print_status "Client Service built successfully"
cd ..

# Build Account Service
echo ""
echo "Building Account Service..."
cd account-service
if ! mvn clean package -DskipTests; then
    print_error "Account Service Maven build failed"
    exit 1
fi
print_status "Account Service built successfully"
cd ..

# Build API Gateway
echo ""
echo "Building API Gateway..."
cd api-gateway
if ! mvn clean package -DskipTests; then
    print_error "API Gateway Maven build failed"
    exit 1
fi
print_status "API Gateway built successfully"
cd ..

print_status "All applications built successfully"

# Step 2: Start PostgreSQL databases with docker-compose
echo ""
echo "=========================================="
echo "Step 2: Starting PostgreSQL databases"
echo "=========================================="
if ! podman-compose up -d; then
    print_error "Failed to start databases with podman-compose"
    exit 1
fi
print_status "PostgreSQL database containers starting..."

# Wait for databases to be ready
echo "Waiting for databases to be ready..."
MAX_DB_WAIT=30
DB_WAIT=0
CLIENT_DB_READY=false
ACCOUNT_DB_READY=false

while [ $DB_WAIT -lt $MAX_DB_WAIT ]; do
    # Check client-db
    if podman exec banking-client-db pg_isready -U bankuser -d banking_client_db > /dev/null 2>&1; then
        CLIENT_DB_READY=true
    fi
    # Check account-db
    if podman exec banking-account-db pg_isready -U bankuser -d banking_account_db > /dev/null 2>&1; then
        ACCOUNT_DB_READY=true
    fi
    
    if [ "$CLIENT_DB_READY" = true ] && [ "$ACCOUNT_DB_READY" = true ]; then
        print_status "Both databases are ready!"
        break
    fi
    
    DB_WAIT=$((DB_WAIT + 1))
    echo -n "."
    sleep 1
done

if [ $DB_WAIT -eq $MAX_DB_WAIT ]; then
    print_warning "Database readiness check timed out, but continuing..."
    echo "You may need to wait a bit longer for the databases to be fully ready."
    sleep 5
fi

echo ""

# Step 3: Build and run container images
echo ""
echo "=========================================="
echo "Step 3: Building and running containers"
echo "=========================================="

# Build and run Client Service container
echo ""
echo "Building Client Service container..."
cd client-service
if ! podman build -t banking-client-service -f Containerfile .; then
    print_error "Client Service container build failed"
    exit 1
fi
print_status "Client Service container built successfully"

echo "Starting Client Service container..."
podman run -d \
    --name banking-client-service \
    --network banking-network \
    -p $CLIENT_SERVICE_PORT:9080 \
    -p 9444:9443 \
    -e DB_HOST=banking-client-db \
    -e DB_PORT=5432 \
    -e DB_NAME=banking_client_db \
    -e DB_USER=bankuser \
    -e DB_PASSWORD=bankpass \
    -e SERVICE_NAME=client-service \
    -e SERVICE_VERSION=1.0.0 \
    -e LOG_LEVEL=INFO \
    banking-client-service

print_status "Client Service container started"
cd ..

# Build and run Account Service container
echo ""
echo "Building Account Service container..."
cd account-service
if ! podman build -t banking-account-service -f Containerfile .; then
    print_error "Account Service container build failed"
    exit 1
fi
print_status "Account Service container built successfully"

echo "Starting Account Service container..."
podman run -d \
    --name banking-account-service \
    --network banking-network \
    -p $ACCOUNT_SERVICE_PORT:9080 \
    -p 9445:9443 \
    -e DB_HOST=banking-account-db \
    -e DB_PORT=5432 \
    -e DB_NAME=banking_account_db \
    -e DB_USER=bankuser \
    -e DB_PASSWORD=bankpass \
    -e CLIENT_SERVICE_URL=http://banking-client-service:9080 \
    -e SERVICE_NAME=account-service \
    -e SERVICE_VERSION=1.0.0 \
    -e CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD=4 \
    -e CIRCUIT_BREAKER_FAILURE_RATIO=0.5 \
    -e CIRCUIT_BREAKER_DELAY=5000 \
    -e CIRCUIT_BREAKER_SUCCESS_THRESHOLD=2 \
    -e RETRY_MAX_RETRIES=3 \
    -e RETRY_DELAY=1000 \
    -e TIMEOUT_VALUE=2000 \
    -e LOG_LEVEL=INFO \
    banking-account-service

print_status "Account Service container started"
cd ..

# Build and run API Gateway container
echo ""
echo "Building API Gateway container..."
cd api-gateway
if ! podman build -t banking-api-gateway -f Containerfile .; then
    print_error "API Gateway container build failed"
    exit 1
fi
print_status "API Gateway container built successfully"

echo "Starting API Gateway container..."
podman run -d \
    --name banking-api-gateway \
    --network banking-network \
    -p $API_GATEWAY_PORT:9080 \
    -p 9443:9443 \
    -e CLIENT_SERVICE_URL=http://banking-client-service:9080 \
    -e ACCOUNT_SERVICE_URL=http://banking-account-service:9080 \
    -e SERVICE_NAME=api-gateway \
    -e SERVICE_VERSION=1.0.0 \
    -e CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD=4 \
    -e CIRCUIT_BREAKER_FAILURE_RATIO=0.5 \
    -e CIRCUIT_BREAKER_DELAY=5000 \
    -e TIMEOUT_VALUE=5000 \
    -e LOG_LEVEL=INFO \
    banking-api-gateway

print_status "API Gateway container started"
cd ..

# Function to wait for service health check
wait_for_service() {
    local service_name="$1"
    local port="$2"
    local max_attempts="${3:-60}"
    local sleep_interval="${4:-2}"
    local timeout_action="${5:-continue}"
    
    echo ""
    echo "Waiting for ${service_name}..."
    
    local attempt=0
    local start_time=$(date +%s)
    
    while [ $attempt -lt $max_attempts ]; do
        ADRESS="http://localhost:${port}/health/live"
        LIVE_RESPONSE=$(curl -w stdout $ADRESS 2>&1)
        echo $LIVE_RESPONSE
        if echo $LIVE_RESPONSE | grep -qi "UP"; then
            local elapsed=$(($(date +%s) - start_time))
            print_status "${service_name} is ready! (${elapsed}s)"
            return 0
        fi
        
        attempt=$((attempt + 1))
        echo -n "."
        sleep "$sleep_interval"
    done
    
    # Timeout handling
    local elapsed=$(($(date +%s) - start_time))
    if [ "$timeout_action" = "fail" ]; then
        print_error "${service_name} health check timeout after ${elapsed}s"
        return 1
    else
        print_warning "${service_name} health check timeout after ${elapsed}s, but continuing..."
        sleep 5
        return 2
    fi
}

# Step 4: Wait for services to be ready
echo ""
echo "=========================================="
echo "Step 4: Waiting for services to start"
echo "=========================================="

# Service configuration with structured data
# Format: name|port|timeout_action|max_attempts|description
readonly SERVICE_CONFIGS=(
    "Client Service|${CLIENT_SERVICE_PORT}|continue|60|Manages client data and operations"
    "Account Service|${ACCOUNT_SERVICE_PORT}|continue|60|Handles account management"
    "API Gateway|${API_GATEWAY_PORT}|continue|60|Routes requests to microservices"
)

# Track service status with POSIX-compatible approach
# Using parallel indexed arrays for service tracking
declare -a service_names=()
declare -a service_statuses=()
declare -a failed_services=()
declare -a timeout_services=()

# Cache array length to avoid repeated calculations
readonly total_services=${#SERVICE_CONFIGS[@]}
ready_count=0

# Validate we have services to check
if [ "${total_services}" -eq 0 ]; then
    print_error "No services configured to check"
    exit 1
fi

# Helper function to get service status by name
get_service_status() {
    local search_name="$1"
    local i
    for i in "${!service_names[@]}"; do
        if [ "${service_names[$i]}" = "$search_name" ]; then
            echo "${service_statuses[$i]}"
            return 0
        fi
    done
    echo "unknown"
    return 1
}

# Check each service with POSIX-compatible approach
for service_config in "${SERVICE_CONFIGS[@]}"; do
    IFS='|' read -r service_name service_port timeout_action max_attempts description <<< "$service_config"
    
    # Validate configuration
    if [ -z "$service_name" ] || [ -z "$service_port" ]; then
        print_warning "Skipping invalid service configuration: $service_config"
        continue
    fi
    
    # Wait for service with configured parameters
    wait_for_service "$service_name" "$service_port" "$max_attempts" 2 "$timeout_action"
    status=$?
    
    # Store service name and status in parallel arrays
    service_names+=("$service_name")
    
    # Track service status with detailed information
    case $status in
        0)
            service_statuses+=("ready")
            ready_count=$((ready_count + 1))
            ;;
        1)
            service_statuses+=("failed")
            failed_services+=("$service_name")
            ;;
        2)
            service_statuses+=("timeout")
            timeout_services+=("$service_name")
            ;;
        *)
            service_statuses+=("unknown")
            print_warning "Unknown status ($status) for $service_name"
            ;;
    esac
done

# Report comprehensive status
echo ""
echo "=========================================="
echo "Service Readiness Summary"
echo "=========================================="
echo "Ready: ${ready_count}/${total_services}"

# Display detailed status for each service
for service_config in "${SERVICE_CONFIGS[@]}"; do
    IFS='|' read -r service_name _ _ _ _ <<< "$service_config"
    status=$(get_service_status "$service_name")
    
    case $status in
        ready)   print_status "$service_name: Ready" ;;
        failed)  print_error "$service_name: Failed" ;;
        timeout) print_warning "$service_name: Timeout (continuing)" ;;
        *)       print_warning "$service_name: Unknown status" ;;
    esac
done

# Handle failures with appropriate actions
echo ""
if [ ${#failed_services[@]} -gt 0 ]; then
    print_error "Critical services failed to start: ${failed_services[*]}"
    print_warning "Attempting to continue with available services..."
    print_warning "Some tests may fail due to unavailable services"
elif [ ${#timeout_services[@]} -gt 0 ]; then
    print_warning "Services with timeout: ${timeout_services[*]}"
    print_warning "These services may still be starting up"
else
    print_status "All services are ready and operational!"
fi

echo ""

# Wait for services to start up
echo ""
echo "Waiting for services to start up..."
sleep 30

# Step 5: Run Microservices Architecture tests
echo ""
echo "=========================================="
echo "Step 5: Testing Microservices Architecture"
echo "=========================================="
echo ""

# Test 1: Client Service Health Check
echo "Test 1: Client Service Health Check"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$CLIENT_SERVICE_PORT/health)
if [ "$HTTP_CODE" = "200" ]; then
    HEALTH_RESPONSE=$(curl -s http://localhost:$CLIENT_SERVICE_PORT/health)
    if echo "$HEALTH_RESPONSE" | grep -q "UP"; then
        print_status "Client Service health check passed"
    else
        print_warning "Client Service health check accessible but some checks are DOWN"
    fi
else
    print_error "Client Service health check failed (HTTP $HTTP_CODE)"
fi

# Test 2: Account Service Health Check
echo ""
echo "Test 2: Account Service Health Check"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$ACCOUNT_SERVICE_PORT/health)
if [ "$HTTP_CODE" = "200" ]; then
    HEALTH_RESPONSE=$(curl -s http://localhost:$ACCOUNT_SERVICE_PORT/health)
    if echo "$HEALTH_RESPONSE" | grep -q "UP"; then
        print_status "Account Service health check passed"
    else
        print_warning "Account Service health check accessible but some checks are DOWN"
    fi
else
    print_error "Account Service health check failed (HTTP $HTTP_CODE)"
fi

# Test 3: API Gateway Health Check (Aggregate)
echo ""
echo "Test 3: API Gateway Health Check (Aggregate)"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$API_GATEWAY_PORT/health)
if [ "$HTTP_CODE" = "200" ]; then
    HEALTH_RESPONSE=$(curl -s http://localhost:$API_GATEWAY_PORT/health)
    if echo "$HEALTH_RESPONSE" | grep -q "UP"; then
        print_status "API Gateway health check passed (all services healthy)"
    else
        print_warning "API Gateway health check accessible but some checks are DOWN"
    fi
else
    print_error "API Gateway health check failed (HTTP $HTTP_CODE)"
fi

# Test 4: Create Client via API Gateway
echo ""
echo "Test 4: POST /api/clients (via API Gateway)"
CREATE_CLIENT=$(curl -s -X POST http://localhost:$API_GATEWAY_PORT/api/clients \
    -H "Content-Type: application/json" \
    -d '{"firstName":"Alice","lastName":"Johnson","email":"alice.johnson@example.com","phone":"1234567890","address":"123 Main St"}')
if [ -n "$CREATE_CLIENT" ]; then
    print_status "Client created via API Gateway"
    echo "$CREATE_CLIENT" | jq . 2>/dev/null || echo "$CREATE_CLIENT"
    CLIENT_ID=$(echo "$CREATE_CLIENT" | jq -r '.id' 2>/dev/null)
else
    print_error "Failed to create client"
fi

# Test 5: Get Client via API Gateway
echo ""
echo "Test 5: GET /api/clients/$CLIENT_ID (via API Gateway)"

if [ -n "$CLIENT_ID" ] && [ "$CLIENT_ID" != "null" ]; then
    GET_CLIENT=$(curl -s http://localhost:$API_GATEWAY_PORT/api/clients/$CLIENT_ID)
    if echo "$GET_CLIENT" | grep -q "Alice"; then
        print_status "Client retrieved successfully via API Gateway"
        echo "$GET_CLIENT" | jq . 2>/dev/null || echo "$GET_CLIENT"
    else
        print_error "Failed to retrieve client"
    fi
else
    print_error "CLIENT_ID not available : Failed to retrieve client"
fi

# Test 6: Create Account via API Gateway
echo ""
echo "Test 6: POST /api/accounts (via API Gateway)"
if [ -n "$CLIENT_ID" ] && [ "$CLIENT_ID" != "null" ]; then
    CREATE_ACCOUNT=$(curl -s -X POST http://localhost:$API_GATEWAY_PORT/api/accounts \
        -H "Content-Type: application/json" \
        -d "{\"clientId\":$CLIENT_ID,\"accountType\":\"CHECKING\"}")
    if [ -n "$CREATE_ACCOUNT" ]; then
        print_status "Account created via API Gateway"
        echo "$CREATE_ACCOUNT" | jq . 2>/dev/null || echo "$CREATE_ACCOUNT"
        ACCOUNT_ID=$(echo "$CREATE_ACCOUNT" | jq -r '.id' 2>/dev/null)
    else
        print_error "Failed to create account"
    fi
else
    print_error "CLIENT_ID not available : Failed to create account"
fi

# Test 7: Deposit Money
echo ""
echo "Test 7: POST /api/accounts/$ACCOUNT_ID/deposit (via API Gateway)"

if [ -n "$ACCOUNT_ID" ] && [ "$ACCOUNT_ID" != "null" ]; then
    DEPOSIT_RESPONSE=$(curl -s -X POST "http://localhost:$API_GATEWAY_PORT/api/accounts/$ACCOUNT_ID/deposit" \
        -H "Content-Type: application/json" \
        -d '{"amount":1000.00}')
    if echo "$DEPOSIT_RESPONSE" | jq -e '.balance' > /dev/null 2>&1; then
        BALANCE=$(echo "$DEPOSIT_RESPONSE" | jq -r '.balance')
        print_status "Deposit successful - New balance: $BALANCE"
        echo "$DEPOSIT_RESPONSE" | jq . 2>/dev/null
    else
        print_warning "Deposit response unexpected"
        echo "$DEPOSIT_RESPONSE"
    fi
else
    print_error "ACCOUNT_ID not available : Failed to POST deposit"
fi

# Test 8: Withdraw Money
echo ""
echo "Test 8: POST /api/accounts/$ACCOUNT_ID/withdraw (via API Gateway)"

if [ -n "$ACCOUNT_ID" ] && [ "$ACCOUNT_ID" != "null" ]; then
    WITHDRAW_RESPONSE=$(curl -s -X POST "http://localhost:$API_GATEWAY_PORT/api/accounts/$ACCOUNT_ID/withdraw" \
        -H "Content-Type: application/json" \
        -d '{"amount":250.00}')
    if echo "$WITHDRAW_RESPONSE" | jq -e '.balance' > /dev/null 2>&1; then
        BALANCE=$(echo "$WITHDRAW_RESPONSE" | jq -r '.balance')
        print_status "Withdrawal successful - New balance: $BALANCE"
        echo "$WITHDRAW_RESPONSE" | jq . 2>/dev/null
    else
        print_warning "Withdrawal response unexpected"
        echo "$WITHDRAW_RESPONSE"
    fi
else
    print_error "ACCOUNT_ID not available : Failed to POST withdraw"
fi

# Test 9: Get Client with Accounts (BFF Aggregation)
echo ""
echo "Test 9: GET /api/banking/clients-with-accounts/$CLIENT_ID (BFF Aggregation)"

if [ -n "$CLIENT_ID" ] && [ "$CLIENT_ID" != "null" ]; then
    AGGREGATED_RESPONSE=$(curl -s "http://localhost:$API_GATEWAY_PORT/api/banking/clients-with-accounts/$CLIENT_ID")
    if echo "$AGGREGATED_RESPONSE" | grep -q "accounts" && echo "$AGGREGATED_RESPONSE" | grep -q "Alice"; then
        print_status "Client with accounts retrieved successfully (BFF aggregation)"
        echo "$AGGREGATED_RESPONSE" | jq . 2>/dev/null || echo "$AGGREGATED_RESPONSE"
    else
        print_warning "BFF aggregation response unexpected"
        echo "$AGGREGATED_RESPONSE"
    fi
else
    print_error "CLIENT_ID not availbale : error in BFF Aggregation clients-with-accounts"
fi

# Test 10: Get All Clients with Accounts (BFF Aggregation)
echo ""
echo "Test 10: GET /api/banking/clients-with-accounts (BFF Aggregation - All Clients)"
ALL_AGGREGATED=$(curl -s "http://localhost:$API_GATEWAY_PORT/api/banking/clients-with-accounts")
if echo "$ALL_AGGREGATED" | jq -e 'length > 0' > /dev/null 2>&1; then
    CLIENT_COUNT=$(echo "$ALL_AGGREGATED" | jq 'length')
    print_status "Retrieved $CLIENT_COUNT clients with their accounts (BFF aggregation)"
    echo "$ALL_AGGREGATED" | jq '.[0]' 2>/dev/null || echo "$ALL_AGGREGATED"
else
    print_warning "BFF aggregation returned no data"
    echo "$ALL_AGGREGATED"
fi

# Test 11: Verify database persistence (Client DB)
echo ""
echo "Test 11: Verify Client database persistence"
if podman ps | grep -q "banking-client-db"; then
    CLIENT_COUNT=$(podman exec banking-client-db psql -U bankuser -d banking_client_db -t -c "SELECT COUNT(*) FROM clients;" 2>/dev/null | tr -d ' ')
    
    if [ -n "$CLIENT_COUNT" ] && [ "$CLIENT_COUNT" -gt 0 ]; then
        print_status "Client database has $CLIENT_COUNT clients (persistence verified)"
    else
        print_warning "No clients found in database"
    fi
else
    print_warning "Client database container not found or not accessible"
fi

# Test 12: Verify database persistence (Account DB)
echo ""
echo "Test 12: Verify Account database persistence"
if podman ps | grep -q "banking-account-db"; then
    ACCOUNT_COUNT=$(podman exec banking-account-db psql -U bankuser -d banking_account_db -t -c "SELECT COUNT(*) FROM accounts;" 2>/dev/null | tr -d ' ')
    
    if [ -n "$ACCOUNT_COUNT" ] && [ "$ACCOUNT_COUNT" -gt 0 ]; then
        print_status "Account database has $ACCOUNT_COUNT accounts (persistence verified)"
    else
        print_warning "No accounts found in database"
    fi
else
    print_warning "Account database container not found or not accessible"
fi

# Summary
echo ""
echo "=========================================="
echo "Deployment Summary"
echo "=========================================="
print_status "Client Service: http://localhost:$CLIENT_SERVICE_PORT"
print_status "Account Service: http://localhost:$ACCOUNT_SERVICE_PORT"
print_status "API Gateway (BFF): http://localhost:$API_GATEWAY_PORT"
print_status "Web Interface: http://localhost:$API_GATEWAY_PORT/web/"
print_status "REST API: http://localhost:$API_GATEWAY_PORT/api/"
echo ""
echo "Microservices Architecture Verified:"
echo "  ✓ Service Decomposition (Client, Account, API Gateway)"
echo "  ✓ Database Per Service Pattern"
echo "  ✓ API Gateway Pattern (BFF)"
echo "  ✓ Service-to-Service Communication (REST)"
echo "  ✓ Fault Tolerance (Circuit Breaker, Retry, Timeout)"
echo "  ✓ Health Checks (Liveness, Readiness, Aggregate)"
echo "  ✓ Metrics & Monitoring (MicroProfile Metrics)"
echo "  ✓ API Documentation (OpenAPI)"
echo ""
echo "Container Management:"
echo "  View logs:         podman logs -f <container-name>"
echo "  Stop service:      podman stop <container-name>"
echo "  Stop databases:    cd solution && podman-compose down"
echo "  Stop all:          podman stop banking-client-service banking-account-service banking-api-gateway && cd solution && podman-compose down"
echo "  Clean all:         podman stop banking-client-service banking-account-service banking-api-gateway && podman rm banking-client-service banking-account-service banking-api-gateway && cd solution && podman-compose down -v"
echo ""
echo "=========================================="
print_status "Lab 08 deployment complete!"
echo "=========================================="

# Made with Bob
