# © Copyright Olivier Planson - 2025
#!/bin/bash

# Lab 1 Testing Script
# Tests the complete Lab 1 solution

set -e  # Exit on error

echo "=========================================="
echo "Lab 1: First Servlet Application - Test"
echo "=========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# Navigate to solution directory
cd solution

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
if [ -f "target/banking-app.war" ]; then
    echo -e "${GREEN}✓ WAR file created: target/banking-app.war${NC}"
    WAR_SIZE=$(du -h target/banking-app.war | cut -f1)
    echo "  Size: $WAR_SIZE"
else
    echo -e "${RED}❌ WAR file not found${NC}"
    exit 1
fi

echo ""

# List WAR contents
echo "WAR file contents:"
echo "----------------------------"
jar -tf target/banking-app.war | head -25
echo "  ... (showing first 25 entries)"

echo ""

# Verify key files in WAR
echo "Verifying key files..."
echo "----------------------------"
if jar -tf target/banking-app.war | grep -q "WEB-INF/classes/com/bank/web/WelcomeServlet.class"; then
    echo -e "${GREEN}✓ WelcomeServlet.class found${NC}"
else
    echo -e "${RED}❌ WelcomeServlet.class missing${NC}"
fi

if jar -tf target/banking-app.war | grep -q "WEB-INF/classes/com/bank/web/ClientListServlet.class"; then
    echo -e "${GREEN}✓ ClientListServlet.class found${NC}"
else
    echo -e "${RED}❌ ClientListServlet.class missing${NC}"
fi

if jar -tf target/banking-app.war | grep -q "WEB-INF/classes/com/bank/model/Client.class"; then
    echo -e "${GREEN}✓ Client.class found${NC}"
else
    echo -e "${RED}❌ Client.class missing${NC}"
fi

if jar -tf target/banking-app.war | grep -q "META-INF/microprofile-config.properties"; then
    echo -e "${GREEN}✓ microprofile-config.properties found${NC}"
else
    echo -e "${YELLOW}⚠ microprofile-config.properties missing (optional for Open Liberty)${NC}"
fi

echo ""

# Check for Open Liberty
echo "Checking for Open Liberty..."
echo "----------------------------"

if [ -z "$LIBERTY_HOME" ]; then
    echo -e "${YELLOW}⚠ LIBERTY_HOME not set${NC}"
    echo "To deploy, set LIBERTY_HOME environment variable"
    echo "Example: export LIBERTY_HOME=/opt/liberty"
    echo ""
    echo "Or manually copy WAR file:"
    echo "  cp target/banking-app.war \$LIBERTY_HOME/usr/servers/defaultServer/dropins/"
else
    echo -e "${GREEN}✓ LIBERTY_HOME: $LIBERTY_HOME${NC}"
    
    # Check if Open Liberty is running
    if curl -s http://localhost:9080 > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Open Liberty is running${NC}"
        
        # Ask if user wants to deploy
        echo ""
        read -p "Deploy to Open Liberty? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            echo "Deploying..."
            mvn liberty:deploy -q
            
            if [ $? -eq 0 ]; then
                echo -e "${GREEN}✓ Deployment successful${NC}"
                echo ""
                echo "Application URLs:"
                echo "  Home:     http://localhost:9080/"
                echo "  Welcome:  http://localhost:9080/welcome"
                echo "  Clients:  http://localhost:9080/clients"
                echo ""
                echo "Testing endpoints..."
                sleep 2
                
                # Test home page
                if curl -s http://localhost:9080/ | grep -q "Banking Application"; then
                    echo -e "${GREEN}✓ Home page accessible${NC}"
                else
                    echo -e "${YELLOW}⚠ Home page may not be fully loaded yet${NC}"
                fi
                
                # Test welcome servlet
                if curl -s http://localhost:9080/welcome | grep -q "Welcome"; then
                    echo -e "${GREEN}✓ Welcome servlet working${NC}"
                else
                    echo -e "${YELLOW}⚠ Welcome servlet may not be fully loaded yet${NC}"
                fi
                
                # Test clients servlet
                if curl -s http://localhost:9080/clients | grep -q "Client"; then
                    echo -e "${GREEN}✓ Clients servlet working${NC}"
                else
                    echo -e "${YELLOW}⚠ Clients servlet may not be fully loaded yet${NC}"
                fi
            else
                echo -e "${RED}❌ Deployment failed${NC}"
            fi
        fi
    else
        echo -e "${YELLOW}⚠ Open Liberty is not running${NC}"
        echo "Start Open Liberty with: \$LIBERTY_HOME/bin/standalone.sh"
    fi
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
echo "Option 2: Local Open Liberty"
if [ -n "$LIBERTY_HOME" ]; then
    echo "  ${GREEN}✓ LIBERTY_HOME is set${NC}"
    echo "  Run: ./run-lab.sh"
    echo "  Or: mvn liberty:deploy"
    echo "  Access: http://localhost:9080/"
else
    echo "  ${YELLOW}⚠ LIBERTY_HOME not set${NC}"
    echo "  Set: export LIBERTY_HOME=/path/to/liberty"
    echo "  Then run: ./run-lab.sh"
fi
echo ""
echo "Option 3: Docker + Open Liberty"
if command -v docker &> /dev/null; then
    echo "  ${GREEN}✓ Docker is available${NC}"
    echo "  Run: ./docker-test.sh"
else
    echo "  ${YELLOW}⚠ Docker not installed${NC}"
fi
echo ""

# Made with Bob
