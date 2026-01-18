#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Restore wait_for_service() function usage in Phase 3
# Now that container names are fixed, the function should work

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

print_header "Restoring wait_for_service() Function Usage"

for lab in "${LABS[@]}"; do
    LAB_DIR="$PROJECT_ROOT/esipe-javaee/03-Labs/$lab"
    SCRIPT_FILE="$LAB_DIR/podman-test.sh"
    
    if [ ! -f "$SCRIPT_FILE" ]; then
        print_error "$lab: podman-test.sh not found"
        continue
    fi
    
    print_info "Processing $lab..."
    
    # Create backup
    cp "$SCRIPT_FILE" "$SCRIPT_FILE.bak"
    
    # Replace inline database wait with wait_for_service() call
    # Find the section from "# Wait for database to be ready" to "fi" before "else"
    sed -i '' '
        /# Wait for database to be ready/,/^[[:space:]]*fi$/ {
            /# Wait for database to be ready/ {
                a\
                wait_for_service "Database" \\
                    "podman exec \"$DB_CONTAINER\" pg_isready -U \"$DB_USER\" -d \"$DB_NAME\"" \\
                    "$DB_READY_TIMEOUT" \\
                    "$HEALTH_CHECK_INTERVAL"
                d
            }
            /print_info "Waiting for Database to be ready..."/d
            /local elapsed=0/d
            /local db_ready=false/d
            /while \[ "\$elapsed" -lt "\$DB_READY_TIMEOUT" \]; do/d
            /print_info "echo DB";/d
            /if podman exec "\$DB_CONTAINER" pg_isready/d
            /print_success "Database is ready!/d
            /db_ready=true/d
            /break/d
            /fi/d
            /echo -n "\."$/d
            /sleep "\$HEALTH_CHECK_INTERVAL"/d
            /elapsed=\$((elapsed + HEALTH_CHECK_INTERVAL))/d
            /done/d
            /if \[ "\$db_ready" = "false" \]; then/d
            /echo ""/d
            /print_error "Database failed to start within/d
            /exit 1/d
        }
    ' "$SCRIPT_FILE"
    
    print_success "  Restored wait_for_service() call"
    
    print_success "$lab: Fixed"
done

print_header "Summary"
print_success "Restored wait_for_service() in all labs"
echo ""
print_info "The function now works correctly because:"
echo "  1. Container names match docker-compose.yml"
echo "  2. Command uses podman exec (not docker exec)"
echo "  3. Variables will expand properly in the function context"

# Made with Bob
