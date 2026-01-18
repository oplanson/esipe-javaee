#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Fix docker-compose to start only postgres service, not all services
# This prevents conflicts when podman-test.sh creates its own application container

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

# Labs with docker-compose mode
LABS=(
    "Lab03-JPA"
    "Lab04-CDI"
    "Lab04B-EJB"
    "Lab05-REST"
    "Lab06-DDD"
    "Lab07-Hexagonal"
    "Lab09-Security"
)

print_header "Fixing docker-compose to Start Only PostgreSQL"

for lab in "${LABS[@]}"; do
    LAB_DIR="$PROJECT_ROOT/esipe-javaee/03-Labs/$lab"
    SCRIPT_FILE="$LAB_DIR/podman-test.sh"
    
    if [ ! -f "$SCRIPT_FILE" ]; then
        print_error "$lab: podman-test.sh not found"
        continue
    fi
    
    print_info "Processing $lab..."
    
    # Check if already fixed
    if grep -q "docker-compose up -d postgres" "$SCRIPT_FILE"; then
        print_info "  Already fixed (uses 'docker-compose up -d postgres')"
        continue
    fi
    
    # Fix: Change "docker-compose up -d" to "docker-compose up -d postgres"
    sed -i '' 's/if docker-compose up -d; then/if docker-compose up -d postgres; then/' "$SCRIPT_FILE"
    
    print_success "  Fixed docker-compose command"
    print_success "$lab: Complete"
done

print_header "Summary"
print_success "Fixed docker-compose commands in all labs"
echo ""
print_info "Now docker-compose will start ONLY the postgres service,"
print_info "allowing podman-test.sh to create its own application container."

# Made with Bob
