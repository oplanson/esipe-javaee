#!/bin/bash
# © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Lab 6 Quick Start Script with Open Liberty
# Builds, deploys, and opens the DDD application

set -e

echo "=========================================="
echo "Lab 6: Domain-Driven Design"
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
    echo "For easier setup with PostgreSQL + Open Liberty, consider using:"
    echo "  ./podman-test.sh"
    echo ""
    read -p "Continue with local Open Liberty deployment? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Exiting. Run ./podman-test.sh for Podman deployment."
        exit 0
    fi
    echo ""
fi

# Check PostgreSQL
echo "Checking PostgreSQL..."
echo "----------------------------"
if ! docker-compose ps | grep -q "lab06-postgres.*Up"; then
    echo -e "${YELLOW}⚠ PostgreSQL not running${NC}"
    echo "Starting PostgreSQL with docker-compose..."
    cd solution
    docker-compose up -d
    cd ..
    
    echo "Waiting for PostgreSQL to be ready..."
    for i in {1..30}; do
        if docker exec lab06-postgres pg_isready -U bankuser -d bankdb > /dev/null 2>&1; then
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

echo "Running with Liberty dev mode..."
echo "----------------------------"
echo "This will:"
echo "  1. Start Open Liberty"
echo "  2. Run Flyway migrations"
echo "  3. Deploy the application"
echo "  4. Enable hot reload"
echo ""
echo "Press Ctrl+C to stop"
echo ""

mvn liberty:dev

# Made with Bob