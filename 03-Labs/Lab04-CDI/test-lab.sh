#!/bin/bash
# © Copyright Olivier Planson - 2025

# Lab 4 Testing Script
# Tests the complete Lab 4 solution with CDI and PostgreSQL

set -e

echo "=========================================="
echo "Lab 4: CDI and Dependency Injection - Test"
echo "=========================================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Check prerequisites
echo "Checking prerequisites..."
echo "----------------------------"

# Check Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java not found. Please install JDK 17+${NC}"
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo -e "${RED}❌ Java 17+ required. Found version: $JAVA_VERSION${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Java $JAVA_VERSION found${NC}"

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Maven not found. Please install Maven 3.8+${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Maven found${NC}"

# Check Docker/Podman for PostgreSQL
if command -v docker &> /dev/null; then
    echo -e "${GREEN}✓ Docker found${NC}"
    CONTAINER_CMD="docker"
    COMPOSE_CMD="docker-compose"
elif command -v podman &> /dev/null; then
    echo -e "${GREEN}✓ Podman found${NC}"
    CONTAINER_CMD="podman"
    COMPOSE_CMD="podman-compose"
else
    echo -e "${RED}❌ Docker or Podman required for PostgreSQL${NC}"
    exit 1
fi

echo ""

# Check PostgreSQL
echo "Checking PostgreSQL..."
echo "----------------------------"
cd solution

if ! $COMPOSE_CMD ps | grep -q "banking-db"; then
    echo -e "${YELLOW}⚠ PostgreSQL not running${NC}"
    echo "Starting PostgreSQL..."
    $COMPOSE_CMD up -d
    
    echo "Waiting for PostgreSQL to be ready..."
    for i in {1..30}; do
        if $CONTAINER_CMD exec banking-db pg_isready -U bankuser -d bankdb > /dev/null 2>&1; then
            echo -e "${GREEN}✓ PostgreSQL is ready${NC}"
            break
        fi
        sleep 2
        echo -n "."
    done
    echo ""
else
    echo -e "${GREEN}✓ PostgreSQL is running${NC}"
fi

echo ""

# Test database connection
echo "Testing database connection..."
echo "----------------------------"
if $CONTAINER_CMD exec banking-db psql -U bankuser -d bankdb -c "SELECT 1" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Database connection successful${NC}"
else
    echo -e "${RED}❌ Database connection failed${NC}"
    exit 1
fi

echo ""

# Build application
echo "Building application..."
echo "----------------------------"
mvn clean package -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
else
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

echo ""

# Check WAR file
if [ -f "target/banking-cdi-app.war" ]; then
    echo -e "${GREEN}✓ WAR file created: target/banking-cdi-app.war${NC}"
    WAR_SIZE=$(du -h target/banking-cdi-app.war | cut -f1)
    echo "  Size: $WAR_SIZE"
else
    echo -e "${RED}❌ WAR file not found${NC}"
    exit 1
fi

echo ""

# Verify key files in WAR
echo "Verifying key files in WAR..."
echo "----------------------------"

if jar -tf target/banking-cdi-app.war | grep -q "WEB-INF/classes/com/bank/model/Client.class"; then
    echo -e "${GREEN}✓ Client entity found${NC}"
else
    echo -e "${RED}❌ Client entity missing${NC}"
fi

if jar -tf target/banking-cdi-app.war | grep -q "WEB-INF/classes/com/bank/model/Account.class"; then
    echo -e "${GREEN}✓ Account entity found${NC}"
else
    echo -e "${RED}❌ Account entity missing${NC}"
fi

if jar -tf target/banking-cdi-app.war | grep -q "WEB-INF/classes/com/bank/service/ClientService.class"; then
    echo -e "${GREEN}✓ ClientService found${NC}"
else
    echo -e "${RED}❌ ClientService missing${NC}"
fi

if jar -tf target/banking-cdi-app.war | grep -q "WEB-INF/classes/META-INF/persistence.xml"; then
    echo -e "${GREEN}✓ persistence.xml found${NC}"
else
    echo -e "${RED}❌ persistence.xml missing${NC}"
fi

if jar -tf target/banking-cdi-app.war | grep -q "WEB-INF/classes/db/migration/V1__create_clients_table.sql"; then
    echo -e "${GREEN}✓ Flyway migrations found${NC}"
else
    echo -e "${RED}❌ Flyway migrations missing${NC}"
fi

echo ""

# Check database schema
echo "Checking database schema..."
echo "----------------------------"

# Check if Flyway has been run
FLYWAY_INITIALIZED=false
if $CONTAINER_CMD exec banking-db psql -U bankuser -d bankdb -c "\dt" | grep -q "flyway_schema_history"; then
    FLYWAY_INITIALIZED=true
fi

# If Flyway not initialized, run migrations
if [ "$FLYWAY_INITIALIZED" = false ]; then
    echo -e "${YELLOW}⚠ Database not initialized. Running Flyway migrations...${NC}"
    
    if mvn flyway:migrate -q -Dflyway.url=jdbc:postgresql://localhost:5432/bankdb -Dflyway.user=bankuser -Dflyway.password=bankpass; then
        echo -e "${GREEN}✓ Flyway migrations completed successfully${NC}"
        FLYWAY_INITIALIZED=true
    else
        echo -e "${RED}❌ Flyway migrations failed${NC}"
        exit 1
    fi
fi

# Check if tables exist
if $CONTAINER_CMD exec banking-db psql -U bankuser -d bankdb -c "\dt" | grep -q "clients"; then
    echo -e "${GREEN}✓ clients table exists${NC}"
    
    # Count clients
    CLIENT_COUNT=$($CONTAINER_CMD exec banking-db psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM clients;" 2>/dev/null | tr -d ' ')
    echo "  Sample data: ${CLIENT_COUNT} clients"
else
    echo -e "${RED}❌ clients table not found${NC}"
fi

if $CONTAINER_CMD exec banking-db psql -U bankuser -d bankdb -c "\dt" | grep -q "accounts"; then
    echo -e "${GREEN}✓ accounts table exists${NC}"
    
    # Count accounts
    ACCOUNT_COUNT=$($CONTAINER_CMD exec banking-db psql -U bankuser -d bankdb -t -c "SELECT COUNT(*) FROM accounts;" 2>/dev/null | tr -d ' ')
    echo "  Sample data: ${ACCOUNT_COUNT} accounts"
else
    echo -e "${RED}❌ accounts table not found${NC}"
fi

# Check Flyway history
if [ "$FLYWAY_INITIALIZED" = true ]; then
    echo -e "${GREEN}✓ Flyway schema history exists${NC}"
    echo ""
    echo "Flyway migrations:"
    $CONTAINER_CMD exec banking-db psql -U bankuser -d bankdb -c "SELECT version, description, installed_on FROM flyway_schema_history ORDER BY installed_rank;" 2>/dev/null || true
fi

echo ""

echo "=========================================="
echo "Test Summary"
echo "=========================================="
echo ""
echo "Prerequisites:  ${GREEN}✓ All checks passed${NC}"
echo "Build:          ${GREEN}✓ Success${NC}"
echo "WAR created:    ${GREEN}✓ Success${NC}"
echo "PostgreSQL:     ${GREEN}✓ Running${NC}"
echo ""
echo -e "${BLUE}Deployment Options:${NC}"
echo ""
echo "Option 1: Podman + Open Liberty + PostgreSQL (Recommended)"
echo "  Run: ./podman-test.sh"
echo "  - Builds and deploys everything in containers"
echo "  - No local installation needed"
echo "  - Includes automated testing"
echo "  - Tests CDI features (Qualifiers, Events, Interceptors)"
echo ""
echo "Option 2: Local Open Liberty + PostgreSQL"
echo "  Run: ./run-lab.sh"
echo "  - Uses local Open Liberty installation"
echo "  - PostgreSQL in Docker"
echo "  - Hot reload with liberty:dev"
echo ""
echo "Option 3: Docker + Open Liberty + PostgreSQL"
echo "  Run: ./docker-test.sh"
echo "  - Everything in Docker containers"
echo "  - Isolated environment"
echo ""
echo -e "${BLUE}Database Management:${NC}"
echo ""
echo "Connect to PostgreSQL:"
echo "  $CONTAINER_CMD exec -it banking-db psql -U bankuser -d bankdb"
echo ""
echo "View tables:"
echo "  $CONTAINER_CMD exec banking-db psql -U bankuser -d bankdb -c '\dt'"
echo ""
echo "Query clients:"
echo "  $CONTAINER_CMD exec banking-db psql -U bankuser -d bankdb -c 'SELECT * FROM clients;'"
echo ""
echo "Stop PostgreSQL:"
echo "  $COMPOSE_CMD down"
echo ""

# Made with Bob