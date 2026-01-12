#!/bin/bash
# © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
# Lab 02B Testing Script
# Tests the Lab 02B solution

set -e

echo "=========================================="
echo "Lab 02B: JSF Client Management Test"
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
if [ -f "target/lab02b-jsf.war" ]; then
    echo -e "${GREEN}✓ WAR file created: target/lab02b-jsf.war${NC}"
    WAR_SIZE=$(du -h target/lab02b-jsf.war | cut -f1)
    echo "  Size: $WAR_SIZE"
else
    echo -e "${RED}❌ WAR file not found${NC}"
    exit 1
fi

echo ""

# List WAR contents
echo "WAR file contents (first 30 entries):"
echo "----------------------------"
jar -tf target/lab02b-jsf.war | head -30
echo "  ..."

echo ""

# Verify key files in WAR
echo "Verifying key files..."
echo "----------------------------"

# Java classes
if jar -tf target/lab02b-jsf.war | grep -q "WEB-INF/classes/com/bank/web/ClientBean.class"; then
    echo -e "${GREEN}✓ ClientBean.class found${NC}"
else
    echo -e "${RED}❌ ClientBean.class missing${NC}"
fi

if jar -tf target/lab02b-jsf.war | grep -q "WEB-INF/classes/com/bank/service/ClientService.class"; then
    echo -e "${GREEN}✓ ClientService.class found${NC}"
else
    echo -e "${RED}❌ ClientService.class missing${NC}"
fi

if jar -tf target/lab02b-jsf.war | grep -q "WEB-INF/classes/com/bank/model/Client.class"; then
    echo -e "${GREEN}✓ Client.class found${NC}"
else
    echo -e "${RED}❌ Client.class missing${NC}"
fi

if jar -tf target/lab02b-jsf.war | grep -q "WEB-INF/classes/com/bank/model/Address.class"; then
    echo -e "${GREEN}✓ Address.class found${NC}"
else
    echo -e "${YELLOW}⚠ Address.class missing${NC}"
fi

if jar -tf target/lab02b-jsf.war | grep -q "WEB-INF/classes/com/bank/validator/EmailValidator.class"; then
    echo -e "${GREEN}✓ EmailValidator.class found${NC}"
else
    echo -e "${YELLOW}⚠ EmailValidator.class missing${NC}"
fi

# JSF pages
if jar -tf target/lab02b-jsf.war | grep -q "WEB-INF/views/client-list.xhtml"; then
    echo -e "${GREEN}✓ client-list.xhtml found${NC}"
else
    echo -e "${RED}❌ client-list.xhtml missing${NC}"
fi

if jar -tf target/lab02b-jsf.war | grep -q "WEB-INF/views/client-form.xhtml"; then
    echo -e "${GREEN}✓ client-form.xhtml found${NC}"
else
    echo -e "${RED}❌ client-form.xhtml missing${NC}"
fi

if jar -tf target/lab02b-jsf.war | grep -q "WEB-INF/views/client-details.xhtml"; then
    echo -e "${GREEN}✓ client-details.xhtml found${NC}"
else
    echo -e "${RED}❌ client-details.xhtml missing${NC}"
fi

if jar -tf target/lab02b-jsf.war | grep -q "WEB-INF/templates/main.xhtml"; then
    echo -e "${GREEN}✓ main.xhtml template found${NC}"
else
    echo -e "${RED}❌ main.xhtml template missing${NC}"
fi

if jar -tf target/lab02b-jsf.war | grep -q "index.xhtml"; then
    echo -e "${GREEN}✓ index.xhtml found${NC}"
else
    echo -e "${RED}❌ index.xhtml missing${NC}"
fi

# Composite component
if jar -tf target/lab02b-jsf.war | grep -q "resources/components/addressInput.xhtml"; then
    echo -e "${GREEN}✓ addressInput.xhtml composite component found${NC}"
else
    echo -e "${YELLOW}⚠ addressInput.xhtml composite component missing${NC}"
fi

# Configuration files
if jar -tf target/lab02b-jsf.war | grep -q "WEB-INF/web.xml"; then
    echo -e "${GREEN}✓ web.xml found${NC}"
else
    echo -e "${RED}❌ web.xml missing${NC}"
fi

if jar -tf target/lab02b-jsf.war | grep -q "WEB-INF/faces-config.xml"; then
    echo -e "${GREEN}✓ faces-config.xml found${NC}"
else
    echo -e "${YELLOW}⚠ faces-config.xml missing (optional)${NC}"
fi

if jar -tf target/lab02b-jsf.war | grep -q "WEB-INF/beans.xml"; then
    echo -e "${GREEN}✓ beans.xml found${NC}"
else
    echo -e "${YELLOW}⚠ beans.xml missing (CDI may not work)${NC}"
fi

# Resources
if jar -tf target/lab02b-jsf.war | grep -q "css/style.css"; then
    echo -e "${GREEN}✓ style.css found${NC}"
else
    echo -e "${YELLOW}⚠ style.css missing${NC}"
fi

if jar -tf target/lab02b-jsf.war | grep -q "META-INF/microprofile-config.properties"; then
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
    echo "  Access: http://localhost:9080/lab02b-jsf/"
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
    echo "  Run: mvn liberty:dev"
else
    echo "  ${YELLOW}⚠ LIBERTY_HOME not set${NC}"
    echo "  Set: export LIBERTY_HOME=/path/to/liberty"
    echo "  Then run: mvn liberty:dev"
fi
echo ""
echo -e "${BLUE}Recommended:${NC} Use Podman for easiest setup!"
echo "  ./podman-test.sh"
echo ""

# Made with Bob