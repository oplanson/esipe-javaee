#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Podman test script for Lab 9 - Bank Security Application
# This script builds, deploys, and tests the application with Podman
# Runs 20 comprehensive security tests

# Note: Not using 'set -e' so all tests run even if some fail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
APP_NAME="bank-security-app"
DB_NAME="bank-security-db"
NETWORK_NAME="bank-security-network"
APP_PORT=9080
DB_PORT=5432
IMAGE_NAME="bank-security:latest"

# Test counters and results tracking
TESTS_PASSED=0
TESTS_FAILED=0
declare -a TEST_RESULTS
declare -a TEST_NAMES
TEST_NUMBER=0

echo "========================================="
echo "Lab 9: Bank Security Application - Podman Test"
echo "========================================="
echo ""

# Function to print colored output
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

print_step() {
    echo -e "${BLUE}▶ $1${NC}"
}

# Function to run a test with detailed tracking
run_test() {
    local test_name="$1"
    local test_command="$2"
    
    ((TEST_NUMBER++))
    TEST_NAMES[$TEST_NUMBER]="$test_name"
    
    echo -n "Test $TEST_NUMBER: $test_name... "
    if eval "$test_command" > /dev/null 2>&1; then
        print_success "PASSED"
        TEST_RESULTS[$TEST_NUMBER]="PASSED"
        ((TESTS_PASSED++))
        return 0
    else
        print_error "FAILED"
        TEST_RESULTS[$TEST_NUMBER]="FAILED"
        ((TESTS_FAILED++))
        return 1
    fi
}

# Function to cleanup existing containers and network (called manually at start)
cleanup_existing() {
    print_info "Cleaning up existing containers and network..."
    
    # Stop and remove containers if they exist
    if podman ps -a --format "{{.Names}}" | grep -q "^${APP_NAME}$"; then
        podman stop ${APP_NAME} 2>/dev/null || true
        podman rm ${APP_NAME} 2>/dev/null || true
    fi
    
    if podman ps -a --format "{{.Names}}" | grep -q "^${DB_NAME}$"; then
        podman stop ${DB_NAME} 2>/dev/null || true
        podman rm ${DB_NAME} 2>/dev/null || true
    fi
    
    # Remove network if it exists
    if podman network exists ${NETWORK_NAME} 2>/dev/null; then
        podman network rm ${NETWORK_NAME} 2>/dev/null || true
    fi
    
    # Remove old image if it exists
    if podman image exists ${IMAGE_NAME} 2>/dev/null; then
        podman rmi ${IMAGE_NAME} 2>/dev/null || true
    fi
}

# Note: No trap cleanup on exit - containers stay running for inspection

# Change to solution directory
cd "$(dirname "$0")/solution"

# Step 0: Check and cleanup existing containers
print_step "Step 0: Checking for existing containers..."

# Check if application container exists and is running
if podman ps 2>/dev/null | grep -q ${APP_NAME}; then
    print_info "Application container is running, stopping..."
    podman stop ${APP_NAME} 2>/dev/null || true
    print_success "Container stopped"
fi

# Check if application container exists (stopped)
if podman ps -a 2>/dev/null | grep -q ${APP_NAME}; then
    print_info "Application container exists, removing..."
    podman rm ${APP_NAME} 2>/dev/null || true
    print_success "Container removed"
fi

# Check if database container exists and is running
if podman ps 2>/dev/null | grep -q ${DB_NAME}; then
    print_info "Database container is running, stopping..."
    podman stop ${DB_NAME} 2>/dev/null || true
    print_success "Database container stopped"
fi

# Check if database container exists (stopped)
if podman ps -a 2>/dev/null | grep -q ${DB_NAME}; then
    print_info "Database container exists, removing..."
    podman rm ${DB_NAME} 2>/dev/null || true
    print_success "Database container removed"
fi

# Check for port conflicts - stop any container using the ports
print_info "Checking for port conflicts on ${APP_PORT} and ${DB_PORT}..."

# Check APP_PORT (9080)
CONFLICTING_APP=$(podman ps --format "{{.Names}}" | while read -r name; do
    if podman port "$name" 2>/dev/null | grep -q "0.0.0.0:${APP_PORT}"; then
        echo "$name"
    fi
done)

if [ -n "$CONFLICTING_APP" ]; then
    print_info "Found containers using port ${APP_PORT}:"
    echo "$CONFLICTING_APP" | while read -r container; do
        if [ -n "$container" ] && [ "$container" != "$APP_NAME" ]; then
            print_info "  Stopping $container..."
            podman stop "$container" > /dev/null 2>&1 || true
            podman rm "$container" > /dev/null 2>&1 || true
            print_success "  ✓ $container stopped and removed"
        fi
    done
fi

# Check DB_PORT (5432)
CONFLICTING_DB=$(podman ps --format "{{.Names}}" | while read -r name; do
    if podman port "$name" 2>/dev/null | grep -q "0.0.0.0:${DB_PORT}"; then
        echo "$name"
    fi
done)

if [ -n "$CONFLICTING_DB" ]; then
    print_info "Found containers using port ${DB_PORT}:"
    echo "$CONFLICTING_DB" | while read -r container; do
        if [ -n "$container" ] && [ "$container" != "$DB_NAME" ]; then
            print_info "  Stopping $container..."
            podman stop "$container" > /dev/null 2>&1 || true
            podman rm "$container" > /dev/null 2>&1 || true
            print_success "  ✓ $container stopped and removed"
        fi
    done
fi

if [ -z "$CONFLICTING_APP" ] && [ -z "$CONFLICTING_DB" ]; then
    print_success "No port conflicts detected"
fi

# Check if old image exists
if podman images | grep -q "${IMAGE_NAME}"; then
    print_info "Old image exists, removing..."
    podman rmi ${IMAGE_NAME} 2>/dev/null || true
    print_success "Old image removed"
fi

# Remove network if it exists
if podman network exists ${NETWORK_NAME} 2>/dev/null; then
    print_info "Removing existing network..."
    podman network rm ${NETWORK_NAME} 2>/dev/null || true
    print_success "Network removed"
fi

print_success "Cleanup completed"
echo ""

# Step 1: Build Maven project
print_step "Step 1: Building Maven project..."
if mvn clean package -DskipTests -q; then
    print_success "Maven build successful"
else
    print_error "Maven build failed"
    print_info "Continuing with tests anyway..."
fi

# Step 2: Create network
print_step "Step 2: Creating Podman network..."
if podman network create ${NETWORK_NAME} > /dev/null 2>&1; then
    print_success "Network created: ${NETWORK_NAME}"
else
    print_error "Failed to create network"
    print_info "Continuing with tests anyway..."
fi

# Step 3: Start PostgreSQL database
print_step "Step 3: Starting PostgreSQL database..."
podman run -d \
    --name ${DB_NAME} \
    --network ${NETWORK_NAME} \
    -e POSTGRES_DB=bankdb \
    -e POSTGRES_USER=bankuser \
    -e POSTGRES_PASSWORD=bankpass \
    -p ${DB_PORT}:5432 \
    postgres:16-alpine

print_info "Waiting for database to be ready..."
sleep 10

# Check if database is ready
for i in {1..30}; do
    if podman exec ${DB_NAME} pg_isready -U bankuser -d bankdb > /dev/null 2>&1; then
        print_success "Database is ready"
        break
    fi
    if [ $i -eq 30 ]; then
        print_error "Database failed to start"
        exit 1
    fi
    sleep 1
done

# Step 4: Build application image
print_step "Step 4: Building application image..."
if podman build -t ${IMAGE_NAME} -f Containerfile . > /dev/null 2>&1; then
    print_success "Image built successfully"
else
    print_error "Image build failed"
    print_info "Continuing with tests anyway..."
fi

# Step 5: Start application container
print_step "Step 5: Starting application container..."
podman run -d \
    --name ${APP_NAME} \
    --network ${NETWORK_NAME} \
    -e DB_HOST=${DB_NAME} \
    -e DB_PORT=5432 \
    -e DB_NAME=bankdb \
    -e DB_USER=bankuser \
    -e DB_PASSWORD=bankpass \
    -p ${APP_PORT}:9080 \
    ${IMAGE_NAME}

print_info "Waiting for application to start..."
sleep 15

# Wait for application to be ready
print_info "Checking application health..."
for i in {1..60}; do
    if curl -sf http://localhost:${APP_PORT}/health/live > /dev/null 2>&1; then
        print_success "Application is ready"
        break
    fi
    if [ $i -eq 60 ]; then
        print_error "Application failed to start"
        print_info "Checking application logs..."
        podman logs ${APP_NAME} | tail -50
        exit 1
    fi
    sleep 2
done

# Step 6: Run automated tests
print_step "Step 6: Running automated tests..."
echo ""

# Test 1: Health check
run_test "Health check (liveness)" \
    "curl -sf http://localhost:${APP_PORT}/health/live | grep -q '\"status\":\"UP\"'"

run_test "Health check (readiness)" \
    "curl -sf http://localhost:${APP_PORT}/health/ready | grep -q '\"status\":\"UP\"'"

# Test 3: Home page
run_test "Home page accessible" \
    "curl -sf http://localhost:${APP_PORT}/ | grep -q 'Bank Security'"

# Test 3: Register new user (CUSTOMER role by default)
print_info "Registering test user..."
REGISTER_RESPONSE=$(curl -sf -X POST http://localhost:${APP_PORT}/api/auth/register \
    -H "Content-Type: application/json" \
    -d '{"username":"testuser","email":"test@example.com","password":"Test@1234"}' 2>/dev/null || echo "")

run_test "User registration" \
    "echo '${REGISTER_RESPONSE}' | grep -q '\"token\"'"

# Extract JWT token from registration response
JWT_TOKEN=$(echo "${REGISTER_RESPONSE}" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -n "$JWT_TOKEN" ]; then
    print_info "JWT token obtained: ${JWT_TOKEN:0:20}..."
    
    # Test 4: Login with registered user
    LOGIN_RESPONSE=$(curl -sf -X POST http://localhost:${APP_PORT}/api/auth/login \
        -H "Content-Type: application/json" \
        -d '{"username":"testuser","password":"Test@1234"}' 2>/dev/null || echo "")
    
    run_test "User login" \
        "echo '${LOGIN_RESPONSE}' | grep -q '\"token\"'"
    
    # Test 5: Get current user info
    run_test "Get current user (/api/auth/me)" \
        "curl -sf http://localhost:${APP_PORT}/api/auth/me \
        -H 'Authorization: Bearer ${JWT_TOKEN}' | grep -q '\"username\":\"testuser\"'"
    
    # Test 6: Get my accounts (should be empty initially)
    run_test "Get my accounts" \
        "curl -sf http://localhost:${APP_PORT}/api/accounts/my \
        -H 'Authorization: Bearer ${JWT_TOKEN}' | grep -q '\[\]'"
    
    # Test 7: Try to access admin endpoint (should fail with 403)
    run_test "Access denied for CUSTOMER role" \
        "curl -s -o /dev/null -w '%{http_code}' http://localhost:${APP_PORT}/api/accounts \
        -H 'Authorization: Bearer ${JWT_TOKEN}' | grep -q '403'"
    
    # Test 8: Logout
    run_test "User logout" \
        "curl -sf -X POST http://localhost:${APP_PORT}/api/auth/logout \
        -H 'Authorization: Bearer ${JWT_TOKEN}' | grep -q 'Logout successful'"
fi

# Test 9: Failed login attempt
run_test "Failed login (wrong password)" \
    "curl -s -o /dev/null -w '%{http_code}' -X POST http://localhost:${APP_PORT}/api/auth/login \
    -H 'Content-Type: application/json' \
    -d '{\"username\":\"testuser\",\"password\":\"WrongPassword\"}' | grep -q '401'"

# Test 10: Access without token (should fail)
run_test "Access denied without token" \
    "curl -s -o /dev/null -w '%{http_code}' http://localhost:${APP_PORT}/api/accounts/my | grep -q '401'"

# Test 11: Database persistence check
print_info "Checking database persistence..."
USER_COUNT=$(podman exec ${DB_NAME} psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM users;" 2>/dev/null | tr -d ' ')
run_test "User persisted in database" \
    "test ${USER_COUNT} -ge 1"

AUDIT_COUNT=$(podman exec ${DB_NAME} psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM security_audit_logs;" 2>/dev/null | tr -d ' ')
run_test "Security audit logs persisted" \
    "test ${AUDIT_COUNT} -ge 1"

# Test 13: OpenAPI documentation
run_test "OpenAPI documentation available" \
    "curl -sf http://localhost:${APP_PORT}/openapi | grep -q 'openapi'"

# Test 14: Metrics endpoint
run_test "Metrics endpoint accessible" \
    "curl -sf http://localhost:${APP_PORT}/metrics | grep -q 'base'"

# Test 15: Register second user with different role
print_info "Registering admin user for advanced tests..."
ADMIN_REGISTER=$(curl -sf -X POST http://localhost:${APP_PORT}/api/auth/register \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","email":"admin@example.com","password":"Admin@1234"}' 2>/dev/null || echo "")

# Note: In production, admin role would be assigned through database or admin panel
# For testing, we verify CUSTOMER role restrictions work correctly

# Test 16: Try to create account with CUSTOMER role (should fail)
if [ -n "$JWT_TOKEN" ]; then
    run_test "CUSTOMER cannot create accounts" \
        "curl -s -o /dev/null -w '%{http_code}' -X POST http://localhost:${APP_PORT}/api/accounts \
        -H 'Authorization: Bearer ${JWT_TOKEN}' \
        -H 'Content-Type: application/json' \
        -d '{\"ownerUsername\":\"testuser\",\"balance\":1000,\"accountType\":\"CHECKING\"}' | grep -q '403'"
fi

# Test 17: Invalid JWT token
run_test "Invalid JWT token rejected" \
    "curl -s -o /dev/null -w '%{http_code}' http://localhost:${APP_PORT}/api/accounts/my \
    -H 'Authorization: Bearer invalid.token.here' | grep -q '401'"

# Test 18: CORS headers present
run_test "CORS headers present" \
    "curl -sI -H 'Origin: http://example.com' http://localhost:${APP_PORT}/api/auth/me | grep -q 'Access-Control'"

# Test 19: Content Security Policy header
run_test "CSP header present" \
    "curl -sI http://localhost:${APP_PORT}/ | grep -q 'Content-Security-Policy'"

# Test 20: Database tables created
print_info "Verifying database schema..."
TABLES_COUNT=$(podman exec ${DB_NAME} psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='public';" 2>/dev/null | tr -d ' ')
run_test "Database tables created" \
    "test ${TABLES_COUNT} -ge 3"

echo ""
echo "========================================="
echo "Test Summary"
echo "========================================="
echo -e "Tests Passed: ${GREEN}${TESTS_PASSED}${NC}"
echo -e "Tests Failed: ${RED}${TESTS_FAILED}${NC}"
echo "Total Tests: $((TESTS_PASSED + TESTS_FAILED))"
echo ""

# Detailed test results
echo "========================================="
echo "Detailed Test Results"
echo "========================================="
for i in $(seq 1 $TEST_NUMBER); do
    if [ "${TEST_RESULTS[$i]}" = "PASSED" ]; then
        echo -e "${GREEN}✓${NC} Test $i: ${TEST_NAMES[$i]} - ${GREEN}PASSED${NC}"
    else
        echo -e "${RED}✗${NC} Test $i: ${TEST_NAMES[$i]} - ${RED}FAILED${NC}"
    fi
done
echo ""

# Show application logs
print_step "Application Logs (last 30 lines):"
podman logs ${APP_NAME} 2>&1 | tail -30

echo ""
print_step "Container Status:"
podman ps --filter "name=${APP_NAME}" --filter "name=${DB_NAME}"

echo ""
if [ ${TESTS_FAILED} -eq 0 ]; then
    print_success "All tests passed! ✓"
    echo ""
    print_success "Containers are still running for your inspection:"
    echo ""
    print_info "Application URL: http://localhost:${APP_PORT}"
    print_info "API Documentation: http://localhost:${APP_PORT}/"
    echo ""
    print_info "API Endpoints:"
    echo "  - POST http://localhost:${APP_PORT}/api/auth/register"
    echo "  - POST http://localhost:${APP_PORT}/api/auth/login"
    echo "  - GET  http://localhost:${APP_PORT}/api/auth/me"
    echo "  - GET  http://localhost:${APP_PORT}/api/accounts/my"
    echo ""
    print_info "Database Access:"
    echo "  podman exec -it ${DB_NAME} psql -U bankuser -d bankdb"
    echo ""
    print_info "View Logs:"
    echo "  podman logs ${APP_NAME}"
    echo "  podman logs ${DB_NAME}"
    echo ""
    print_info "When done, cleanup with:"
    echo "  podman stop ${APP_NAME} ${DB_NAME}"
    echo "  podman rm ${APP_NAME} ${DB_NAME}"
    echo "  podman network rm ${NETWORK_NAME}"
    echo "  podman rmi ${IMAGE_NAME}"
    echo ""
    exit 0
else
    print_error "Some tests failed!"
    echo ""
    print_info "Containers are still running for debugging"
    print_info "Check application logs: podman logs ${APP_NAME}"
    print_info "Check database logs: podman logs ${DB_NAME}"
    echo ""
    print_info "When done, cleanup with:"
    echo "  podman stop ${APP_NAME} ${DB_NAME}"
    echo "  podman rm ${APP_NAME} ${DB_NAME}"
    echo "  podman network rm ${NETWORK_NAME}"
    echo ""
    exit 1
fi

# Made with Bob
