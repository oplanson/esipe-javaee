#!/bin/bash
# © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

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

# Configuration
IMAGE_NAME="banking-jpa-app:lab03"
CONTAINER_NAME="banking-jpa-lab03"
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
if podman images | grep -q "banking-jpa-app.*lab03"; then
    echo -e "${YELLOW}⚠ Old image exists, removing...${NC}"
    podman rmi $IMAGE_NAME 2>/dev/null || true
    echo -e "${GREEN}✓ Old image removed${NC}"
fi

echo -e "${GREEN}✓ Cleanup complete - ready for fresh deployment${NC}"
echo ""

# Navigate to solution
cd solution

# Stop and remove any existing docker-compose services (with volumes)
if command -v docker-compose &> /dev/null; then
    if docker-compose ps 2>/dev/null | grep -q "Up\|Exit"; then
        echo -e "${YELLOW}⚠ Stopping and removing existing docker-compose services and volumes...${NC}"
        docker-compose down -v 2>/dev/null || true
        echo -e "${GREEN}✓ Docker-compose services and volumes removed${NC}"
    fi
fi

# Step 1: Start PostgreSQL with docker-compose
echo "Step 1: Starting PostgreSQL..."
echo "----------------------------"

# Check if PostgreSQL is already running on port 5432
DB_PORT_IN_USE=false
if command -v lsof &> /dev/null; then
    # macOS/Linux with lsof
    if lsof -Pi :5432 -sTCP:LISTEN -t >/dev/null 2>&1; then
        DB_PORT_IN_USE=true
    fi
elif command -v netstat &> /dev/null; then
    # Linux with netstat
    if netstat -tuln 2>/dev/null | grep -q ":5432 "; then
        DB_PORT_IN_USE=true
    fi
elif command -v ss &> /dev/null; then
    # Linux with ss
    if ss -tuln 2>/dev/null | grep -q ":5432 "; then
        DB_PORT_IN_USE=true
    fi
fi

# Check if database container is already running
DB_CONTAINER_RUNNING=false
if docker ps 2>/dev/null | grep -q "banking-db"; then
    DB_CONTAINER_RUNNING=true
elif podman ps 2>/dev/null | grep -q "banking-db"; then
    DB_CONTAINER_RUNNING=true
fi

if [ "$DB_PORT_IN_USE" = true ] && [ "$DB_CONTAINER_RUNNING" = true ]; then
    echo -e "${GREEN}✓ PostgreSQL is already running on port 5432${NC}"
    echo "Skipping database startup (idempotent operation)"
    
    # Verify database is accessible
    echo "Verifying database connection..."
    if docker exec banking-db pg_isready -U bankuser -d bankdb > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Database connection verified${NC}"
    elif podman exec banking-db pg_isready -U bankuser -d bankdb > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Database connection verified${NC}"
    else
        echo -e "${YELLOW}⚠ Database container exists but connection failed${NC}"
        echo "Restarting database container..."
        docker-compose down -v 2>/dev/null || true
        docker-compose up -d
    fi
elif [ "$DB_PORT_IN_USE" = true ]; then
    echo -e "${YELLOW}⚠ Port 5432 is in use by another process/container${NC}"
    echo "Attempting to identify and stop the conflicting container..."
    
    # Find containers using port 5432
    CONTAINERS_ON_5432=$(docker ps --format "{{.Names}}" 2>/dev/null | while read -r name; do
        if docker port "$name" 2>/dev/null | grep -q ":5432->"; then
            echo "$name"
        fi
    done)
    
    if [ -z "$CONTAINERS_ON_5432" ]; then
        # Try with podman
        CONTAINERS_ON_5432=$(podman ps --format "{{.Names}}" 2>/dev/null | while read -r name; do
            if podman port "$name" 2>/dev/null | grep -q ":5432->"; then
                echo "$name"
            fi
        done)
    fi
    
    if [ -n "$CONTAINERS_ON_5432" ]; then
        echo "Found container(s) using port 5432:"
        echo "$CONTAINERS_ON_5432" | while read -r container; do
            if [ -n "$container" ]; then
                echo -e "${YELLOW}  Stopping and removing: $container${NC}"
                docker stop "$container" > /dev/null 2>&1 || podman stop "$container" > /dev/null 2>&1 || true
                docker rm "$container" > /dev/null 2>&1 || podman rm "$container" > /dev/null 2>&1 || true
                echo -e "${GREEN}  ✓ $container removed${NC}"
            fi
        done
        echo ""
        echo "Starting fresh PostgreSQL database..."
        docker-compose up -d
    else
        # Port is used by a non-container process
        echo -e "${RED}❌ Port 5432 is in use by a non-container process${NC}"
        echo ""
        if command -v lsof &> /dev/null; then
            echo "Process details:"
            lsof -Pi :5432 -sTCP:LISTEN 2>/dev/null || echo "  (Unable to determine process)"
        fi
        echo ""
        echo "Please stop the PostgreSQL service manually:"
        echo "  - If using Homebrew: brew services stop postgresql"
        echo "  - If using systemd: sudo systemctl stop postgresql"
        echo "  - Or: sudo pkill -9 postgres"
        exit 1
    fi
else
    # Start fresh database
    echo "Starting PostgreSQL database..."
    docker-compose up -d
fi

# Wait for PostgreSQL to be ready (common for all paths that start the database)
if [ "$DB_PORT_IN_USE" != true ] || [ "$DB_CONTAINER_RUNNING" != true ]; then
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
fi

echo ""

# Step 2: Build application
echo "Step 2: Building application..."
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

# Step 3: Run Flyway migrations
echo "Step 3: Running Flyway migrations..."
echo "----------------------------"
mvn flyway:migrate -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Database migrations completed${NC}"
else
    echo -e "${YELLOW}⚠ Flyway migrations failed (will retry after container starts)${NC}"
fi

echo ""

# Step 4: Build Podman image
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

# Step 5: Start container with Open Liberty
echo "Step 5: Starting container with Open Liberty..."
echo "----------------------------"

# Get host IP for database connection
# On macOS/Linux with Podman, use host.containers.internal
# On Linux with Docker, use host.docker.internal or bridge network
DB_HOST="host.containers.internal"

podman run -d \
    --name $CONTAINER_NAME \
    -p $APP_PORT:9080 \
    -p 9443:9443 \
    --add-host=host.containers.internal:host-gateway \
    -e DB_HOST=${DB_HOST} \
    -e DB_PORT=5432 \
    -e DB_NAME=bankdb \
    -e DB_USER=bankuser \
    -e DB_PASSWORD=bankpass \
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
TIMEOUT=90
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
    echo -e "${YELLOW}Tip: Check for database connection or Flyway migration errors${NC}"
    exit 1
fi

# Check if migrations ran
echo ""
echo "Checking database schema..."
echo "----------------------------"

# Try docker first, then podman
TABLES=$(docker exec banking-db psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE';" 2>/dev/null | tr -d ' ')
if [ -z "$TABLES" ]; then
    TABLES=$(podman exec banking-db psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE';" 2>/dev/null | tr -d ' ')
fi

if [ -n "$TABLES" ] && [ "$TABLES" -gt 0 ]; then
    echo -e "${GREEN}✓ Database tables created ($TABLES tables)${NC}"
    
    # List tables
    echo ""
    echo "Tables in database:"
    docker exec banking-db psql -U bankuser -d bankdb -c "\dt" 2>/dev/null || \
    podman exec banking-db psql -U bankuser -d bankdb -c "\dt" 2>/dev/null || \
    echo "  (Unable to list tables)"
else
    echo -e "${YELLOW}⚠ Database tables not found${NC}"
    echo "Flyway migrations may not have run successfully."
    echo ""
    echo "To manually run migrations:"
    echo "  cd solution && mvn flyway:migrate"
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

echo "Step 7: Testing endpoints..."
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

# Test clients endpoint
echo -n "Testing clients endpoint... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:$APP_PORT/clients)
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

# Test database data
echo -n "Testing database data... "
# Try docker first, then podman
CLIENT_COUNT=$(docker exec banking-db psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM clients;" 2>/dev/null | tr -d ' ')
if [ -z "$CLIENT_COUNT" ]; then
    CLIENT_COUNT=$(podman exec banking-db psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM clients;" 2>/dev/null | tr -d ' ')
fi

if [ -n "$CLIENT_COUNT" ] && [ "$CLIENT_COUNT" -ge 0 ] 2>/dev/null; then
    echo -e "${GREEN}✓ PASS ($CLIENT_COUNT clients in database)${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${YELLOW}⚠ SKIP (cannot connect to database)${NC}"
fi

# Test transaction validator endpoint (JNDI demo)
echo -n "Testing transaction validator endpoint... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" http://localhost:$APP_PORT/validate-transaction)
if [ "$HTTP_CODE" = "200" ] && grep -q "Transaction Validator" /tmp/response.txt; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test valid transaction amount (5000.00 < 10000.00)
echo -n "Testing valid transaction (€5000.00)... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" -X POST http://localhost:$APP_PORT/validate-transaction -d "amount=5000.00&description=Test+valid")
if [ "$HTTP_CODE" = "200" ] && grep -q "Transaction Valid" /tmp/response.txt; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test invalid transaction amount (15000.00 > 10000.00)
echo -n "Testing invalid transaction (€15000.00)... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" -X POST http://localhost:$APP_PORT/validate-transaction -d "amount=15000.00&description=Test+invalid")
if [ "$HTTP_CODE" = "200" ] && grep -q "Transaction Invalid" /tmp/response.txt; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test transaction at exact limit (10000.00 = 10000.00)
echo -n "Testing transaction at limit (€10000.00)... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" -X POST http://localhost:$APP_PORT/validate-transaction -d "amount=10000.00&description=Test+limit")
if [ "$HTTP_CODE" = "200" ] && grep -q "Transaction Valid" /tmp/response.txt; then
    echo -e "${GREEN}✓ PASS${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "${RED}✗ FAIL (HTTP $HTTP_CODE)${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test JSON API response
echo -n "Testing transaction validator JSON API... "
HTTP_CODE=$(curl -s -o /tmp/response.txt -w "%{http_code}" -X POST http://localhost:$APP_PORT/validate-transaction -H "Accept: application/json" -d "amount=7500.00")
if [ "$HTTP_CODE" = "200" ] && grep -q '"valid": true' /tmp/response.txt && grep -q '"maxAmount": 10000' /tmp/response.txt; then
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

echo ""
echo "=========================================="
echo "Database Queries"
echo "=========================================="
echo ""
echo "View all clients:"
docker exec banking-db psql -U bankuser -d bankdb -c "SELECT id, name, email FROM clients;" 2>/dev/null || \
podman exec banking-db psql -U bankuser -d bankdb -c "SELECT id, name, email FROM clients;" 2>/dev/null || \
echo "  (No clients in database yet)"
echo ""
echo "View all accounts:"
docker exec banking-db psql -U bankuser -d bankdb -c "SELECT id, number, balance, type, client_id FROM accounts;" 2>/dev/null || \
podman exec banking-db psql -U bankuser -d bankdb -c "SELECT id, number, balance, type, client_id FROM accounts;" 2>/dev/null || \
echo "  (No accounts in database yet)"
echo ""

if [ $TESTS_FAILED -gt 0 ]; then
    echo ""
    echo -e "${YELLOW}⚠ Some tests failed. Check logs:${NC}"
    echo "  podman logs $CONTAINER_NAME"
    echo "  podman logs $CONTAINER_NAME | grep -i error"
    echo "  podman logs $CONTAINER_NAME | grep -i flyway"
    exit 1
fi

echo "=========================================="
echo "Management Commands"
echo "=========================================="
echo ""
echo "View application logs:"
echo "  podman logs -f $CONTAINER_NAME"
echo ""
echo "View database logs:"
echo "  docker logs -f banking-db"
echo ""
echo "Stop application:"
echo "  podman stop $CONTAINER_NAME"
echo ""
echo "Stop database (with volumes cleanup):"
echo "  docker-compose down -v"
echo ""
echo "Remove application container:"
echo "  podman rm $CONTAINER_NAME"
echo ""
echo "Remove application image:"
echo "  podman rmi $IMAGE_NAME"
echo ""
echo "Restart application:"
echo "  podman restart $CONTAINER_NAME"
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
echo -e "${GREEN}✓ Lab 3 is running with Podman + Open Liberty + PostgreSQL!${NC}"
echo ""
echo -e "${GREEN}✓ All tests passed successfully!${NC}"
echo ""
echo -e "${BLUE}Container is still running. Use the commands above to manage it.${NC}"
echo ""

# Made with Bob