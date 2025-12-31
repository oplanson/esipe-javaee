#!/bin/bash
# © Copyright Olivier Planson - 2025

# Podman-based testing for Lab 3 with Open Liberty and PostgreSQL
# No local Liberty installation required!

set -e

echo "=========================================="
echo "Lab 3: Podman + Open Liberty + PostgreSQL"
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
    echo "  WAR file: target/banking-jpa-app.war"
else
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

echo ""

# Run Flyway migrations
echo "Running Flyway migrations..."
echo "----------------------------"
mvn flyway:migrate -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Database migrations completed${NC}"
else
    echo -e "${YELLOW}⚠ Flyway migrations failed (will retry after container starts)${NC}"
fi

echo ""

# Build Podman image
echo "Building Podman image..."
echo "----------------------------"
podman build -t banking-jpa-app:lab03 -f Containerfile .

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Podman image built${NC}"
else
    echo -e "${RED}❌ Podman build failed${NC}"
    exit 1
fi

echo ""

# Stop existing container if running
if podman ps -a | grep -q banking-jpa-lab03; then
    echo "Stopping existing container..."
    podman stop banking-jpa-lab03 > /dev/null 2>&1 || true
    podman rm banking-jpa-lab03 > /dev/null 2>&1 || true
fi

# Start container with Open Liberty
echo "Starting container with Open Liberty..."
echo "----------------------------"

# Get host IP for database connection
# On macOS/Linux with Podman, use host.containers.internal
# On Linux with Docker, use host.docker.internal or bridge network
DB_HOST="host.containers.internal"

podman run -d \
    --name banking-jpa-lab03 \
    -p 9080:9080 \
    -p 9443:9443 \
    --add-host=host.containers.internal:host-gateway \
    -e DB_HOST=${DB_HOST} \
    -e DB_PORT=5432 \
    -e DB_NAME=bankdb \
    -e DB_USER=bankuser \
    -e DB_PASSWORD=bankpass \
    banking-jpa-app:lab03

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
    if ! podman ps | grep -q banking-jpa-lab03; then
        echo ""
        echo -e "${RED}❌ Container stopped unexpectedly${NC}"
        echo ""
        echo "Container logs:"
        echo "----------------------------"
        podman logs banking-jpa-lab03 | tail -50
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
    podman ps -a | grep banking-jpa-lab03
    echo ""
    echo "Container logs (last 50 lines):"
    echo "----------------------------"
    podman logs banking-jpa-lab03 | tail -50
    echo ""
    echo -e "${YELLOW}Tip: Check for database connection or Flyway migration errors${NC}"
    exit 1
fi

# Check if migrations ran
echo ""
echo "Checking database schema..."
echo "----------------------------"

# Check if docker or podman command exists for database
if command -v docker &> /dev/null; then
    DB_CMD="docker"
elif command -v podman &> /dev/null; then
    DB_CMD="podman"
else
    echo -e "${YELLOW}⚠ Neither docker nor podman found, skipping database check${NC}"
    DB_CMD=""
fi

if [ -n "$DB_CMD" ]; then
    # Try to check tables
    TABLES=$($DB_CMD exec banking-db psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE';" 2>/dev/null | tr -d ' ')
    
    if [ -n "$TABLES" ] && [ "$TABLES" -gt 0 ]; then
        echo -e "${GREEN}✓ Database tables created ($TABLES tables)${NC}"
        
        # List tables
        echo ""
        echo "Tables in database:"
        $DB_CMD exec banking-db psql -U bankuser -d bankdb -c "\dt" 2>/dev/null || echo "  (Unable to list tables)"
    else
        echo -e "${YELLOW}⚠ Database tables not found${NC}"
        echo "Flyway migrations may not have run successfully."
        echo ""
        echo "To manually run migrations:"
        echo "  cd solution && mvn flyway:migrate"
    fi
else
    echo -e "${YELLOW}⚠ Cannot check database schema${NC}"
fi

echo ""
echo "=========================================="
echo "Application Ready!"
echo "=========================================="
echo ""
echo -e "${BLUE}Application URLs:${NC}"
echo "  🏠 Home:     http://localhost:9080/"
echo "  👥 Clients:  http://localhost:9080/clients"
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

# Test metrics endpoint
echo -n "Testing metrics endpoint... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:9080/metrics)
if [ "$HTTP_CODE" = "200" ] && grep -q "base" /tmp/response.txt; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
elif [ "$HTTP_CODE" = "401" ]; then
    # Try with authentication if required
    HTTP_CODE=$(curl -s -u admin:adminpwd -o /tmp/response.txt -w "%{http_code}" http://localhost:9080/metrics)
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
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:9080/openapi)
if [ "$HTTP_CODE" = "200" ] && grep -q "openapi" /tmp/response.txt; then
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
    echo "  podman logs banking-jpa-lab03"
    echo "  podman logs banking-jpa-lab03 | grep -i error"
    echo "  podman logs banking-jpa-lab03 | grep -i flyway"
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
echo "  podman logs -f banking-jpa-lab03"
echo ""
echo "View database logs:"
echo "  docker logs -f banking-db"
echo ""
echo "Stop application:"
echo "  podman stop banking-jpa-lab03"
echo ""
echo "Stop database:"
echo "  docker-compose down"
echo ""
echo "Remove application container:"
echo "  podman rm banking-jpa-lab03"
echo ""
echo "Remove application image:"
echo "  podman rmi banking-jpa-app:lab03"
echo ""
echo "Restart application:"
echo "  podman restart banking-jpa-lab03"
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
echo -e "${GREEN}✓ Lab 3 is running with Podman + Open Liberty + PostgreSQL!${NC}"
echo ""

# Made with Bob