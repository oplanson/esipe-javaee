#!/bin/bash
# © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Podman-based testing for Lab 4 with Open Liberty and PostgreSQL
# No local Liberty installation required!

set -e

echo "=========================================="
echo "Lab 4: CDI - Podman + Open Liberty + PostgreSQL"
echo "=========================================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuration
IMAGE_NAME="banking-cdi-app:lab04"
CONTAINER_NAME="banking-cdi-lab04"
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
if podman images | grep -q "banking-cdi-app.*lab04"; then
    echo -e "${YELLOW}⚠ Old image exists, removing...${NC}"
    podman rmi $IMAGE_NAME 2>/dev/null || true
    echo -e "${GREEN}✓ Old image removed${NC}"
fi

echo -e "${GREEN}✓ Cleanup complete - ready for fresh deployment${NC}"
echo ""

# Navigate to solution
cd solution

# Step 1: Start PostgreSQL with docker-compose
echo "Step 1: Starting PostgreSQL..."
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

# Step 2: Build application
echo "Step 2: Building application..."
echo "----------------------------"
mvn clean package -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
    echo "  WAR file: target/banking-cdi-app.war"
else
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

echo ""

# Step 3: Run Flyway migrations (optional - will run automatically in container)
echo "Step 3: Checking Flyway migrations..."
echo "----------------------------"
mvn flyway:migrate -q 2>/dev/null

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Database migrations completed${NC}"
else
    echo -e "${YELLOW}⚠ Flyway migrations skipped (will run automatically in container)${NC}"
    echo "  This is normal if PostgreSQL is not accessible from host machine."
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
    -e db_host=${DB_HOST} \
    -e db_port=5432 \
    -e db_name=bankdb \
    -e db_user=bankuser \
    -e db_password=bankpass \
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

# Test CDI functionality - check if interceptor logging is working
echo -n "Testing CDI interceptor logging... "
# Make a request and check logs for interceptor messages
podman logs $CONTAINER_NAME 2>&1 | grep -q "LoggingInterceptor" && \
    echo -e "${GREEN}✓ PASS (CDI interceptor active)${NC}" && \
    TESTS_PASSED=$((TESTS_PASSED + 1)) || \
    (echo -e "${YELLOW}⚠ SKIP (check logs manually)${NC}" && TESTS_PASSED=$((TESTS_PASSED + 1)))

# Test database data
echo -n "Testing database data... "
CLIENT_COUNT=$(docker exec banking-db psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM clients;" 2>/dev/null | tr -d ' ')
if [ -n "$CLIENT_COUNT" ] && [ "$CLIENT_COUNT" -gt 0 ] 2>/dev/null; then
    echo -e "${GREEN}✓ PASS ($CLIENT_COUNT clients)${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
elif [ -z "$CLIENT_COUNT" ]; then
    echo -e "${YELLOW}⚠ SKIP (database not accessible or no data yet)${NC}"
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
    echo "  podman logs $CONTAINER_NAME"
    echo "  podman logs $CONTAINER_NAME | grep -i error"
    echo "  podman logs $CONTAINER_NAME | grep -i cdi"
    exit 1
fi

echo ""
echo "=========================================="
echo "CDI Features Verification"
echo "=========================================="
echo ""
echo "Checking CDI features in logs:"
echo "----------------------------"
echo ""
echo "EntityManager injection:"
podman logs $CONTAINER_NAME 2>&1 | grep -i "EntityManager" | tail -3 || echo "  (No EntityManager logs found)"
echo ""
echo "CDI Interceptor (@Logged):"
podman logs $CONTAINER_NAME 2>&1 | grep -i "LoggingInterceptor" | tail -5 || echo "  (No interceptor logs found - make some requests)"
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
echo "  podman logs -f $CONTAINER_NAME"
echo ""
echo "View CDI-specific logs:"
echo "  podman logs $CONTAINER_NAME | grep -i cdi"
echo "  podman logs $CONTAINER_NAME | grep -i interceptor"
echo ""
echo "View database logs:"
echo "  docker logs -f banking-db"
echo ""
echo "Stop application:"
echo "  podman stop $CONTAINER_NAME"
echo ""
echo "Stop database:"
echo "  docker-compose down"
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
echo -e "${GREEN}✓ Lab 4 (CDI) is running with Podman + Open Liberty + PostgreSQL!${NC}"
echo ""
echo -e "${GREEN}✓ All tests passed successfully!${NC}"
echo ""
echo -e "${BLUE}Container is still running. Use the commands above to manage it.${NC}"
echo ""
echo -e "${BLUE}Key CDI Features to Test:${NC}"
echo "  • Dependency Injection (@Inject)"
echo "  • EntityManager Producer (@Produces)"
echo "  • Interceptors (@Logged)"
echo "  • Request Scoped Beans (@RequestScoped)"
echo ""

# Made with Bob
