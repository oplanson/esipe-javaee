#!/bin/bash
# © Copyright Olivier Planson - 2025
# Lab 02 Testing Script
# Tests the Lab 02 solution

set -e

echo "=========================================="
echo "Lab 02: Servlets, JSP & MicroProfile Test"
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

# Check for Podman (optional but recommended)
if command -v podman &> /dev/null; then
    echo -e "${GREEN}✓ Podman found${NC}"
    PODMAN_AVAILABLE=true
else
    echo -e "${YELLOW}⚠ Podman not found (optional)${NC}"
    PODMAN_AVAILABLE=false
fi

echo ""

# Navigate to starter directory
cd starter

echo "Building application..."
echo "----------------------------"

# Clean and build
mvn clean package -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
else
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

echo ""

# Check if WAR file was created
if [ -f "target/banking-web-app.war" ]; then
    echo -e "${GREEN}✓ WAR file created: target/banking-web-app.war${NC}"
    WAR_SIZE=$(du -h target/banking-web-app.war | cut -f1)
    echo "  Size: $WAR_SIZE"
else
    echo -e "${RED}❌ WAR file not found${NC}"
    exit 1
fi

echo ""

# List WAR contents
echo "WAR file contents (first 30 entries):"
echo "----------------------------"
jar -tf target/banking-web-app.war | head -30
echo "  ..."

echo ""

# Verify key files in WAR
echo "Verifying key files..."
echo "----------------------------"

if jar -tf target/banking-web-app.war | grep -q "WEB-INF/classes/com/bank/web/ClientController.class"; then
    echo -e "${GREEN}✓ ClientController.class found${NC}"
else
    echo -e "${RED}❌ ClientController.class missing${NC}"
fi

if jar -tf target/banking-web-app.war | grep -q "WEB-INF/classes/com/bank/service/ClientService.class"; then
    echo -e "${GREEN}✓ ClientService.class found${NC}"
else
    echo -e "${RED}❌ ClientService.class missing${NC}"
fi

if jar -tf target/banking-web-app.war | grep -q "WEB-INF/classes/com/bank/model/Client.class"; then
    echo -e "${GREEN}✓ Client.class found${NC}"
else
    echo -e "${RED}❌ Client.class missing${NC}"
fi

if jar -tf target/banking-web-app.war | grep -q "WEB-INF/classes/com/bank/model/Account.class"; then
    echo -e "${GREEN}✓ Account.class found${NC}"
else
    echo -e "${RED}❌ Account.class missing${NC}"
fi

if jar -tf target/banking-web-app.war | grep -q "WEB-INF/classes/com/bank/health/DatabaseHealthCheck.class"; then
    echo -e "${GREEN}✓ DatabaseHealthCheck.class found${NC}"
else
    echo -e "${YELLOW}⚠ DatabaseHealthCheck.class missing${NC}"
fi

if jar -tf target/banking-web-app.war | grep -q "WEB-INF/classes/com/bank/health/WebAppReadinessCheck.class"; then
    echo -e "${GREEN}✓ WebAppReadinessCheck.class found${NC}"
else
    echo -e "${YELLOW}⚠ WebAppReadinessCheck.class missing${NC}"
fi

if jar -tf target/banking-web-app.war | grep -q "WEB-INF/views/client-list.jsp"; then
    echo -e "${GREEN}✓ client-list.jsp found${NC}"
else
    echo -e "${RED}❌ client-list.jsp missing${NC}"
fi

if jar -tf target/banking-web-app.war | grep -q "WEB-INF/views/client-form.jsp"; then
    echo -e "${GREEN}✓ client-form.jsp found${NC}"
else
    echo -e "${RED}❌ client-form.jsp missing${NC}"
fi

if jar -tf target/banking-web-app.war | grep -q "WEB-INF/views/client-details.jsp"; then
    echo -e "${GREEN}✓ client-details.jsp found${NC}"
else
    echo -e "${RED}❌ client-details.jsp missing${NC}"
fi

if jar -tf target/banking-web-app.war | grep -q "css/style.css"; then
    echo -e "${GREEN}✓ style.css found${NC}"
else
    echo -e "${YELLOW}⚠ style.css missing${NC}"
fi

if jar -tf target/banking-web-app.war | grep -q "META-INF/microprofile-config.properties"; then
    echo -e "${GREEN}✓ microprofile-config.properties found${NC}"
else
    echo -e "${YELLOW}⚠ microprofile-config.properties missing${NC}"
fi

echo ""
echo "=========================================="
echo "Test Summary"
echo "=========================================="
echo ""
echo "Build:        ${GREEN}✓ Success${NC}"
echo "WAR created:  ${GREEN}✓ Success${NC}"
echo ""
echo -e "${BLUE}Deployment Options:${NC}"
echo ""
echo "Option 1: Podman + Open Liberty (Recommended - No installation needed)"
if [ "$PODMAN_AVAILABLE" = true ]; then
    echo "  ${GREEN}✓ Podman is available${NC}"
    echo "  Run: ./podman-test.sh"
    echo "  Access: http://localhost:9080/"
else
    echo "  ${YELLOW}⚠ Podman not installed${NC}"
    echo "  Install: brew install podman (macOS)"
    echo "  Then run: ./podman-test.sh"
fi
echo ""
echo "Option 2: Docker + Open Liberty"
if command -v docker &> /dev/null; then
    echo "  ${GREEN}✓ Docker is available${NC}"
    echo "  Run: ./docker-test.sh"
else
    echo "  ${YELLOW}⚠ Docker not installed${NC}"
fi
echo ""
echo "Option 3: Local Open Liberty"
if [ -n "$LIBERTY_HOME" ]; then
    echo "  ${GREEN}✓ LIBERTY_HOME is set${NC}"
    echo "  Run: ./run-lab.sh"
    echo "  Or: mvn liberty:dev"
else
    echo "  ${YELLOW}⚠ LIBERTY_HOME not set${NC}"
    echo "  Set: export LIBERTY_HOME=/path/to/liberty"
    echo "  Then run: ./run-lab.sh"
fi
echo ""
echo -e "${BLUE}Recommended:${NC} Use Podman for easiest setup!"
echo "  ./podman-test.sh"
echo ""

# Made with Bob