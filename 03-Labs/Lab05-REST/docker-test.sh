#!/bin/bash
# © Copyright Olivier Planson - 2025

# Docker-based testing for Lab 5 with Open Liberty and PostgreSQL
# No local Liberty installation required!

set -e

echo "=========================================="
echo "Lab 5: REST API - Docker + Open Liberty + PostgreSQL"
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
    echo "  macOS/Windows: https://www.docker.com/products/docker-desktop"
    echo "  Linux: sudo apt-get install docker.io"
    exit 1
fi

echo -e "${GREEN}✓ Docker found${NC}"
docker --version
echo ""

# Navigate to solution
cd solution

# Start PostgreSQL with docker-compose
echo "Starting PostgreSQL..."
echo "----------------------------"
if ! docker-compose ps | grep -q "banking-db.*Up"; then
    docker-compose up -d
    
    echo "Waiting for PostgreSQL to be ready..."
    for i in {1..30}; do
        if docker exec banking-db pg_isready -U bankuser -d bankdb > /dev/null 2>&1; then
            echo -e "${GREEN}✓ PostgreSQL is ready${NC}"
            break
        fi
        sleep 2
        echo -n "."
    done
    echo ""
else
    echo -e "${GREEN}✓ PostgreSQL already running${NC}"
fi

echo ""

# Build application
echo "Building application..."
echo "----------------------------"
mvn clean package -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
    echo "  WAR file: target/banking-rest-app.war"
else
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

echo ""

# Build Docker image
echo "Building Docker image..."
echo "----------------------------"
docker build -t banking-rest-app:lab05 -f Containerfile . -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Docker image built${NC}"
else
    echo -e "${RED}❌ Docker build failed${NC}"
    exit 1
fi

echo ""

# Stop existing container if running
if docker ps -a | grep -q banking-rest-lab05; then
    echo "Stopping existing container..."
    docker stop banking-rest-lab05 > /dev/null 2>&1 || true
    docker rm banking-rest-lab05 > /dev/null 2>&1 || true
fi

# Start container with Open Liberty
echo "Starting container with Open Liberty..."
echo "----------------------------"
docker run -d \
    --name banking-rest-lab05 \
    -p 9080:9080 \
    -p 9443:9443 \
    --network host \
    banking-rest-app:lab05

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
TIMEOUT=90
ELAPSED=0
LIBERTY_STARTED=false

while [ $ELAPSED -lt $TIMEOUT ]; do
    # Check if container is still running
    if ! docker ps | grep -q banking-rest-lab05; then
        echo ""
        echo -e "${RED}❌ Container stopped unexpectedly${NC}"
        echo ""
        echo "Container logs:"
        echo "----------------------------"
        docker logs banking-rest-lab05 | tail -50
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
    docker ps -a | grep banking-rest-lab05
    echo ""
    echo "Container logs (last 50 lines):"
    echo "----------------------------"
    docker logs banking-rest-lab05 | tail -50
    echo ""
    echo -e "${YELLOW}Tip: Check for database connection or Flyway migration errors${NC}"
    exit 1
fi

# Wait for Flyway migrations
echo ""
echo "Waiting for Flyway migrations..."
sleep 5

# Check if migrations ran
echo "Checking database schema..."
if docker exec banking-db psql -U bankuser -d bankdb -c "\dt" | grep -q "clients"; then
    echo -e "${GREEN}✓ Database tables created${NC}"
else
    echo -e "${YELLOW}⚠ Database tables not found${NC}"
    echo "Check container logs for Flyway errors:"
    echo "  docker logs banking-rest-lab05 | grep -i flyway"
fi

echo ""
echo "=========================================="
echo "Application Ready!"
echo "=========================================="
echo ""
echo -e "${BLUE}Application URLs:${NC}"
echo "  🏠 Home:        http://localhost:9080/"
echo "  👥 Clients:     http://localhost:9080/clients"
echo "  🔌 REST API:    http://localhost:9080/api"
echo ""
echo -e "${BLUE}REST API Endpoints:${NC}"
echo "  GET /api/clients           - List all clients"
echo "  GET /api/clients/{id}      - Get client by ID"
echo "  POST /api/clients          - Create client"
echo "  GET /api/accounts          - List all accounts"
echo "  GET /api/accounts/{id}     - Get account by ID"
echo ""
echo -e "${BLUE}MicroProfile Endpoints:${NC}"
echo "  💊 Health:   http://localhost:9080/health"
echo "  📊 Metrics:  http://localhost:9080/metrics"
echo ""
echo -e "${BLUE}Database:${NC}"
echo "  Connect:     docker exec -it banking-db psql -U bankuser -d bankdb"
echo "  View tables: docker exec banking-db psql -U bankuser -d bankdb -c '\dt'"
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
if [ "$HTTP_CODE" = "200" ] && grep -q "Banking" /tmp/response.txt; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test clients endpoint
echo -n "Testing clients endpoint... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:9080/clients)
if [ "$HTTP_CODE" = "200" ]; then
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

# Test REST API - GET clients
echo -n "Testing REST API (GET /api/clients)... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:9080/api/clients)
if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test REST API - POST client
echo -n "Testing REST API (POST /api/clients)... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" -X POST http://localhost:9080/api/clients \
    -H "Content-Type: application/json" \
    -d '{"name":"Test Client","email":"test@example.com","premium":false}')
if [ "$HTTP_CODE" = "201" ]; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test REST API - GET accounts
echo -n "Testing REST API (GET /api/accounts)... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:9080/api/accounts)
if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test database data
echo -n "Testing database data... "
CLIENT_COUNT=$(docker exec banking-db psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM clients;" 2>/dev/null | tr -d ' ')
if [ "$CLIENT_COUNT" -gt 0 ]; then
    echo -e "${GREEN}✓ PASS ($CLIENT_COUNT clients)${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (no clients found)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Cleanup
rm -f /tmp/response.txt

echo ""
echo "Test Results: ${GREEN}$TESTS_PASSED passed${NC}, ${RED}$TESTS_FAILED failed${NC}"

if [ $TESTS_FAILED -gt 0 ]; then
    echo ""
    echo -e "${YELLOW}⚠ Some tests failed. Check logs:${NC}"
    echo "  docker logs banking-rest-lab05"
    echo "  docker logs banking-rest-lab05 | grep -i error"
    echo "  docker logs banking-rest-lab05 | grep -i rest"
fi

echo ""
echo "=========================================="
echo "Database Queries"
echo "=========================================="
echo ""
echo "View all clients:"
docker exec banking-db psql -U bankuser -d bankdb -c "SELECT id, name, email FROM clients;" 2>/dev/null || echo "  (Run after first deployment)"
echo ""
echo "View all accounts:"
docker exec banking-db psql -U bankuser -d bankdb -c "SELECT id, number, balance, type, client_id FROM accounts;" 2>/dev/null || echo "  (Run after first deployment)"
echo ""

echo "=========================================="
echo "Management Commands"
echo "=========================================="
echo ""
echo "View application logs:"
echo "  docker logs -f banking-rest-lab05"
echo ""
echo "View database logs:"
echo "  docker logs -f banking-db"
echo ""
echo "Stop application:"
echo "  docker stop banking-rest-lab05"
echo ""
echo "Stop database:"
echo "  docker-compose down"
echo ""
echo "Remove application container:"
echo "  docker rm banking-rest-lab05"
echo ""
echo "Remove application image:"
echo "  docker rmi banking-rest-app:lab05"
echo ""
echo "Restart application:"
echo "  docker restart banking-rest-lab05"
echo ""
echo "Test REST API with curl:"
echo "  curl http://localhost:9080/api/clients"
echo "  curl http://localhost:9080/api/accounts"
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
echo -e "${GREEN}✓ Lab 5 (REST API) is running with Docker + Open Liberty + PostgreSQL!${NC}"
echo ""

# Made with Bob