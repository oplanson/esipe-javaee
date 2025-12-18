# © Copyright Olivier Planson - 2025
#!/bin/bash

# Podman-based testing for Lab 1 with Open Liberty
# No local Liberty installation required!

set -e

echo "=========================================="
echo "Lab 1: Podman + Open Liberty Testing"
echo "=========================================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

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

# Navigate to solution
cd solution

echo "Building application..."
echo "----------------------------"
mvn clean package -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
    echo "  WAR file: target/banking-app.war"
else
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

echo ""

echo "Building Podman image..."
echo "----------------------------"
podman build -t banking-app:lab01 -f Containerfile .

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Podman image built${NC}"
else
    echo -e "${RED}❌ Podman build failed${NC}"
    exit 1
fi

echo ""

# Stop existing container if running
if podman ps -a | grep -q banking-app-lab01; then
    echo "Stopping existing container..."
    podman stop banking-app-lab01 > /dev/null 2>&1 || true
    podman rm banking-app-lab01 > /dev/null 2>&1 || true
fi

echo "Starting container with Open Liberty..."
echo "----------------------------"
podman run -d \
    --name banking-app-lab01 \
    -p 9080:9080 \
    -p 9443:9443 \
    banking-app:lab01

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Container started${NC}"
else
    echo -e "${RED}❌ Container failed to start${NC}"
    exit 1
fi

echo ""
echo "Waiting for Open Liberty to start..."
echo "----------------------------"

# Wait for container to be healthy
TIMEOUT=60
ELAPSED=0
LIBERTY_STARTED=false

while [ $ELAPSED -lt $TIMEOUT ]; do
    # Check if container is still running
    if ! podman ps | grep -q banking-app-lab01; then
        echo ""
        echo -e "${RED}❌ Container stopped unexpectedly${NC}"
        echo ""
        echo "Container logs:"
        echo "----------------------------"
        podman logs banking-app-lab01 | tail -50
        exit 1
    fi
    
    # Try to connect to the application
    if curl -s -f http://localhost:9080/ > /dev/null 2>&1; then
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
    podman ps -a | grep banking-app-lab01
    echo ""
    echo "Container logs (last 50 lines):"
    echo "----------------------------"
    podman logs banking-app-lab01 | tail -50
    echo ""
    echo -e "${YELLOW}Tip: Check for port conflicts or configuration errors${NC}"
    exit 1
fi

# Verify the port is correctly configured
echo ""
echo "Verifying server configuration..."
echo "----------------------------"

# Check if server is listening on port 9080
if curl -s -I http://localhost:9080/ | head -1 | grep -q "200\|302"; then
    echo -e "${GREEN}✓ Server responding on port 9080${NC}"
else
    echo -e "${RED}❌ Server not responding correctly on port 9080${NC}"
    echo ""
    echo "Checking container logs for port configuration..."
    podman logs banking-app-lab01 | grep -i "port\|endpoint\|listening" | tail -10
    exit 1
fi

echo ""
echo "=========================================="
echo "Application Ready!"
echo "=========================================="
echo ""
echo -e "${BLUE}Application URLs:${NC}"
echo "  🏠 Home:     http://localhost:9080/"
echo "  👋 Welcome:  http://localhost:9080/welcome"
echo "  👥 Clients:  http://localhost:9080/clients"
echo "  ➕ Add:      http://localhost:9080/add-client.html"
echo ""
echo -e "${BLUE}MicroProfile Endpoints:${NC}"
echo "  💊 Health:   http://localhost:9080/health"
echo "  📊 Metrics:  http://localhost:9080/metrics"
echo "  📖 OpenAPI:  http://localhost:9080/openapi"
echo ""

# Wait a moment
sleep 3

echo "Testing endpoints..."
echo "----------------------------"

TESTS_PASSED=0
TESTS_FAILED=0

# Test home page
echo -n "Testing home page... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:9080/)
if [ "$HTTP_CODE" = "200" ] && grep -q "Banking Application" /tmp/response.txt; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test welcome servlet
echo -n "Testing welcome servlet... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:9080/welcome)
if [ "$HTTP_CODE" = "200" ] && grep -q "Welcome" /tmp/response.txt; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test clients servlet
echo -n "Testing clients servlet... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:9080/clients)
if [ "$HTTP_CODE" = "200" ] && grep -q "Client" /tmp/response.txt; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test health endpoint
echo -n "Testing health endpoint... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:9080/health)
if [ "$HTTP_CODE" = "200" ] && grep -q "UP" /tmp/response.txt; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test metrics endpoint
echo -n "Testing metrics endpoint... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:9080/metrics)
if [ "$HTTP_CODE" = "200" ] && grep -q "base" /tmp/response.txt; then
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
    echo "  podman logs banking-app-lab01"
fi

echo ""
echo "=========================================="
echo "Podman Commands"
echo "=========================================="
echo ""
echo "View logs:"
echo "  podman logs -f banking-app-lab01"
echo ""
echo "Stop container:"
echo "  podman stop banking-app-lab01"
echo ""
echo "Remove container:"
echo "  podman rm banking-app-lab01"
echo ""
echo "Remove image:"
echo "  podman rmi banking-app:lab01"
echo ""
echo "Restart container:"
echo "  podman restart banking-app-lab01"
echo ""

# Try to open browser
if command -v open &> /dev/null; then
    echo "Opening browser..."
    open http://localhost:9080/
elif command -v xdg-open &> /dev/null; then
    echo "Opening browser..."
    xdg-open http://localhost:9080/
fi

echo ""
echo -e "${GREEN}✓ Lab 1 is running with Podman + Open Liberty!${NC}"
echo ""

# Made with Bob
