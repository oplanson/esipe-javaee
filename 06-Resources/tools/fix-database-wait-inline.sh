#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Replace wait_for_service() call with inline database check
# Strategy: Execute podman exec directly instead of passing as string

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

print_header "Replacing wait_for_service() with Inline Database Check"

for lab in "${LABS[@]}"; do
    LAB_DIR="$PROJECT_ROOT/esipe-javaee/03-Labs/$lab"
    SCRIPT_FILE="$LAB_DIR/podman-test.sh"
    
    if [ ! -f "$SCRIPT_FILE" ]; then
        print_error "$lab: podman-test.sh not found"
        continue
    fi
    
    print_info "Processing $lab..."
    
    # Find the line with wait_for_service "Database"
    if grep -q 'wait_for_service "Database"' "$SCRIPT_FILE"; then
        # Create a temporary file with the replacement
        awk '
        /wait_for_service "Database"/ {
            print "                # Wait for database to be ready"
            print "                print_info \"Waiting for Database to be ready...\""
            print "                local elapsed=0"
            print "                while [ \"$elapsed\" -lt \"$DB_READY_TIMEOUT\" ]; do"
            print "                    if podman exec \"$DB_CONTAINER\" pg_isready -U \"$DB_USER\" -d \"$DB_NAME\" >/dev/null 2>&1; then"
            print "                        print_success \"Database is ready! (${elapsed}s)\""
            print "                        break"
            print "                    fi"
            print "                    echo -n \".\""
            print "                    sleep \"$HEALTH_CHECK_INTERVAL\""
            print "                    elapsed=$((elapsed + HEALTH_CHECK_INTERVAL))"
            print "                done"
            print "                "
            print "                if [ \"$elapsed\" -ge \"$DB_READY_TIMEOUT\" ]; then"
            print "                    echo \"\""
            print "                    print_error \"Database failed to start within ${DB_READY_TIMEOUT}s\""
            print "                    exit 1"
            print "                fi"
            # Skip the next 3 lines (parameters of wait_for_service)
            for (i=1; i<=3; i++) getline
            next
        }
        { print }
        ' "$SCRIPT_FILE" > "$SCRIPT_FILE.tmp"
        
        mv "$SCRIPT_FILE.tmp" "$SCRIPT_FILE"
        print_success "  Replaced wait_for_service with inline code"
    else
        print_info "  No wait_for_service call found"
    fi
    
    print_success "$lab: Fixed"
done

print_header "Summary"
print_success "Replaced wait_for_service() calls with inline database checks"
echo ""
print_info "Benefits:"
echo "  1. Command executes directly without string wrapping"
echo "  2. Variables expand naturally in the script context"
echo "  3. No bash -c or eval complications"
echo "  4. Simpler and more reliable"

# Made with Bob
