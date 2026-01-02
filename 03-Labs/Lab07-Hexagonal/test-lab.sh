#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
# Test Lab 07 - Hexagonal Architecture

set -e

echo "========================================="
echo "Testing Lab 07 - Hexagonal Architecture"
echo "========================================="

# Change to solution directory
cd solution

BASE_URL="http://localhost:9080"
API_URL="${BASE_URL}/api"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test counter
TESTS_PASSED=0
TESTS_FAILED=0

# Function to test endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local expected_status=$3
    local description=$4
    local data=$5

    echo -n "Testing: ${description}... "
    
    if [ -n "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X ${method} \
            -H "Content-Type: application/json" \
            -d "${data}" \
            "${endpoint}")
    else
        response=$(curl -s -w "\n%{http_code}" -X ${method} "${endpoint}")
    fi
    
    status_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$status_code" -eq "$expected_status" ]; then
        echo -e "${GREEN}✓ PASSED${NC} (Status: ${status_code})"
        ((TESTS_PASSED++))
        return 0
    else
        echo -e "${RED}✗ FAILED${NC} (Expected: ${expected_status}, Got: ${status_code})"
        echo "Response: ${body}"
        ((TESTS_FAILED++))
        return 1
    fi
}

echo ""
echo "1. Testing Health Endpoints"
echo "----------------------------"
test_endpoint "GET" "${BASE_URL}/health" 200 "Health check"
test_endpoint "GET" "${BASE_URL}/health/live" 200 "Liveness check"
test_endpoint "GET" "${BASE_URL}/health/ready" 200 "Readiness check"

echo ""
echo "2. Testing Metrics Endpoint"
echo "---------------------------"
test_endpoint "GET" "${BASE_URL}/metrics" 200 "Metrics endpoint"

echo ""
echo "3. Testing Client REST API"
echo "--------------------------"
test_endpoint "GET" "${API_URL}/clients" 200 "List all clients"

# Create a test client
CLIENT_DATA='{"firstName":"John","lastName":"Doe","email":"john.doe@example.com","premium":false}'
echo -n "Creating test client... "
response=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d "${CLIENT_DATA}" \
    "${API_URL}/clients")
status_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$status_code" -eq 201 ]; then
    echo -e "${GREEN}✓ PASSED${NC}"
    ((TESTS_PASSED++))
    CLIENT_ID=$(echo "$body" | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
    echo "Created client with ID: ${CLIENT_ID}"
else
    echo -e "${RED}✗ FAILED${NC} (Status: ${status_code})"
    ((TESTS_FAILED++))
    CLIENT_ID=""
fi

if [ -n "$CLIENT_ID" ]; then
    test_endpoint "GET" "${API_URL}/clients/${CLIENT_ID}" 200 "Get client by ID"
    
    UPDATE_DATA='{"firstName":"John","lastName":"Smith","email":"john.smith@example.com","premium":true}'
    test_endpoint "PUT" "${API_URL}/clients/${CLIENT_ID}" 200 "Update client" "${UPDATE_DATA}"
fi

echo ""
echo "4. Testing Account REST API"
echo "---------------------------"
test_endpoint "GET" "${API_URL}/accounts" 200 "List all accounts"

if [ -n "$CLIENT_ID" ]; then
    # Create a test account
    ACCOUNT_DATA="{\"clientId\":${CLIENT_ID},\"accountType\":\"SAVINGS\",\"initialBalance\":1000.00}"
    echo -n "Creating test account... "
    response=$(curl -s -w "\n%{http_code}" -X POST \
        -H "Content-Type: application/json" \
        -d "${ACCOUNT_DATA}" \
        "${API_URL}/accounts")
    status_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$status_code" -eq 201 ]; then
        echo -e "${GREEN}✓ PASSED${NC}"
        ((TESTS_PASSED++))
        ACCOUNT_ID=$(echo "$body" | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
        echo "Created account with ID: ${ACCOUNT_ID}"
    else
        echo -e "${RED}✗ FAILED${NC} (Status: ${status_code})"
        ((TESTS_FAILED++))
        ACCOUNT_ID=""
    fi
    
    if [ -n "$ACCOUNT_ID" ]; then
        test_endpoint "GET" "${API_URL}/accounts/${ACCOUNT_ID}" 200 "Get account by ID"
        
        # Test deposit
        DEPOSIT_DATA='{"amount":500.00}'
        test_endpoint "POST" "${API_URL}/accounts/${ACCOUNT_ID}/deposit" 200 "Deposit money" "${DEPOSIT_DATA}"
        
        # Test withdrawal
        WITHDRAW_DATA='{"amount":200.00}'
        test_endpoint "POST" "${API_URL}/accounts/${ACCOUNT_ID}/withdraw" 200 "Withdraw money" "${WITHDRAW_DATA}"
        
        # Test account closure
        test_endpoint "POST" "${API_URL}/accounts/${ACCOUNT_ID}/close" 200 "Close account"
    fi
fi

echo ""
echo "5. Testing Web UI"
echo "-----------------"
test_endpoint "GET" "${BASE_URL}/" 200 "Home page"
test_endpoint "GET" "${BASE_URL}/clients" 200 "Client list page"
test_endpoint "GET" "${BASE_URL}/accounts" 200 "Account list page"

echo ""
echo "========================================="
echo "Test Summary"
echo "========================================="
echo -e "Tests Passed: ${GREEN}${TESTS_PASSED}${NC}"
echo -e "Tests Failed: ${RED}${TESTS_FAILED}${NC}"
echo "Total Tests: $((TESTS_PASSED + TESTS_FAILED))"
echo "========================================="

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "${GREEN}All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}Some tests failed!${NC}"
    exit 1
fi

# Made with Bob
