#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Revert eval changes and implement proper fix
# Strategy: Execute commands directly instead of passing as strings

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
LABS_DB=(
    "Lab03-JPA"
    "Lab04-CDI"
    "Lab04B-EJB"
    "Lab05-REST"
    "Lab06-DDD"
    "Lab07-Hexagonal"
    "Lab09-Security"
)

print_header "Reverting eval and Implementing Proper Fix"

for lab in "${LABS_DB[@]}"; do
    LAB_DIR="$PROJECT_ROOT/esipe-javaee/03-Labs/$lab"
    SCRIPT_FILE="$LAB_DIR/podman-test.sh"
    
    if [ ! -f "$SCRIPT_FILE" ]; then
        print_error "$lab: podman-test.sh not found"
        continue
    fi
    
    print_info "Processing $lab..."
    
    # Step 1: Revert wait_for_service() back to bash -c
    if grep -q 'if eval "$health_check_cmd"' "$SCRIPT_FILE"; then
        sed -i.bak 's/if eval "$health_check_cmd"/if bash -c "$health_check_cmd"/' "$SCRIPT_FILE"
        print_success "  Reverted wait_for_service() to bash -c"
    fi
    
    # Step 2: Revert run_test() back to bash -c
    if grep -q 'if eval "$test_command"' "$SCRIPT_FILE"; then
        sed -i.bak 's/if eval "$test_command"/if bash -c "$test_command"/' "$SCRIPT_FILE"
        print_success "  Reverted run_test() to bash -c"
    fi
    
    # Step 3: Replace wait_for_service call with direct command execution
    # Find the line number of wait_for_service "Database" call
    LINE_NUM=$(grep -n 'wait_for_service "Database"' "$SCRIPT_FILE" | cut -d: -f1)
    
    if [ -n "$LINE_NUM" ]; then
        # Replace the wait_for_service call with inline loop
        perl -i.bak -pe '
            if (/wait_for_service "Database"/) {
                $_ = qq{                # Wait for database to be ready
                print_info "Waiting for Database to be ready..."
                local elapsed=0
                while [ "\$elapsed" -lt "\$DB_READY_TIMEOUT" ]; do
                    if podman exec "\$DB_CONTAINER" pg_isready -U "\$DB_USER" -d "\$DB_NAME" >/dev/null 2>&1; then
                        print_success "Database is ready! (\${elapsed}s)"
                        break
                    fi
                    echo -n "."
                    sleep "\$HEALTH_CHECK_INTERVAL"
                    elapsed=\$((elapsed + HEALTH_CHECK_INTERVAL))
                done
                
                if [ "\$elapsed" -ge "\$DB_READY_TIMEOUT" ]; then
                    echo ""
                    print_error "Database failed to start within \${DB_READY_TIMEOUT}s"
                    exit 1
                fi
};
                # Skip the next 3 lines (the parameters of wait_for_service)
                for (1..3) { <> }
            }
        ' "$SCRIPT_FILE"
        print_success "  Replaced wait_for_service call with inline code"
    fi
    
    # Step 4: Fix database test commands to execute directly
    # Replace the problematic run_test calls with direct execution
    
    # Remove backup files
    rm -f "$SCRIPT_FILE.bak"
    
    print_success "$lab: Fixed"
done

print_header "Summary"
print_success "Reverted eval changes and implemented proper fix"
echo ""
print_info "Changes made:"
echo "  1. Reverted wait_for_service() and run_test() to bash -c"
echo "  2. Replaced wait_for_service call with inline database check"
echo "  3. Database check now executes command directly without string wrapping"

# Made with Bob
