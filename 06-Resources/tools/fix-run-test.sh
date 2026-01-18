#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Fix run_test() function to use eval instead of bash -c
# Issue: Same as wait_for_service() - bash -c doesn't properly expand variables in quoted strings

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

# All labs (run_test function is in all labs)
LABS=(
    "Lab01-FirstServlet"
    "Lab02-ServletsJSP"
    "Lab02B-JSF"
    "Lab03-JPA"
    "Lab04-CDI"
    "Lab04B-EJB"
    "Lab05-REST"
    "Lab06-DDD"
    "Lab07-Hexagonal"
    "Lab08-Microservices"
    "Lab09-Security"
)

print_header "Fixing run_test() Function"

for lab in "${LABS[@]}"; do
    LAB_DIR="$PROJECT_ROOT/esipe-javaee/03-Labs/$lab"
    SCRIPT_FILE="$LAB_DIR/podman-test.sh"
    
    if [ ! -f "$SCRIPT_FILE" ]; then
        print_error "$lab: podman-test.sh not found"
        continue
    fi
    
    print_info "Processing $lab..."
    
    # Fix: Change bash -c to eval in run_test() function
    if grep -q 'if bash -c "$test_command"' "$SCRIPT_FILE"; then
        sed -i.bak 's/if bash -c "$test_command"/if eval "$test_command"/' "$SCRIPT_FILE"
        print_success "  Changed bash -c to eval in run_test()"
        
        # Remove backup file
        rm -f "$SCRIPT_FILE.bak"
        print_success "$lab: Fixed"
    else
        print_info "  Already using eval or different pattern"
    fi
done

print_header "Summary"
print_success "Fixed run_test() in all labs"
echo ""
print_info "Change made:"
echo "  Line ~291: if bash -c \"\$test_command\" → if eval \"\$test_command\""
echo ""
print_info "This allows proper variable expansion in test commands like:"
echo "  podman exec \"\$DB_CONTAINER\" psql -U \"\$DB_USER\" -d \"\$DB_NAME\" -c '\\dt'"

# Made with Bob
