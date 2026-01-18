#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Apply template v2.1 to Lab05B-JMS with correct configuration

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"
TEMPLATE_FILE="$SCRIPT_DIR/podman-test-template.sh"
LAB_DIR="$PROJECT_ROOT/esipe-javaee/03-Labs/Lab05B-JMS"

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_header() { echo -e "\n${BLUE}=== $1 ===${NC}\n"; }
print_success() { echo -e "${GREEN}✓${NC} $1"; }
print_info() { echo -e "${YELLOW}ℹ${NC} $1"; }

print_header "Applying Template v2.1 to Lab05B-JMS"

# Backup existing script
if [ -f "$LAB_DIR/podman-test.sh" ]; then
    cp "$LAB_DIR/podman-test.sh" "$LAB_DIR/podman-test.sh.backup"
    print_success "Backed up existing script"
fi

# Copy template
cp "$TEMPLATE_FILE" "$LAB_DIR/podman-test.sh"
print_success "Copied template"

# Apply Lab05B-JMS specific configuration
sed -i '' \
    -e 's/LAB_NAME=".*"/LAB_NAME="Lab 05B - JMS Asynchronous Transaction Processing"/' \
    -e 's/LAB_NUMBER=".*"/LAB_NUMBER="05B"/' \
    -e 's/IMAGE_NAME=".*"/IMAGE_NAME="banking-jms-lab05b"/' \
    -e 's/CONTAINER_NAME=".*"/CONTAINER_NAME="banking-jms-lab05b"/' \
    -e 's/APP_PORT=.*/APP_PORT=9080/' \
    -e 's/DB_MODE=".*"/DB_MODE="docker-compose"/' \
    -e 's/DB_CONTAINER=".*"/DB_CONTAINER="banking-jms-db"/' \
    -e 's/DB_PORT=.*/DB_PORT=5432/' \
    -e 's/DB_USER=".*"/DB_USER="bankuser"/' \
    -e 's/DB_PASSWORD=".*"/DB_PASSWORD="bankpass"/' \
    -e 's/DB_NAME=".*"/DB_NAME="bankingdb"/' \
    -e 's/BUILD_DIR=".*"/BUILD_DIR="solution"/' \
    -e 's/WAR_NAME=".*"/WAR_NAME="banking-jms-app.war"/' \
    "$LAB_DIR/podman-test.sh"

print_success "Applied Lab05B-JMS configuration"

# Fix docker-compose command to start only postgres
sed -i '' 's/if docker-compose up -d; then/if docker-compose up -d postgres; then/' "$LAB_DIR/podman-test.sh"
print_success "Fixed docker-compose command (postgres only)"

# Make executable
chmod +x "$LAB_DIR/podman-test.sh"
print_success "Made script executable"

print_header "Summary"
print_success "Template v2.1 applied to Lab05B-JMS"
echo ""
print_info "Configuration:"
echo "  LAB_NAME: Lab 05B - JMS Asynchronous Transaction Processing"
echo "  IMAGE_NAME: banking-jms-lab05b"
echo "  CONTAINER_NAME: banking-jms-lab05b"
echo "  APP_PORT: 9080"
echo "  DB_MODE: docker-compose"
echo "  DB_CONTAINER: banking-jms-db"
echo "  WAR_NAME: banking-jms-app.war"
echo ""
print_info "Next steps:"
echo "  1. Add Lab05B-JMS specific tests to Phase 4"
echo "  2. Test the script: cd $LAB_DIR && ./podman-test.sh"

# Made with Bob
