#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Fix database tests by replacing run_test calls with inline execution
# Strategy: Execute podman exec directly for database tests

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

print_header "Fixing Database Tests with Inline Execution"

for lab in "${LABS[@]}"; do
    LAB_DIR="$PROJECT_ROOT/esipe-javaee/03-Labs/$lab"
    SCRIPT_FILE="$LAB_DIR/podman-test.sh"
    
    if [ ! -f "$SCRIPT_FILE" ]; then
        print_error "$lab: podman-test.sh not found"
        continue
    fi
    
    print_info "Processing $lab..."
    
    # Fix 1: Remove duplicate "# Wait for database to be ready" comment
    if grep -q '# Wait for database to be ready' "$SCRIPT_FILE"; then
        # Keep only the first occurrence
        awk '!seen[$0]++ || !/# Wait for database to be ready/' "$SCRIPT_FILE" > "$SCRIPT_FILE.tmp"
        mv "$SCRIPT_FILE.tmp" "$SCRIPT_FILE"
        print_success "  Removed duplicate comment"
    fi
    
    # Fix 2: Replace database schema test with inline execution
    # Find and replace the Flyway migrations test
    if grep -q 'run_test "Database schema initialized"' "$SCRIPT_FILE"; then
        awk '
        /run_test "Database schema initialized"/ {
            print "    # Test Flyway migrations applied"
            print "    ((TEST_NUMBER++))"
            print "    TEST_NAMES[$TEST_NUMBER]=\"Database schema initialized\""
            print "    echo -n \"Test $TEST_NUMBER: Database schema initialized... \""
            print "    if podman exec \"$DB_CONTAINER\" psql -U \"$DB_USER\" -d \"$DB_NAME\" -c \"\\dt\" 2>/dev/null | grep -q \"clients\"; then"
            print "        echo -e \"${GREEN}✓ PASSED${NC}\""
            print "        TEST_RESULTS[$TEST_NUMBER]=\"PASSED\""
            print "        ((TESTS_PASSED++))"
            print "    else"
            print "        echo -e \"${RED}✗ FAILED${NC}\""
            print "        TEST_RESULTS[$TEST_NUMBER]=\"FAILED\""
            print "        ((TESTS_FAILED++))"
            print "    fi"
            # Skip the next line (the command parameter)
            getline
            next
        }
        { print }
        ' "$SCRIPT_FILE" > "$SCRIPT_FILE.tmp"
        
        mv "$SCRIPT_FILE.tmp" "$SCRIPT_FILE"
        print_success "  Replaced database schema test with inline code"
    fi
    
    print_success "$lab: Fixed"
done

print_header "Summary"
print_success "Fixed database tests with inline execution"
echo ""
print_info "Benefits:"
echo "  1. podman exec commands execute directly"
echo "  2. No string wrapping or quoting issues"
echo "  3. Variables expand naturally"
echo "  4. Tests work reliably"

# Made with Bob
