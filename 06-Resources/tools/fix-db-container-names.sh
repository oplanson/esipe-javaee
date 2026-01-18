#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Fix DB_CONTAINER names to match docker-compose.yml
# Issue: Script uses different container names than docker-compose

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_header() { echo -e "\n${BLUE}=== $1 ===${NC}\n"; }
print_success() { echo -e "${GREEN}✓${NC} $1"; }
print_error() { echo -e "${RED}✗${NC} $1"; }
print_info() { echo -e "${YELLOW}ℹ${NC} $1"; }

print_header "Fixing DB_CONTAINER Names"

# Lab03-JPA
LAB="Lab03-JPA"
SCRIPT_FILE="$PROJECT_ROOT/esipe-javaee/03-Labs/$LAB/podman-test.sh"
print_info "Processing $LAB..."
sed -i.bak 's/^DB_CONTAINER="lab03-postgres"$/DB_CONTAINER="banking-db"/' "$SCRIPT_FILE"
rm -f "$SCRIPT_FILE.bak"
print_success "  Changed lab03-postgres → banking-db"

# Lab04-CDI
LAB="Lab04-CDI"
SCRIPT_FILE="$PROJECT_ROOT/esipe-javaee/03-Labs/$LAB/podman-test.sh"
print_info "Processing $LAB..."
sed -i.bak 's/^DB_CONTAINER="lab04-postgres"$/DB_CONTAINER="banking-db"/' "$SCRIPT_FILE"
rm -f "$SCRIPT_FILE.bak"
print_success "  Changed lab04-postgres → banking-db"

# Lab05-REST
LAB="Lab05-REST"
SCRIPT_FILE="$PROJECT_ROOT/esipe-javaee/03-Labs/$LAB/podman-test.sh"
print_info "Processing $LAB..."
sed -i.bak 's/^DB_CONTAINER="lab05-postgres"$/DB_CONTAINER="banking-db"/' "$SCRIPT_FILE"
rm -f "$SCRIPT_FILE.bak"
print_success "  Changed lab05-postgres → banking-db"

print_header "Summary"
print_success "Fixed DB_CONTAINER names in 3 labs"
echo ""
print_info "All labs now match their docker-compose.yml container names:"
echo "  Lab03-JPA: banking-db"
echo "  Lab04-CDI: banking-db"
echo "  Lab04B-EJB: lab04b-postgres (already correct)"
echo "  Lab05-REST: banking-db"
echo "  Lab06-DDD: lab06-postgres (already correct)"
echo "  Lab07-Hexagonal: lab07-postgres (already correct)"
echo "  Lab09-Security: bank-security-db (already correct)"

# Made with Bob
