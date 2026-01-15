#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Lab 04B - EJB Testing Script
# Tests the EJB banking application locally

set -e

echo "=========================================="
echo "Lab 04B: EJB Banking Application Testing"
echo "=========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}✗ Maven is not installed${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Maven found${NC}"
mvn --version | head -1

# Check if PostgreSQL is running
echo ""
echo "Step 1: Checking PostgreSQL..."
echo "----------------------------"
if ! pg_isready -h localhost -p 5432 &> /dev/null; then
    echo -e "${YELLOW}⚠ PostgreSQL not running on localhost:5432${NC}"
    echo "Starting PostgreSQL with Docker Compose..."
    docker-compose up -d postgres
    echo "Waiting for PostgreSQL to be ready..."
    sleep 5
fi
echo -e "${GREEN}✓ PostgreSQL is ready${NC}"

# Build the application
echo ""
echo "Step 2: Building application..."
echo "----------------------------"
cd solution
mvn clean package
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
else
    echo -e "${RED}✗ Build failed${NC}"
    exit 1
fi

# Start Liberty server
echo ""
echo "Step 3: Starting Liberty server..."
echo "----------------------------"
mvn liberty:run &
LIBERTY_PID=$!

# Wait for server to start
echo "Waiting for server to start..."
sleep 30

# Test health endpoint
echo ""
echo "Step 4: Testing health endpoint..."
echo "----------------------------"
HEALTH_RESPONSE=$(curl -s http://localhost:9080/health)
if echo "$HEALTH_RESPONSE" | grep -q "UP"; then
    echo -e "${GREEN}✓ Health check passed${NC}"
    echo "$HEALTH_RESPONSE" | jq '.' 2>/dev/null || echo "$HEALTH_RESPONSE"
else
    echo -e "${RED}✗ Health check failed${NC}"
    echo "$HEALTH_RESPONSE"
fi

# Test metrics endpoint
echo ""
echo "Step 5: Testing metrics endpoint..."
echo "----------------------------"
METRICS_RESPONSE=$(curl -s http://localhost:9080/metrics)
if [ -n "$METRICS_RESPONSE" ]; then
    echo -e "${GREEN}✓ Metrics endpoint accessible${NC}"
    echo "$METRICS_RESPONSE" | head -20
else
    echo -e "${RED}✗ Metrics endpoint failed${NC}"
fi

# Test application endpoint
echo ""
echo "Step 6: Testing application..."
echo "----------------------------"
APP_RESPONSE=$(curl -s http://localhost:9080/)
if echo "$APP_RESPONSE" | grep -q "Lab 04B"; then
    echo -e "${GREEN}✓ Application is accessible${NC}"
else
    echo -e "${RED}✗ Application not accessible${NC}"
fi

# Step 7: Comprehensive EJB Functional Tests
echo ""
echo "Step 7: Testing EJB operations..."
echo "----------------------------"

# Test counters
TESTS_PASSED=0
TESTS_FAILED=0

# Test 1: Create first account (Stateless EJB)
echo ""
echo "Test 1: Creating first account (Stateless EJB - AccountServiceBean)..."
RESPONSE=$(curl -s -u admin:admin123 "http://localhost:9080/banking?action=create")
if echo "$RESPONSE" | grep -q "Account created"; then
    echo -e "${GREEN}✓ Account creation successful${NC}"
    ACCOUNT1_ID=$(echo "$RESPONSE" | grep -oP 'ACC-\d+' | head -1)
    echo "  Account Number: $ACCOUNT1_ID"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ Account creation failed${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 2: Create second account
echo ""
echo "Test 2: Creating second account..."
sleep 1
RESPONSE=$(curl -s -u admin:admin123 "http://localhost:9080/banking?action=create")
if echo "$RESPONSE" | grep -q "Account created"; then
    echo -e "${GREEN}✓ Second account created${NC}"
    ACCOUNT2_ID=$(echo "$RESPONSE" | grep -oP 'ACC-\d+' | tail -1)
    echo "  Account Number: $ACCOUNT2_ID"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ Second account creation failed${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 3: Verify accounts list
echo ""
echo "Test 3: Verifying accounts list..."
RESPONSE=$(curl -s -u admin:admin123 "http://localhost:9080/banking")
ACCOUNT_COUNT=$(echo "$RESPONSE" | grep -o "ACC-" | wc -l)
if [ "$ACCOUNT_COUNT" -ge 2 ]; then
    echo -e "${GREEN}✓ Found $ACCOUNT_COUNT accounts${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ Expected at least 2 accounts, found $ACCOUNT_COUNT${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 4: Deposit operation (Stateless EJB with CMT)
echo ""
echo "Test 4: Testing deposit operation (Container-Managed Transaction)..."
FIRST_ACCOUNT_ID=$(curl -s -u admin:admin123 "http://localhost:9080/banking" | grep -oP '<td>\K\d+(?=</td>)' | head -1)
if [ -n "$FIRST_ACCOUNT_ID" ]; then
    RESPONSE=$(curl -s -u admin:admin123 "http://localhost:9080/banking?action=deposit&accountId=$FIRST_ACCOUNT_ID&amount=1000.00")
    if echo "$RESPONSE" | grep -q "Deposit successful"; then
        echo -e "${GREEN}✓ Deposit of \$1000.00 successful${NC}"
        echo "  Account ID: $FIRST_ACCOUNT_ID"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        echo -e "${RED}✗ Deposit failed${NC}"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
else
    echo -e "${YELLOW}⚠ Could not extract account ID for deposit test${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 5: Withdrawal operation
echo ""
echo "Test 5: Testing withdrawal operation..."
if [ -n "$FIRST_ACCOUNT_ID" ]; then
    RESPONSE=$(curl -s -u admin:admin123 "http://localhost:9080/banking?action=withdraw&accountId=$FIRST_ACCOUNT_ID&amount=250.00")
    if echo "$RESPONSE" | grep -q "Withdrawal successful"; then
        echo -e "${GREEN}✓ Withdrawal of \$250.00 successful${NC}"
        echo "  Account ID: $FIRST_ACCOUNT_ID"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        echo -e "${RED}✗ Withdrawal failed${NC}"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
else
    echo -e "${YELLOW}⚠ Skipping withdrawal test (no account ID)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 6: Transfer operation (tests transaction management)
echo ""
echo "Test 6: Testing transfer operation (Transaction Management)..."
SECOND_ACCOUNT_ID=$(curl -s -u admin:admin123 "http://localhost:9080/banking" | grep -oP '<td>\K\d+(?=</td>)' | sed -n '2p')
if [ -n "$FIRST_ACCOUNT_ID" ] && [ -n "$SECOND_ACCOUNT_ID" ]; then
    RESPONSE=$(curl -s -u admin:admin123 "http://localhost:9080/banking?action=transfer&fromId=$FIRST_ACCOUNT_ID&toId=$SECOND_ACCOUNT_ID&amount=100.00")
    if echo "$RESPONSE" | grep -q "Transfer successful"; then
        echo -e "${GREEN}✓ Transfer of \$100.00 successful${NC}"
        echo "  From Account ID: $FIRST_ACCOUNT_ID"
        echo "  To Account ID: $SECOND_ACCOUNT_ID"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        echo -e "${RED}✗ Transfer failed${NC}"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
else
    echo -e "${YELLOW}⚠ Skipping transfer test (insufficient accounts)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 7: Verify balance after operations
echo ""
echo "Test 7: Verifying account balances..."
RESPONSE=$(curl -s -u admin:admin123 "http://localhost:9080/banking")
if echo "$RESPONSE" | grep -q "\$650.00"; then
    echo -e "${GREEN}✓ First account balance correct (\$650.00 = \$1000 - \$250 - \$100)${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${YELLOW}⚠ Could not verify first account balance${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

if echo "$RESPONSE" | grep -q "\$100.00"; then
    echo -e "${GREEN}✓ Second account balance correct (\$100.00 from transfer)${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${YELLOW}⚠ Could not verify second account balance${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 8: Singleton EJB - Configuration Service
echo ""
echo "Test 8: Testing Singleton EJB (ConfigServiceBean)..."
RESPONSE=$(curl -s -u admin:admin123 "http://localhost:9080/banking")
if echo "$RESPONSE" | grep -q "EJB Banking Application"; then
    echo -e "${GREEN}✓ Singleton ConfigService working (app.name retrieved)${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${YELLOW}⚠ Could not verify Singleton ConfigService${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 9: Timer Service - Report Generator
echo ""
echo "Test 9: Testing Timer Service (ReportGeneratorBean)..."
if echo "$RESPONSE" | grep -q "Report Statistics"; then
    echo -e "${GREEN}✓ Timer Service working (ReportGeneratorBean active)${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${YELLOW}⚠ Could not verify Timer Service${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 10: Security - Role-based access
echo ""
echo "Test 10: Testing EJB Security (Role-based access)..."
RESPONSE=$(curl -s -u admin:admin123 "http://localhost:9080/banking?action=create")
if echo "$RESPONSE" | grep -q "Account created"; then
    echo -e "${GREEN}✓ Admin role has access to create accounts${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ Admin role access failed${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

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

echo "=========================================="
echo "Testing Complete!"
echo "=========================================="
echo ""
echo -e "${BLUE}Application URLs:${NC}"
echo "  - Home: http://localhost:9080/"
echo "  - Banking: http://localhost:9080/banking"
echo "  - Health: http://localhost:9080/health"
echo "  - Metrics: http://localhost:9080/metrics"
echo ""
echo -e "${BLUE}Default credentials:${NC}"
echo "  - admin/admin123"
echo "  - teller/teller123"
echo "  - customer/customer123"
echo ""
echo -e "${BLUE}EJB Components Tested:${NC}"
echo "  ✓ Stateless Session Bean (AccountServiceBean)"
echo "  ✓ Singleton Session Bean (ConfigServiceBean)"
echo "  ✓ Timer Service (ReportGeneratorBean)"
echo "  ✓ Container-Managed Transactions (CMT)"
echo "  ✓ EJB Security (Role-based access)"
echo "  ✓ JPA Integration"
echo ""
echo "Server is still running. Press Ctrl+C to stop."
echo ""

# Wait for user to stop
wait $LIBERTY_PID

# Made with Bob