# © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
#!/bin/bash

# Docker-based testing for Lab 1
# No local WildFly installation required!

set -e

echo "=========================================="
echo "Lab 1: Docker-based Testing"
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
echo ""

# Navigate to solution
cd solution

echo "Building application..."
echo "----------------------------"
mvn clean package -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
    echo "  WAR file: target/banking-app.war"
else
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

echo ""

# Create Dockerfile if it doesn't exist
if [ ! -f "Dockerfile" ]; then
    echo "Creating Dockerfile..."
    cat > Dockerfile << 'EOF'
FROM quay.io/wildfly/wildfly:27.0.1.Final-jdk17

# Copy WAR file to deployments
COPY target/banking-app.war /opt/jboss/wildfly/standalone/deployments/

# Expose ports
EXPOSE 8080 9990

# Start WildFly
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
EOF
    echo -e "${GREEN}✓ Dockerfile created${NC}"
fi

echo ""

echo "Building Docker image..."
echo "----------------------------"
docker build -t banking-app:lab01 . -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Docker image built${NC}"
else
    echo -e "${RED}❌ Docker build failed${NC}"
    exit 1
fi

echo ""

# Stop existing container if running
if docker ps -a | grep -q banking-app-lab01; then
    echo "Stopping existing container..."
    docker stop banking-app-lab01 > /dev/null 2>&1 || true
    docker rm banking-app-lab01 > /dev/null 2>&1 || true
fi

echo "Starting container..."
echo "----------------------------"
docker run -d \
    --name banking-app-lab01 \
    -p 8080:8080 \
    -p 9990:9990 \
    banking-app:lab01

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Container started${NC}"
else
    echo -e "${RED}❌ Container failed to start${NC}"
    exit 1
fi

echo ""
echo "Waiting for application to deploy..."
for i in {1..30}; do
    if curl -s http://localhost:8080/banking-app/ > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Application deployed${NC}"
        break
    fi
    sleep 2
    echo -n "."
done
echo ""

echo ""
echo "=========================================="
echo "Application Ready!"
echo "=========================================="
echo ""
echo -e "${BLUE}Application URLs:${NC}"
echo "  🏠 Home:     http://localhost:8080/banking-app/"
echo "  👋 Welcome:  http://localhost:8080/banking-app/welcome"
echo "  👥 Clients:  http://localhost:8080/banking-app/clients"
echo "  ➕ Add:      http://localhost:8080/banking-app/add-client.html"
echo ""

# Wait a moment
sleep 2

echo "Testing endpoints..."
echo "----------------------------"

# Test home page
if curl -s http://localhost:8080/banking-app/ | grep -q "Banking Application"; then
    echo -e "${GREEN}✓ Home page working${NC}"
else
    echo -e "${YELLOW}⚠ Home page not responding${NC}"
fi

# Test welcome servlet
if curl -s http://localhost:8080/banking-app/welcome | grep -q "Welcome"; then
    echo -e "${GREEN}✓ Welcome servlet working${NC}"
else
    echo -e "${YELLOW}⚠ Welcome servlet not responding${NC}"
fi

# Test clients servlet
if curl -s http://localhost:8080/banking-app/clients | grep -q "Client"; then
    echo -e "${GREEN}✓ Clients servlet working${NC}"
else
    echo -e "${YELLOW}⚠ Clients servlet not responding${NC}"
fi

echo ""
echo "=========================================="
echo "Docker Commands"
echo "=========================================="
echo ""
echo "View logs:"
echo "  docker logs -f banking-app-lab01"
echo ""
echo "Stop container:"
echo "  docker stop banking-app-lab01"
echo ""
echo "Remove container:"
echo "  docker rm banking-app-lab01"
echo ""
echo "Remove image:"
echo "  docker rmi banking-app:lab01"
echo ""

# Try to open browser
if command -v open &> /dev/null; then
    echo "Opening browser..."
    open http://localhost:8080/banking-app/
elif command -v xdg-open &> /dev/null; then
    echo "Opening browser..."
    xdg-open http://localhost:8080/banking-app/
fi

echo ""
echo -e "${GREEN}✓ Lab 1 is running in Docker!${NC}"
echo ""

# Made with Bob
