# © Copyright Olivier Planson - 2025
#!/bin/bash

# Lab 1 Quick Start Script with Open Liberty
# Builds, deploys, and opens the application

set -e

echo "=========================================="
echo "Lab 1: Quick Start with Open Liberty"
echo "=========================================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Check if Podman is available (suggest alternative)
if command -v podman &> /dev/null; then
    echo -e "${BLUE}ℹ️  Podman detected!${NC}"
    echo "For easier setup without Open Liberty installation, consider using:"
    echo "  ./podman-test.sh"
    echo ""
    read -p "Continue with Open Liberty deployment? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Exiting. Run ./podman-test.sh for Podman deployment."
        exit 0
    fi
    echo ""
fi

# Check if Open Liberty is set
if [ -z "$LIBERTY_HOME" ]; then
    echo -e "${RED}❌ LIBERTY_HOME not set${NC}"
    echo ""
    echo "Please set LIBERTY_HOME environment variable:"
    echo "  export LIBERTY_HOME=/path/to/liberty"
    echo ""
    echo "Or download Open Liberty from: https://www.liberty.org/downloads/"
    echo ""
    echo -e "${BLUE}Alternative: Use Podman (no Open Liberty needed)${NC}"
    echo "  ./podman-test.sh"
    exit 1
fi

echo -e "${GREEN}✓ LIBERTY_HOME: $LIBERTY_HOME${NC}"
echo ""

# Check if Open Liberty is running
if ! curl -s http://localhost:9080 > /dev/null 2>&1; then
    echo -e "${YELLOW}⚠ Open Liberty is not running${NC}"
    echo ""
    echo "Starting Open Liberty..."
    
    # Start Open Liberty in background
    $LIBERTY_HOME/bin/standalone.sh > /dev/null 2>&1 &
    LIBERTY_PID=$!
    
    echo "Waiting for Open Liberty to start..."
    for i in {1..30}; do
        if curl -s http://localhost:9080 > /dev/null 2>&1; then
            echo -e "${GREEN}✓ Open Liberty started (PID: $LIBERTY_PID)${NC}"
            break
        fi
        sleep 2
        echo -n "."
    done
    echo ""
else
    echo -e "${GREEN}✓ Open Liberty is already running${NC}"
fi

echo ""

# Navigate to solution
cd solution

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

echo "Deploying to Open Liberty..."
echo "----------------------------"
mvn liberty:deploy -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Deployment successful${NC}"
else
    echo -e "${RED}❌ Deployment failed${NC}"
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

# Wait a moment for deployment to complete
sleep 3

echo "Testing endpoints..."
echo "----------------------------"

# Test home page
if curl -s http://localhost:9080/ | grep -q "Banking Application"; then
    echo -e "${GREEN}✓ Home page working${NC}"
else
    echo -e "${YELLOW}⚠ Home page not responding${NC}"
fi

# Test welcome servlet
if curl -s http://localhost:9080/welcome | grep -q "Welcome"; then
    echo -e "${GREEN}✓ Welcome servlet working${NC}"
else
    echo -e "${YELLOW}⚠ Welcome servlet not responding${NC}"
fi

# Test clients servlet
if curl -s http://localhost:9080/clients | grep -q "Client"; then
    echo -e "${GREEN}✓ Clients servlet working${NC}"
else
    echo -e "${YELLOW}⚠ Clients servlet not responding${NC}"
fi

echo ""
echo "=========================================="
echo "Next Steps"
echo "=========================================="
echo ""
echo "1. Open your browser to:"
echo "   http://localhost:9080/"
echo ""
echo "2. Try the features:"
echo "   - View client list"
echo "   - Add new clients"
echo "   - Navigate between pages"
echo ""
echo "3. To stop Open Liberty:"
echo "   \$LIBERTY_HOME/bin/jboss-cli.sh --connect command=:shutdown"
echo ""
echo "4. To redeploy after changes:"
echo "   mvn clean package liberty:redeploy"
echo ""
echo -e "${BLUE}Alternative Deployment Methods:${NC}"
echo "  • Podman + Open Liberty: ./podman-test.sh"
echo "  • Docker + Open Liberty: ./docker-test.sh"
echo ""

# Try to open browser (works on macOS and some Linux)
if command -v open &> /dev/null; then
    echo "Opening browser..."
    open http://localhost:9080/
elif command -v xdg-open &> /dev/null; then
    echo "Opening browser..."
    xdg-open http://localhost:9080/
fi

echo ""
echo -e "${GREEN}✓ Lab 1 is ready to use with Open Liberty!${NC}"
echo ""

# Made with Bob
