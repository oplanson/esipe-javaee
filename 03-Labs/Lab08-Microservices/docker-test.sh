#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Lab 08: Microservices Architecture - Podman Test Script
# This script builds, deploys, and tests the microservices architecture

set -e  # Exit on error

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test counters
TESTS_PASSED=0
TESTS_FAILED=0
TOTAL_TESTS=0

# Function to print colored output
print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

# Function to print test result
print_test_result() {
    ((TOTAL_TESTS++))
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ PASS${NC}: $2"
        ((TESTS_PASSED++))
    else
        echo -e "${RED}✗ FAIL${NC}: $2"
        ((TESTS_FAILED++))
    fi
}

# Function to wait for service to be ready
wait_for_service() {
    local service_name=$1
    local health_url=$2
    local max_attempts=30
    local attempt=0
    
    print_info "Waiting for $service_name to be ready..."
    
    while [ $attempt -lt $max_attempts ]; do
        if curl -sf "$health_url" > /dev/null 2>&1; then
            print_success "$service_name is ready"
            return 0
        fi
        attempt=$((attempt + 1))
        echo -n "."
        sleep 2
    done
    
    print_error "$service_name failed to start"
    return 1
}

# Function to cleanup
cleanup() {
    print_info "Cleaning up..."
    docker-compose down -v 2>/dev/null || true
    print_success "Cleanup complete"
}

# Trap to ensure cleanup on exit
trap cleanup EXIT

echo "======================================"
echo "Lab 08: Microservices Testing"
echo "======================================"
echo ""

# Check if docker-compose is installed
if ! command -v docker-compose &> /dev/null; then
    print_error "docker-compose is not installed"
    print_info "Install with: pip3 install docker-compose"
    exit 1
fi

# Clean up any existing containers
print_info "Cleaning up existing containers..."
docker-compose down -v 2>/dev/null || true

# Build and start services
print_info "Building and starting services..."
print_info "This may take several minutes..."
docker-compose up -d --build

if [ $? -ne 0 ]; then
    print_error "Failed to start services"
    exit 1
fi

print_success "Services started"
echo ""

# Wait for databases to be ready
print_info "Waiting for databases to initialize..."
sleep 10

# Wait for services to be ready
wait_for_service "Client Service" "http://localhost:9081/health/live" || exit 1
wait_for_service "Account Service" "http://localhost:9082/health/live" || exit 1
wait_for_service "API Gateway" "http://localhost:9080/health/live" || exit 1

echo ""
print_success "All services are ready"
echo ""

# ============================================
# Health Check Tests
# ============================================
echo "======================================"
echo "Health Check Tests"
echo "======================================"
echo ""

# Test 1: Client Service Liveness
echo "Test 1: Client Service Liveness"
curl -sf http://localhost:9081/health/live | grep -q "UP"
print_test_result $? "Client Service is alive"

# Test 2: Client Service Readiness
echo ""
echo "Test 2: Client Service Readiness"
curl -sf http://localhost:9081/health/ready | grep -q "UP"
print_test_result $? "Client Service is ready"

# Test 3: Account Service Liveness
echo ""
echo "Test 3: Account Service Liveness"
curl -sf http://localhost:9082/health/live | grep -q "UP"
print_test_result $? "Account Service is alive"

# Test 4: Account Service Readiness
echo ""
echo "Test 4: Account Service Readiness"
curl -sf http://localhost:9082/health/ready | grep -q "UP"
print_test_result $? "Account Service is ready"

# Test 5: API Gateway Liveness
echo ""
echo "Test 5: API Gateway Liveness"
curl -sf http://localhost:9080/health/live | grep -q "UP"
print_test_result $? "API Gateway is alive"

# Test 6: API Gateway Readiness (Aggregate)
echo ""
echo "Test 6: API Gateway Readiness (Aggregate)"
curl -sf http://localhost:9080/health/ready | grep -q "UP"
print_test_result $? "API Gateway is ready (all services healthy)"

# ============================================
# Client Service Tests (via Gateway)
# ============================================
echo ""
echo "======================================"
echo "Client Service Tests (via Gateway)"
echo "======================================"
echo ""

# Test 7: Create Client
echo "Test 7: Create Client via Gateway"
CLIENT_RESPONSE=$(curl -sf -X POST http://localhost:9080/api/clients \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john.doe@example.com","phone":"1234567890","address":"123 Main St"}')

if [ $? -eq 0 ]; then
    CLIENT_ID=$(echo "$CLIENT_RESPONSE" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
    if [ -n "$CLIENT_ID" ]; then
        print_test_result 0 "Client created with ID: $CLIENT_ID"
    else
        print_test_result 1 "Failed to extract client ID"
        CLIENT_ID=1  # Fallback for subsequent tests
    fi
else
    print_test_result 1 "Failed to create client"
    CLIENT_ID=1  # Fallback for subsequent tests
fi

# Test 8: Get Client
echo ""
echo "Test 8: Get Client via Gateway"
curl -sf "http://localhost:9080/api/clients/$CLIENT_ID" | grep -q "John Doe"
print_test_result $? "Client retrieved successfully"

# Test 9: List All Clients
echo ""
echo "Test 9: List All Clients via Gateway"
curl -sf http://localhost:9080/api/clients | grep -q "John Doe"
print_test_result $? "Client list retrieved successfully"

# Test 10: Update Client
echo ""
echo "Test 10: Update Client via Gateway"
curl -sf -X PUT "http://localhost:9080/api/clients/$CLIENT_ID" \
  -H "Content-Type: application/json" \
  -d '{"name":"John Updated","email":"john.updated@example.com","phone":"0987654321","address":"456 Oak Ave","premium":true}' | grep -q "John Updated"
print_test_result $? "Client updated successfully"

# ============================================
# Account Service Tests (via Gateway)
# ============================================
echo ""
echo "======================================"
echo "Account Service Tests (via Gateway)"
echo "======================================"
echo ""

# Test 11: Create Account
echo "Test 11: Create Account via Gateway"
ACCOUNT_RESPONSE=$(curl -sf -X POST http://localhost:9080/api/accounts \
  -H "Content-Type: application/json" \
  -d "{\"clientId\":$CLIENT_ID,\"accountType\":\"CHECKING\"}")

if [ $? -eq 0 ]; then
    ACCOUNT_ID=$(echo "$ACCOUNT_RESPONSE" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
    if [ -n "$ACCOUNT_ID" ]; then
        print_test_result 0 "Account created with ID: $ACCOUNT_ID"
    else
        print_test_result 1 "Failed to extract account ID"
        ACCOUNT_ID=1  # Fallback for subsequent tests
    fi
else
    print_test_result 1 "Failed to create account"
    ACCOUNT_ID=1  # Fallback for subsequent tests
fi

# Test 12: Get Account
echo ""
echo "Test 12: Get Account via Gateway"
curl -sf "http://localhost:9080/api/accounts/$ACCOUNT_ID" | grep -q "CHECKING"
print_test_result $? "Account retrieved successfully"

# Test 13: Deposit Money
echo ""
echo "Test 13: Deposit Money via Gateway"
curl -sf -X POST "http://localhost:9080/api/accounts/$ACCOUNT_ID/deposit" \
  -H "Content-Type: application/json" \
  -d '{"amount":1000.00}' | grep -q "1000"
print_test_result $? "Money deposited successfully (balance: 1000.00)"

# Test 14: Withdraw Money
echo ""
echo "Test 14: Withdraw Money via Gateway"
curl -sf -X POST "http://localhost:9080/api/accounts/$ACCOUNT_ID/withdraw" \
  -H "Content-Type: application/json" \
  -d '{"amount":250.00}' | grep -q "750"
print_test_result $? "Money withdrawn successfully (balance: 750.00)"

# Test 15: Create Second Account for Transfer
echo ""
echo "Test 15: Create Second Account for Transfer"
ACCOUNT2_RESPONSE=$(curl -sf -X POST http://localhost:9080/api/accounts \
  -H "Content-Type: application/json" \
  -d "{\"clientId\":$CLIENT_ID,\"accountType\":\"SAVINGS\"}")

ACCOUNT2_ID=$(echo "$ACCOUNT2_RESPONSE" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
if [ -n "$ACCOUNT2_ID" ]; then
    print_test_result 0 "Second account created with ID: $ACCOUNT2_ID"
else
    print_test_result 1 "Failed to create second account"
    ACCOUNT2_ID=2  # Fallback
fi

# Test 16: Transfer Money
echo ""
echo "Test 16: Transfer Money Between Accounts"
curl -sf -X POST http://localhost:9080/api/accounts/transfer \
  -H "Content-Type: application/json" \
  -d "{\"fromAccountId\":$ACCOUNT_ID,\"toAccountId\":$ACCOUNT2_ID,\"amount\":200.00}" > /dev/null
print_test_result $? "Money transferred successfully"

# Verify balances after transfer
BALANCE1=$(curl -sf "http://localhost:9080/api/accounts/$ACCOUNT_ID" | grep -o '"balance":[0-9.]*' | grep -o '[0-9.]*')
BALANCE2=$(curl -sf "http://localhost:9080/api/accounts/$ACCOUNT2_ID" | grep -o '"balance":[0-9.]*' | grep -o '[0-9.]*')
print_info "Account 1 balance: $BALANCE1 (expected: 550.00)"
print_info "Account 2 balance: $BALANCE2 (expected: 200.00)"

# ============================================
# API Gateway Aggregation Tests
# ============================================
echo ""
echo "======================================"
echo "API Gateway Aggregation Tests"
echo "======================================"
echo ""

# Test 17: Get Client with Accounts (Aggregation)
echo "Test 17: Get Client with Accounts (Aggregation)"
AGGREGATED_RESPONSE=$(curl -sf "http://localhost:9080/api/clients/$CLIENT_ID/accounts")
echo "$AGGREGATED_RESPONSE" | grep -q "accounts" && echo "$AGGREGATED_RESPONSE" | grep -q "John"
print_test_result $? "Client with accounts retrieved successfully (aggregated response)"

# ============================================
# Fault Tolerance Tests
# ============================================
echo ""
echo "======================================"
echo "Fault Tolerance Tests"
echo "======================================"
echo ""

# Test 18: Circuit Breaker Test
echo "Test 18: Circuit Breaker Test"
print_info "Stopping Client Service to test circuit breaker..."
docker-compose stop client-service
sleep 5

# Try to create account (should fail gracefully due to circuit breaker)
CREATE_ACCOUNT_RESPONSE=$(curl -s -X POST http://localhost:9080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{"clientId":999,"accountType":"SAVINGS"}' \
  -w "%{http_code}" -o /dev/null)

if [ "$CREATE_ACCOUNT_RESPONSE" -ge 400 ]; then
    print_test_result 0 "Circuit breaker activated (HTTP $CREATE_ACCOUNT_RESPONSE)"
else
    print_test_result 1 "Circuit breaker did not activate"
fi

print_info "Restarting Client Service..."
docker-compose start client-service
sleep 30
wait_for_service "Client Service" "http://localhost:9081/health/live"

# ============================================
# Metrics Tests
# ============================================
echo ""
echo "======================================"
echo "Metrics Tests"
echo "======================================"
echo ""

# Test 19: Client Service Metrics
echo "Test 19: Client Service Metrics"
curl -sf http://localhost:9081/metrics | grep -q "application"
print_test_result $? "Client Service metrics endpoint accessible"

# Test 20: Account Service Metrics
echo ""
echo "Test 20: Account Service Metrics"
curl -sf http://localhost:9082/metrics | grep -q "application"
print_test_result $? "Account Service metrics endpoint accessible"

# Test 21: API Gateway Metrics
echo ""
echo "Test 21: API Gateway Metrics"
curl -sf http://localhost:9080/metrics | grep -q "application"
print_test_result $? "API Gateway metrics endpoint accessible"

# ============================================
# OpenAPI Documentation Tests
# ============================================
echo ""
echo "======================================"
echo "OpenAPI Documentation Tests"
echo "======================================"
echo ""

# Test 22: Client Service OpenAPI
echo "Test 22: Client Service OpenAPI Documentation"
curl -sf http://localhost:9081/openapi | grep -q "openapi"
print_test_result $? "Client Service OpenAPI documentation accessible"

# Test 23: Account Service OpenAPI
echo ""
echo "Test 23: Account Service OpenAPI Documentation"
curl -sf http://localhost:9082/openapi | grep -q "openapi"
print_test_result $? "Account Service OpenAPI documentation accessible"

# Test 24: API Gateway OpenAPI
echo ""
echo "Test 24: API Gateway OpenAPI Documentation"
curl -sf http://localhost:9080/openapi | grep -q "openapi"
print_test_result $? "API Gateway OpenAPI documentation accessible"

# ============================================
# Print Summary
# ============================================
echo ""
echo "======================================"
echo "Test Summary"
echo "======================================"
echo -e "Total Tests: ${BLUE}$TOTAL_TESTS${NC}"
echo -e "Tests Passed: ${GREEN}$TESTS_PASSED${NC}"
echo -e "Tests Failed: ${RED}$TESTS_FAILED${NC}"
echo ""

if [ $TESTS_FAILED -eq 0 ]; then
    print_success "All tests passed! 🎉"
    echo ""
    print_info "Services are running:"
    print_info "  - Client Service: http://localhost:9081"
    print_info "  - Account Service: http://localhost:9082"
    print_info "  - API Gateway: http://localhost:9080"
    echo ""
    print_info "To stop services: docker-compose down"
    print_info "To view logs: docker-compose logs -f"
    echo ""
    exit 0
else
    print_error "Some tests failed!"
    echo ""
    print_info "View logs with: docker-compose logs"
    print_info "Check service status: docker-compose ps"
    echo ""
    exit 1
fi

# Made with Bob
