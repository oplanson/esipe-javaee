#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
# Docker test script for Lab 07 - Hexagonal Architecture

set -e

echo "========================================="
echo "Lab 07 - Hexagonal Architecture"
echo "Docker Deployment Test"
echo "========================================="
# Change to solution directory
cd solution


# Configuration
APP_NAME="banking-hexagonal-app"
DB_NAME="banking-hexagonal-db"
NETWORK_NAME="banking-hexagonal-network"
DB_PORT=5432
APP_PORT=9080

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to wait for service
wait_for_service() {
    local host=$1
    local port=$2
    local service=$3
    local max_attempts=30
    local attempt=0
    
    echo -n "Waiting for ${service} to be ready"
    while ! nc -z ${host} ${port} 2>/dev/null; do
        attempt=$((attempt + 1))
        if [ $attempt -ge $max_attempts ]; then
            echo -e " ${RED}✗ TIMEOUT${NC}"
            return 1
        fi
        echo -n "."
        sleep 2
    done
    echo -e " ${GREEN}✓ READY${NC}"
    return 0
}

# 1. Cleanup existing containers
echo ""
echo "1. Cleaning up existing containers"
echo "-----------------------------------"
docker stop ${APP_NAME} 2>/dev/null || true
docker rm ${APP_NAME} 2>/dev/null || true
docker stop ${DB_NAME} 2>/dev/null || true
docker rm ${DB_NAME} 2>/dev/null || true
docker network rm ${NETWORK_NAME} 2>/dev/null || true
echo -e "${GREEN}✓ Cleanup complete${NC}"

# 2. Create network
echo ""
echo "2. Creating network"
echo "-------------------"
docker network create ${NETWORK_NAME}
echo -e "${GREEN}✓ Network created${NC}"

# 3. Start PostgreSQL
echo ""
echo "3. Starting PostgreSQL"
echo "----------------------"
docker run -d \
    --name ${DB_NAME} \
    --network ${NETWORK_NAME} \
    -e POSTGRES_DB=bankingdb \
    -e POSTGRES_USER=bankuser \
    -e POSTGRES_PASSWORD=bankpass \
    -p ${DB_PORT}:5432 \
    postgres:16-alpine

echo -e "${GREEN}✓ Database container started${NC}"

# Wait for PostgreSQL
wait_for_service localhost ${DB_PORT} "PostgreSQL" || exit 1

# 4. Build application
echo ""
echo "4. Building application"
echo "-----------------------"
echo "Building Maven package..."
mvn clean package -DskipTests

echo "Building Docker image..."
docker build -t ${APP_NAME}:latest -f Containerfile .
echo -e "${GREEN}✓ Application image built${NC}"

# 5. Start application
echo ""
echo "5. Starting application"
echo "-----------------------"
docker run -d \
    --name ${APP_NAME} \
    --network ${NETWORK_NAME} \
    -p ${APP_PORT}:9080 \
    -e DB_HOST=${DB_NAME} \
    -e DB_PORT=5432 \
    -e DB_NAME=bankingdb \
    -e DB_USER=bankuser \
    -e DB_PASSWORD=bankpass \
    ${APP_NAME}:latest

echo -e "${GREEN}✓ Application container started${NC}"

# Wait for application
wait_for_service localhost ${APP_PORT} "Application" || exit 1

# 6. Run tests
echo ""
echo "6. Running tests"
echo "----------------"
sleep 5  # Give the app a bit more time to fully initialize

BASE_URL="http://localhost:${APP_PORT}"
TESTS_PASSED=0
TESTS_FAILED=0

# Test health endpoint
echo -n "Testing health endpoint... "
if curl -s -f "${BASE_URL}/health" > /dev/null; then
    echo -e "${GREEN}✓ PASSED${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}✗ FAILED${NC}"
    ((TESTS_FAILED++))
fi

# Test metrics endpoint
echo -n "Testing metrics endpoint... "
if curl -s -f "${BASE_URL}/metrics" > /dev/null; then
    echo -e "${GREEN}✓ PASSED${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}✗ FAILED${NC}"
    ((TESTS_FAILED++))
fi

# Test REST API - clients
echo -n "Testing clients API... "
if curl -s -f "${BASE_URL}/api/clients" > /dev/null; then
    echo -e "${GREEN}✓ PASSED${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}✗ FAILED${NC}"
    ((TESTS_FAILED++))
fi

# Test REST API - accounts
echo -n "Testing accounts API... "
if curl -s -f "${BASE_URL}/api/accounts" > /dev/null; then
    echo -e "${GREEN}✓ PASSED${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}✗ FAILED${NC}"
    ((TESTS_FAILED++))
fi

# Test Web UI
echo -n "Testing web UI... "
if curl -s -f "${BASE_URL}/" > /dev/null; then
    echo -e "${GREEN}✓ PASSED${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}✗ FAILED${NC}"
    ((TESTS_FAILED++))
fi

# 7. Display results
echo ""
echo "========================================="
echo "Deployment Summary"
echo "========================================="
echo -e "Network: ${GREEN}${NETWORK_NAME}${NC}"
echo -e "Database: ${GREEN}${DB_NAME}${NC} (port ${DB_PORT})"
echo -e "Application: ${GREEN}${APP_NAME}${NC} (port ${APP_PORT})"
echo ""
echo "Test Results:"
echo -e "  Passed: ${GREEN}${TESTS_PASSED}${NC}"
echo -e "  Failed: ${RED}${TESTS_FAILED}${NC}"
echo ""
echo "Access URLs:"
echo "  Web UI:  http://localhost:${APP_PORT}"
echo "  API:     http://localhost:${APP_PORT}/api"
echo "  Health:  http://localhost:${APP_PORT}/health"
echo "  Metrics: http://localhost:${APP_PORT}/metrics"
echo ""
echo "Useful commands:"
echo "  View logs:     docker logs -f ${APP_NAME}"
echo "  Stop all:      docker stop ${APP_NAME} ${DB_NAME}"
echo "  Remove all:    docker rm ${APP_NAME} ${DB_NAME}"
echo "  Remove network: docker network rm ${NETWORK_NAME}"
echo "========================================="

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "${GREEN}All tests passed! Deployment successful!${NC}"
    exit 0
else
    echo -e "${RED}Some tests failed! Check the logs.${NC}"
    echo "View application logs: docker logs ${APP_NAME}"
    exit 1
fi

# Made with Bob
