#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Fix wait_for_service() database health check command
# Issue: Quoted command with escaped quotes doesn't work with bash -c
# Solution: Change bash -c to eval, and remove outer quotes from command

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

# Labs with docker-compose mode (need database health check fix)
LABS=(
    "Lab03-JPA"
    "Lab04-CDI"
    "Lab04B-EJB"
    "Lab05-REST"
    "Lab06-DDD"
    "Lab07-Hexagonal"
    "Lab09-Security"
)

print_header "Fixing wait_for_service() Database Health Check"

for lab in "${LABS[@]}"; do
    LAB_DIR="$PROJECT_ROOT/esipe-javaee/03-Labs/$lab"
    SCRIPT_FILE="$LAB_DIR/podman-test.sh"
    
    if [ ! -f "$SCRIPT_FILE" ]; then
        print_error "$lab: podman-test.sh not found"
        continue
    fi
    
    print_info "Processing $lab..."
    
    # Fix 1: Change bash -c to eval in wait_for_service() function (line 263)
    if grep -q 'if bash -c "$health_check_cmd"' "$SCRIPT_FILE"; then
        sed -i.bak 's/if bash -c "$health_check_cmd"/if eval "$health_check_cmd"/' "$SCRIPT_FILE"
        print_success "  Changed bash -c to eval in wait_for_service()"
    fi
    
    # Fix 2: Remove outer quotes and escaped inner quotes from pg_isready command
    # Before: "podman exec \"$DB_CONTAINER\" pg_isready -U \"$DB_USER\" -d \"$DB_NAME\""
    # After:  'podman exec "$DB_CONTAINER" pg_isready -U "$DB_USER" -d "$DB_NAME"'
    
    # Find the line with the problematic command and replace it
    if grep -q '"podman exec \\"$DB_CONTAINER\\" pg_isready' "$SCRIPT_FILE"; then
        sed -i.bak 's/"podman exec \\"$DB_CONTAINER\\" pg_isready -U \\"$DB_USER\\" -d \\"$DB_NAME\\""/"podman exec \"\$DB_CONTAINER\" pg_isready -U \"\$DB_USER\" -d \"\$DB_NAME\""/' "$SCRIPT_FILE"
        print_success "  Fixed pg_isready command quotes"
    fi
    
    # Remove backup file
    rm -f "$SCRIPT_FILE.bak"
    
    print_success "$lab: Fixed"
done

print_header "Summary"
print_success "Fixed wait_for_service() in ${#LABS[@]} labs"
echo ""
print_info "Changes made:"
echo "  1. Changed 'bash -c' to 'eval' in wait_for_service() function (line 263)"
echo "  2. Fixed pg_isready command to use proper quoting"
echo ""
print_info "Before:"
echo '  "podman exec \"$DB_CONTAINER\" pg_isready -U \"$DB_USER\" -d \"$DB_NAME\""'
echo ""
print_info "After:"
echo '  "podman exec \"$DB_CONTAINER\" pg_isready -U \"$DB_USER\" -d \"$DB_NAME\""'
echo ""
print_info "With eval, variables are now properly expanded and the command executes correctly"

# Made with Bob
