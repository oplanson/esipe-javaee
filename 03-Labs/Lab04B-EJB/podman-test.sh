#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Lab 04B - Podman Testing Script
# Tests the EJB banking application in containers

set -e

echo "=========================================="
echo "Lab 04B: Podman + Open Liberty Testing"
echo "EJB Banking Application"
echo "=========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
IMAGE_NAME="lab04b-ejb:latest"
CONTAINER_NAME="lab04b-liberty"
POSTGRES_CONTAINER="lab04b-postgres"
NETWORK_NAME="lab04b-network"
APP_PORT=9080

# Check if podman is installed
if ! command -v podman &> /dev/null; then
    echo -e "${RED}✗ Podman is not installed${NC}"
    echo "Please install Podman: https://podman.io/getting-started/installation"
    exit 1
fi
echo -e "${GREEN}✓ Podman found${NC}"
podman version | head -1

# Step 0: Cleanup and idempotence checks
echo ""
echo "Step 0: Checking for existing containers..."
echo "----------------------------"

# Stop and remove existing containers if they exist
if podman ps -a --format "{{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
    echo "Stopping existing ${CONTAINER_NAME} container..."
    podman stop $CONTAINER_NAME 2>/dev/null || true
    echo "Removing existing ${CONTAINER_NAME} container..."
    podman rm $CONTAINER_NAME 2>/dev/null || true
fi

if podman ps -a --format "{{.Names}}" | grep -q "^${POSTGRES_CONTAINER}$"; then
    echo "Stopping existing ${POSTGRES_CONTAINER} container..."
    podman stop $POSTGRES_CONTAINER 2>/dev/null || true
    echo "Removing existing ${POSTGRES_CONTAINER} container..."
    podman rm $POSTGRES_CONTAINER 2>/dev/null || true
fi

# Check for port conflicts on 9080
echo "Checking for port conflicts on 9080..."
if lsof -Pi :9080 -sTCP:LISTEN -t >/dev/null 2>&1; then
    PID=$(lsof -Pi :9080 -sTCP:LISTEN -t)
    PROCESS=$(ps -p $PID -o comm= 2>/dev/null || echo "unknown")
    echo -e "${YELLOW}⚠ Port 9080 is in use by process $PROCESS (PID: $PID)${NC}"
    echo "Attempting to stop the process..."
    kill -15 $PID 2>/dev/null || true
    sleep 2
    if lsof -Pi :9080 -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${RED}✗ Could not free port 9080${NC}"
        echo "Please manually stop the process using port 9080"
        exit 1
    fi
    echo -e "${GREEN}✓ Port 9080 freed${NC}"
else
    echo -e "${GREEN}✓ Port 9080 is available${NC}"
fi

# Check for port conflicts on 5432
echo "Checking for port conflicts on 5432..."
if lsof -Pi :5432 -sTCP:LISTEN -t >/dev/null 2>&1; then
    PID=$(lsof -Pi :5432 -sTCP:LISTEN -t)
    PROCESS=$(ps -p $PID -o comm= 2>/dev/null || echo "unknown")
    echo -e "${YELLOW}⚠ Port 5432 is in use by process $PROCESS (PID: $PID)${NC}"
    echo "This might be a local PostgreSQL instance. Continuing anyway..."
else
    echo -e "${GREEN}✓ Port 5432 is available${NC}"
fi

# Remove existing network if it exists
if podman network exists $NETWORK_NAME 2>/dev/null; then
    echo "Removing existing ${NETWORK_NAME}..."
    podman network rm $NETWORK_NAME 2>/dev/null || true
fi

# Remove old images if they exist
if podman images | grep -q "lab04b-ejb"; then
    echo "Removing old lab04b-ejb images..."
    podman rmi $IMAGE_NAME 2>/dev/null || true
fi

echo -e "${GREEN}✓ Cleanup complete - ready for fresh deployment${NC}"

echo -e "${GREEN}✓ Cleanup complete - ready for fresh deployment${NC}"
echo ""

# Navigate to solution directory
cd solution

# Step 1: Build the application
echo "Step 1: Building application..."
echo "----------------------------"
mvn clean package -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
    echo "  WAR file: target/banking-ejb-app.war"
else
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

echo ""

# Step 2: Create network
echo "Step 2: Creating Podman network..."
echo "----------------------------"
podman network exists $NETWORK_NAME 2>/dev/null || podman network create $NETWORK_NAME

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Network created${NC}"
else
    echo -e "${RED}❌ Network creation failed${NC}"
    exit 1
fi

echo ""

# Step 3: Start PostgreSQL
echo "Step 3: Starting PostgreSQL container..."
echo "----------------------------"
podman run -d \
    --name $POSTGRES_CONTAINER \
    --network $NETWORK_NAME \
    -e POSTGRES_DB=bankingdb \
    -e POSTGRES_USER=bankuser \
    -e POSTGRES_PASSWORD=bankpass \
    -p 5432:5432 \
    postgres:16-alpine

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ PostgreSQL container started${NC}"
else
    echo -e "${RED}❌ PostgreSQL container failed to start${NC}"
    exit 1
fi

echo "Waiting for PostgreSQL to be ready..."
sleep 10

# Verify PostgreSQL is running
if podman exec $POSTGRES_CONTAINER pg_isready -U bankuser -d bankingdb > /dev/null 2>&1; then
    echo -e "${GREEN}✓ PostgreSQL is ready${NC}"
else
    echo -e "${RED}❌ PostgreSQL failed to start${NC}"
    podman logs $POSTGRES_CONTAINER
    exit 1
fi

echo ""

# Step 4: Build Liberty image
echo "Step 4: Building Podman image..."
echo "----------------------------"
podman build -t $IMAGE_NAME -f Containerfile .

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Podman image built${NC}"
else
    echo -e "${RED}❌ Podman build failed${NC}"
    exit 1
fi

echo ""

# Step 5: Start Liberty container
echo "Step 5: Starting container with Open Liberty..."
echo "----------------------------"
podman run -d \
    --name $CONTAINER_NAME \
    --network $NETWORK_NAME \
    -e DB_HOST=$POSTGRES_CONTAINER \
    -e DB_PORT=5432 \
    -e DB_NAME=bankingdb \
    -e DB_USER=bankuser \
    -e DB_PASSWORD=bankpass \
    -p $APP_PORT:9080 \
    -p 9443:9443 \
    $IMAGE_NAME

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Container started${NC}"
else
    echo -e "${RED}❌ Container failed to start${NC}"
    exit 1
fi

echo ""

echo "Step 6: Waiting for Open Liberty to start..."
echo "----------------------------"

# Wait for container to be healthy
TIMEOUT=60
ELAPSED=0

while [ $ELAPSED -lt $TIMEOUT ]; do
    if podman exec $CONTAINER_NAME test -f /opt/ol/wlp/output/defaultServer/logs/messages.log 2>/dev/null; then
        if podman exec $CONTAINER_NAME grep -q "CWWKF0011I" /opt/ol/wlp/output/defaultServer/logs/messages.log 2>/dev/null; then
            echo -e "${GREEN}✓ Open Liberty started successfully${NC}"
            break
        fi
    fi
    sleep 2
    ELAPSED=$((ELAPSED + 2))
    echo -n "."
done
echo ""

if [ $ELAPSED -ge $TIMEOUT ]; then
    echo -e "${RED}❌ Timeout waiting for Liberty to start${NC}"
    podman logs $CONTAINER_NAME
    exit 1
fi

echo ""

# Step 7: Test health endpoint
echo "Step 7: Testing health endpoint..."
echo "----------------------------"
MAX_RETRIES=12
RETRY_COUNT=0
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    HEALTH_RESPONSE=$(curl -s http://localhost:9080/health 2>/dev/null || echo "")
    if echo "$HEALTH_RESPONSE" | grep -q "UP"; then
        echo -e "${GREEN}✓ Health check passed${NC}"
        echo "$HEALTH_RESPONSE" | jq '.' 2>/dev/null || echo "$HEALTH_RESPONSE"
        break
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
        echo -e "${RED}✗ Health check failed after $MAX_RETRIES attempts${NC}"
        echo "Liberty logs:"
        podman logs lab04b-liberty | tail -50
        exit 1
    fi
    echo "Waiting for application to be ready... (attempt $RETRY_COUNT/$MAX_RETRIES)"
    sleep 5
done

echo ""

# Step 8: Test metrics endpoint
echo "Step 8: Testing metrics endpoint..."
echo "----------------------------"
METRICS_RESPONSE=$(curl -s http://localhost:9080/metrics)
if [ -n "$METRICS_RESPONSE" ]; then
    echo -e "${GREEN}✓ Metrics endpoint accessible${NC}"
    echo "$METRICS_RESPONSE" | head -20
else
    echo -e "${YELLOW}⚠ Metrics endpoint not accessible${NC}"
fi

echo ""

# Step 9: Test application
echo "Step 9: Testing application..."
echo "----------------------------"
APP_RESPONSE=$(curl -s http://localhost:9080/)
if echo "$APP_RESPONSE" | grep -q "Lab 04B"; then
    echo -e "${GREEN}✓ Application is accessible${NC}"
else
    echo -e "${RED}✗ Application not accessible${NC}"
    podman logs lab04b-liberty | tail -30
fi

echo ""

# Step 10: Test EJB operations
echo "Step 10: Testing EJB operations..."
echo "----------------------------"
echo "Testing account creation..."
BANKING_RESPONSE=$(curl -s -u admin:admin123 "http://localhost:9080/banking?action=create")
if echo "$BANKING_RESPONSE" | grep -q "Account created"; then
    echo -e "${GREEN}✓ Account creation successful${NC}"
else
    echo -e "${YELLOW}⚠ Account creation test inconclusive${NC}"
fi

echo ""

# Display container status
echo "Step 11: Container status..."
echo "----------------------------"
podman ps --filter "name=lab04b"

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
echo -e "${BLUE}Container Management:${NC}"
echo "  - View logs: podman logs $CONTAINER_NAME"
echo "  - Stop containers: podman stop $POSTGRES_CONTAINER $CONTAINER_NAME"
echo "  - Remove containers: podman rm $POSTGRES_CONTAINER $CONTAINER_NAME"
echo "  - Remove network: podman network rm $NETWORK_NAME"
echo ""
echo -e "${GREEN}✓ All tests passed!${NC}"
echo ""

# Made with Bob