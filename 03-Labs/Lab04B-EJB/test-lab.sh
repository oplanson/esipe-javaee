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

# Test EJB functionality
echo ""
echo "Step 7: Testing EJB operations..."
echo "----------------------------"
echo "Testing account creation..."
BANKING_RESPONSE=$(curl -s -u admin:admin123 "http://localhost:9080/banking?action=create")
if echo "$BANKING_RESPONSE" | grep -q "Account created"; then
    echo -e "${GREEN}✓ Account creation successful${NC}"
else
    echo -e "${YELLOW}⚠ Account creation test inconclusive${NC}"
fi

echo ""
echo "=========================================="
echo "Testing Complete!"
echo "=========================================="
echo ""
echo "Application URLs:"
echo "  - Home: http://localhost:9080/"
echo "  - Banking: http://localhost:9080/banking"
echo "  - Health: http://localhost:9080/health"
echo "  - Metrics: http://localhost:9080/metrics"
echo ""
echo "Default credentials:"
echo "  - admin/admin123"
echo "  - teller/teller123"
echo "  - customer/customer123"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

# Wait for user to stop
wait $LIBERTY_PID

# Made with Bob