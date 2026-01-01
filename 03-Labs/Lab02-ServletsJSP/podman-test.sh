#!/bin/bash
# © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
# Podman-based testing for Lab 02 with Open Liberty
# No local Liberty installation required!

set -e

echo "=========================================="
echo "Lab 02: Podman + Open Liberty Testing"
echo "Servlets, JSP & MicroProfile"
echo "=========================================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuration
IMAGE_NAME="banking-web-app:lab02"
CONTAINER_NAME="banking-web-lab02"
APP_PORT=9080

# Check if Podman is installed
if ! command -v podman &> /dev/null; then
    echo -e "${RED}❌ Podman not found${NC}"
    echo ""
    echo "Please install Podman:"
    echo "  macOS: brew install podman"
    echo "  Linux: sudo apt-get install podman"
    echo "  Windows: Download from https://podman.io/getting-started/installation"
    exit 1
fi

echo -e "${GREEN}✓ Podman found${NC}"
podman --version
echo ""

# Step 0: Cleanup existing containers and images
echo "Step 0: Checking for existing containers..."
echo "----------------------------"

# Determine which container runtime to use
CONTAINER_CMD=""
if command -v docker &> /dev/null; then
    CONTAINER_CMD="docker"
elif command -v podman &> /dev/null; then
    CONTAINER_CMD="podman"
fi

# Check if application container exists and is running
if $CONTAINER_CMD ps 2>/dev/null | grep -q $CONTAINER_NAME; then
    echo -e "${YELLOW}⚠ Application container is running, stopping...${NC}"
    podman stop $CONTAINER_NAME > /dev/null 2>&1 || true
    echo -e "${GREEN}✓ Container stopped${NC}"
fi

# Check if application container exists (stopped)
if $CONTAINER_CMD ps -a 2>/dev/null | grep -q $CONTAINER_NAME; then
    echo -e "${YELLOW}⚠ Application container exists, removing...${NC}"
    podman rm $CONTAINER_NAME > /dev/null 2>&1 || true
    echo -e "${GREEN}✓ Container removed${NC}"
fi

# Check for port conflicts - stop any container using port 9080
echo "Checking for port conflicts on $APP_PORT..."
CONFLICTING_CONTAINERS=$(podman ps --format "{{.Names}}" | while read -r name; do
    if podman port "$name" 2>/dev/null | grep -q "0.0.0.0:$APP_PORT"; then
        echo "$name"
    fi
done)

if [ -n "$CONFLICTING_CONTAINERS" ]; then
    echo -e "${YELLOW}⚠ Found containers using port $APP_PORT:${NC}"
    echo "$CONFLICTING_CONTAINERS" | while read -r container; do
        if [ -n "$container" ] && [ "$container" != "$CONTAINER_NAME" ]; then
            echo -e "${YELLOW}  Stopping $container...${NC}"
            podman stop "$container" > /dev/null 2>&1 || true
            podman rm "$container" > /dev/null 2>&1 || true
            echo -e "${GREEN}  ✓ $container stopped and removed${NC}"
        fi
    done
else
    echo -e "${GREEN}✓ No port conflicts detected${NC}"
fi

# Check if image exists
if podman images | grep -q "banking-web-app.*lab02"; then
    echo -e "${YELLOW}⚠ Old image exists, removing...${NC}"
    podman rmi $IMAGE_NAME 2>/dev/null || true
    echo -e "${GREEN}✓ Old image removed${NC}"
fi

echo -e "${GREEN}✓ Cleanup complete - ready for fresh deployment${NC}"
echo ""

# Navigate to solution directory
cd solution

echo "Step 1: Building application..."
echo "----------------------------"
mvn clean package -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
    echo "  WAR file: target/banking-web-app.war"
else
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

echo ""

echo "Step 2: Building Podman image..."
echo "----------------------------"
podman build -t $IMAGE_NAME -f Containerfile .

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Podman image built${NC}"
else
    echo -e "${RED}❌ Podman build failed${NC}"
    exit 1
fi

echo ""

echo "Step 3: Starting container with Open Liberty..."
echo "----------------------------"
podman run -d \
    --name $CONTAINER_NAME \
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
echo "Step 4: Waiting for Open Liberty to start..."
echo "----------------------------"

# Wait for container to be healthy
TIMEOUT=60
ELAPSED=0
LIBERTY_STARTED=false

while [ $ELAPSED -lt $TIMEOUT ]; do
    # Check if container is still running
    if ! podman ps | grep -q $CONTAINER_NAME; then
        echo ""
        echo -e "${RED}❌ Container stopped unexpectedly${NC}"
        echo ""
        echo "Container logs:"
        echo "----------------------------"
        podman logs $CONTAINER_NAME | tail -50
        exit 1
    fi
    
    # Try to connect to the application
    if curl -s -f http://localhost:$APP_PORT/ > /dev/null 2>&1; then
        echo ""
        echo -e "${GREEN}✓ Open Liberty started successfully${NC}"
        LIBERTY_STARTED=true
        break
    fi
    
    sleep 2
    ELAPSED=$((ELAPSED + 2))
    echo -n "."
done

echo ""

if [ "$LIBERTY_STARTED" = false ]; then
    echo -e "${RED}❌ Open Liberty failed to start within ${TIMEOUT}s${NC}"
    echo ""
    echo "Container status:"
    podman ps -a | grep $CONTAINER_NAME
    echo ""
    echo "Container logs (last 50 lines):"
    echo "----------------------------"
    podman logs $CONTAINER_NAME | tail -50
    echo ""
    echo -e "${YELLOW}Tip: Check for port conflicts or configuration errors${NC}"
    exit 1
fi

# Verify the port is correctly configured
echo ""
echo "Verifying server configuration..."
echo "----------------------------"

# Check if server is listening on port
if curl -s -I http://localhost:$APP_PORT/ | head -1 | grep -q "200\|302"; then
    echo -e "${GREEN}✓ Server responding on port $APP_PORT${NC}"
else
    echo -e "${RED}❌ Server not responding correctly on port $APP_PORT${NC}"
    echo ""
    echo "Checking container logs for port configuration..."
    podman logs $CONTAINER_NAME | grep -i "port\|endpoint\|listening" | tail -10
    exit 1
fi

echo ""
echo "=========================================="
echo "Application Ready!"
echo "=========================================="
echo ""
echo -e "${BLUE}Application URLs:${NC}"
echo "  🏠 Home:          http://localhost:9080/"
echo "  👥 Clients:       http://localhost:9080/clients"
echo "  👤 Client View:   http://localhost:9080/client?action=view&id=1"
echo "  ➕ New Client:    http://localhost:9080/client?action=new"
echo ""
echo -e "${BLUE}MicroProfile Endpoints:${NC}"
echo "  💊 Health:        http://localhost:9080/health"
echo "  ❤️  Liveness:      http://localhost:9080/health/live"
echo "  ✅ Readiness:     http://localhost:9080/health/ready"
echo "  📊 Metrics:       http://localhost:9080/metrics"
echo "  📖 OpenAPI:       http://localhost:9080/openapi"
echo ""

# Wait a moment
sleep 3

echo "Step 5: Testing endpoints..."
echo "----------------------------"

TESTS_PASSED=0
TESTS_FAILED=0

# Test home page
echo -n "Testing home page... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:$APP_PORT/)
if [ "$HTTP_CODE" = "200" ] && grep -q "Banking" /tmp/response.txt; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test clients servlet
echo -n "Testing clients page... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:$APP_PORT/clients)
if [ "$HTTP_CODE" = "200" ] && grep -q "Client" /tmp/response.txt; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test new client form
echo -n "Testing new client form... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" "http://localhost:$APP_PORT/client?action=new")
if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test health endpoint
echo -n "Testing health endpoint... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:$APP_PORT/health)
if [ "$HTTP_CODE" = "200" ] && grep -q "UP" /tmp/response.txt; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test liveness endpoint
echo -n "Testing liveness endpoint... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:$APP_PORT/health/live)
if [ "$HTTP_CODE" = "200" ] && grep -q "UP" /tmp/response.txt; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test readiness endpoint
echo -n "Testing readiness endpoint... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:$APP_PORT/health/ready)
if [ "$HTTP_CODE" = "200" ] && grep -q "UP" /tmp/response.txt; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test metrics endpoint
echo -n "Testing metrics endpoint... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:$APP_PORT/metrics)
if [ "$HTTP_CODE" = "200" ] && grep -q "base" /tmp/response.txt; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
elif [ "$HTTP_CODE" = "401" ]; then
    # Try with authentication if required
    HTTP_CODE=$(curl -s -u admin:adminpwd -o /tmp/response.txt -w "%{http_code}" http://localhost:$APP_PORT/metrics)
    if [ "$HTTP_CODE" = "200" ] && grep -q "base" /tmp/response.txt; then
        echo -e "${GREEN}✓ PASS (with auth)${NC}"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        echo -e "${RED}✗ FAIL even with auth (HTTP $HTTP_CODE)${NC}"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test OpenAPI endpoint
echo -n "Testing OpenAPI endpoint... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:$APP_PORT/openapi)
if [ "$HTTP_CODE" = "200" ] && grep -q "openapi" /tmp/response.txt; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Cleanup
rm -f /tmp/response.txt

echo ""
echo "Test Results: ${GREEN}$TESTS_PASSED passed${NC}, ${RED}$TESTS_FAILED failed${NC}"

if [ $TESTS_FAILED -gt 0 ]; then
    echo ""
    echo -e "${YELLOW}⚠ Some tests failed. Check container logs:${NC}"
    echo "  podman logs $CONTAINER_NAME"
    exit 1
fi

echo ""
echo "=========================================="
echo "Podman Commands"
echo "=========================================="
echo ""
echo "View logs:"
echo "  podman logs -f $CONTAINER_NAME"
echo ""
echo "Stop container:"
echo "  podman stop $CONTAINER_NAME"
echo ""
echo "Remove container:"
echo "  podman rm $CONTAINER_NAME"
echo ""
echo "Remove image:"
echo "  podman rmi $IMAGE_NAME"
echo ""
echo "Restart container:"
echo "  podman restart $CONTAINER_NAME"
echo ""
echo "Execute shell in container:"
echo "  podman exec -it $CONTAINER_NAME /bin/bash"
echo ""

# Try to open browser
if command -v open &> /dev/null; then
    echo "Opening browser..."
    open http://localhost:$APP_PORT/
elif command -v xdg-open &> /dev/null; then
    echo "Opening browser..."
    xdg-open http://localhost:$APP_PORT/
fi

echo ""
echo -e "${GREEN}✓ Lab 02 is running with Podman + Open Liberty!${NC}"
echo ""
echo -e "${GREEN}✓ All tests passed successfully!${NC}"
echo ""
echo -e "${BLUE}Container is still running. Use the commands above to manage it.${NC}"
echo ""

# Made with Bob