#!/bin/bash
# © Copyright Olivier Planson - 2025
# Lab 02 Quick Start Script with Open Liberty
# Builds, deploys, and opens the application

set -e

echo "=========================================="
echo "Lab 02: Quick Start with Open Liberty"
echo "Servlets, JSP & MicroProfile"
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

# Navigate to starter directory
cd starter

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

echo "Starting Liberty in dev mode..."
echo "----------------------------"
echo ""
echo -e "${BLUE}Liberty dev mode features:${NC}"
echo "  • Automatic reload on code changes"
echo "  • Hot deployment"
echo "  • Integrated testing"
echo "  • Press Ctrl+C to stop"
echo ""
echo "Starting..."
echo ""

# Run Liberty in dev mode
mvn liberty:dev

# Made with Bob