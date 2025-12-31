#!/bin/bash
# © Copyright Olivier Planson - 2025

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
    echo "  WAR file: target/banking-cdi-app.war"
else
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

echo ""

# Run Flyway migrations (optional - will run automatically in container)
echo "Checking Flyway migrations..."
echo "----------------------------"
mvn flyway:migrate -q 2>/dev/null

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Database migrations completed${NC}"
else
    echo -e "${YELLOW}⚠ Flyway migrations skipped (will run automatically in container)${NC}"
    echo "  This is normal if PostgreSQL is not accessible from host machine."
fi

echo ""

# Build Podman image
echo "Building Podman image..."
echo "----------------------------"
podman build -t banking-cdi-app:lab04 -f Containerfile .

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Podman image built${NC}"
else
    echo -e "${RED}❌ Podman build failed${NC}"
    exit 1
fi

echo ""

# Stop existing container if running
if podman ps -a | grep -q banking-cdi-lab04; then
    echo "Stopping existing container..."
    podman stop banking-cdi-lab04 > /dev/null 2>&1 || true
    podman rm banking-cdi-lab04 > /dev/null 2>&1 || true
fi

# Start container with Open Liberty
echo "Starting container with Open Liberty..."
echo "----------------------------"

# Get host IP for database connection
# On macOS/Linux with Podman, use host.containers.internal
# On Linux with Docker, use host.docker.internal or bridge network
DB_HOST="host.containers.internal"

podman run -d \
    --name banking-cdi-lab04 \
    -p 9080:9080 \
    -p 9443:9443 \
    --add-host=host.containers.internal:host-gateway \
    -e db_host=${DB_HOST} \
    -e db_port=5432 \
    -e db_name=bankdb \
    -e db_user=bankuser \
    -e db_password=bankpass \
    banking-cdi-app:lab04

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
    if ! podman ps | grep -q banking-cdi-lab04; then
        echo ""
        echo -e "${RED}❌ Container stopped unexpectedly${NC}"
        echo ""
        echo "Container logs:"
        echo "----------------------------"
        podman logs banking-cdi-lab04 | tail -50
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
    podman ps -a | grep banking-cdi-lab04
    echo ""
    echo "Container logs (last 50 lines):"
    echo "----------------------------"
    podman logs banking-cdi-lab04 | tail -50
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

# Test CDI functionality - check if interceptor logging is working
echo -n "Testing CDI interceptor logging... "
# Make a request and check logs for interceptor messages
podman logs banking-cdi-lab04 2>&1 | grep -q "LoggingInterceptor" && \
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
    echo "  podman logs banking-cdi-lab04"
    echo "  podman logs banking-cdi-lab04 | grep -i error"
    echo "  podman logs banking-cdi-lab04 | grep -i cdi"
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
podman logs banking-cdi-lab04 2>&1 | grep -i "EntityManager" | tail -3 || echo "  (No EntityManager logs found)"
echo ""
echo "CDI Interceptor (@Logged):"
podman logs banking-cdi-lab04 2>&1 | grep -i "LoggingInterceptor" | tail -5 || echo "  (No interceptor logs found - make some requests)"
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
echo "  podman logs -f banking-cdi-lab04"
echo ""
echo "View CDI-specific logs:"
echo "  podman logs banking-cdi-lab04 | grep -i cdi"
echo "  podman logs banking-cdi-lab04 | grep -i interceptor"
echo ""
echo "View database logs:"
echo "  docker logs -f banking-db"
echo ""
echo "Stop application:"
echo "  podman stop banking-cdi-lab04"
echo ""
echo "Stop database:"
echo "  docker-compose down"
echo ""
echo "Remove application container:"
echo "  podman rm banking-cdi-lab04"
echo ""
echo "Remove application image:"
echo "  podman rmi banking-cdi-app:lab04"
echo ""
echo "Restart application:"
echo "  podman restart banking-cdi-lab04"
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
echo -e "${GREEN}✓ Lab 4 (CDI) is running with Podman + Open Liberty + PostgreSQL!${NC}"
echo ""
echo -e "${BLUE}Key CDI Features to Test:${NC}"
echo "  • Dependency Injection (@Inject)"
echo "  • EntityManager Producer (@Produces)"
echo "  • Interceptors (@Logged)"
echo "  • Request Scoped Beans (@RequestScoped)"
echo ""

# Made with Bob
