#!/bin/bash
# © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
# Docker-based testing for Lab 02 with Open Liberty
# Alternative to Podman

set -e

echo "=========================================="
echo "Lab 02: Docker + Open Liberty Testing"
echo "Servlets, JSP & MicroProfile"
echo "=========================================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker not found${NC}"
    echo ""
    echo "Please install Docker:"
    echo "  macOS: Download Docker Desktop from https://www.docker.com/products/docker-desktop"
    echo "  Linux: sudo apt-get install docker.io"
    echo "  Windows: Download Docker Desktop from https://www.docker.com/products/docker-desktop"
    echo ""
    echo -e "${BLUE}Alternative: Use Podman instead${NC}"
    echo "  ./podman-test.sh"
    exit 1
fi

echo -e "${GREEN}✓ Docker found${NC}"
docker --version
echo ""

# Navigate to starter directory
cd starter

echo "Building application..."
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

echo "Building Docker image..."
echo "----------------------------"
docker build -t banking-web-app:lab02 -f Containerfile .

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Docker image built${NC}"
else
    echo -e "${RED}❌ Docker build failed${NC}"
    exit 1
fi

echo ""

# Stop existing container if running
if docker ps -a | grep -q banking-web-lab02; then
    echo "Stopping existing container..."
    docker stop banking-web-lab02 > /dev/null 2>&1 || true
    docker rm banking-web-lab02 > /dev/null 2>&1 || true
fi

echo "Starting container with Open Liberty..."
echo "----------------------------"
docker run -d \
    --name banking-web-lab02 \
    -p 9080:9080 \
    -p 9443:9443 \
    banking-web-app:lab02

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
    if ! docker ps | grep -q banking-web-lab02; then
        echo ""
        echo -e "${RED}❌ Container stopped unexpectedly${NC}"
        echo ""
        echo "Container logs:"
        echo "----------------------------"
        docker logs banking-web-lab02 | tail -50
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
    docker ps -a | grep banking-web-lab02
    echo ""
    echo "Container logs (last 50 lines):"
    echo "----------------------------"
    docker logs banking-web-lab02 | tail -50
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
echo ""

# Wait a moment
sleep 3

echo "Testing endpoints..."
echo "----------------------------"

TESTS_PASSED=0
TESTS_FAILED=0

# Test home page
echo -n "Testing home page... "
if curl -s http://localhost:9080/ | grep -q "Banking"; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test clients page
echo -n "Testing clients page... "
if curl -s http://localhost:9080/clients | grep -q "Client"; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test health endpoint
echo -n "Testing health endpoint... "
if curl -s http://localhost:9080/health | grep -q "UP"; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

echo ""
echo "Test Results: ${GREEN}$TESTS_PASSED passed${NC}, ${RED}$TESTS_FAILED failed${NC}"

echo ""
echo "=========================================="
echo "Docker Commands"
echo "=========================================="
echo ""
echo "View logs:"
echo "  docker logs -f banking-web-lab02"
echo ""
echo "Stop container:"
echo "  docker stop banking-web-lab02"
echo ""
echo "Remove container:"
echo "  docker rm banking-web-lab02"
echo ""
echo "Remove image:"
echo "  docker rmi banking-web-app:lab02"
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
echo -e "${GREEN}✓ Lab 02 is running with Docker + Open Liberty!${NC}"
echo ""

# Made with Bob