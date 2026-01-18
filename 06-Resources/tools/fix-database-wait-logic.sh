#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Fix database wait logic - use a flag to track success
# Problem: Current logic doesn't properly detect if database is ready

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

print_header "Fixing Database Wait Logic"

for lab in "${LABS[@]}"; do
    LAB_DIR="$PROJECT_ROOT/esipe-javaee/03-Labs/$lab"
    SCRIPT_FILE="$LAB_DIR/podman-test.sh"
    
    if [ ! -f "$SCRIPT_FILE" ]; then
        print_error "$lab: podman-test.sh not found"
        continue
    fi
    
    print_info "Processing $lab..."
    
    # Replace the database wait logic with proper flag-based approach
    awk '
    /# Wait for database to be ready/ {
        print "                # Wait for database to be ready"
        print "                print_info \"Waiting for Database to be ready...\""
        print "                local elapsed=0"
        print "                local db_ready=false"
        print "                while [ \"$elapsed\" -lt \"$DB_READY_TIMEOUT\" ]; do"
        print "                    if podman exec \"$DB_CONTAINER\" pg_isready -U \"$DB_USER\" -d \"$DB_NAME\" >/dev/null 2>&1; then"
        print "                        print_success \"Database is ready! (${elapsed}s)\""
        print "                        db_ready=true"
        print "                        break"
        print "                    fi"
        print "                    echo -n \".\""
        print "                    sleep \"$HEALTH_CHECK_INTERVAL\""
        print "                    elapsed=$((elapsed + HEALTH_CHECK_INTERVAL))"
        print "                done"
        print "                "
        print "                if [ \"$db_ready\" = \"false\" ]; then"
        print "                    echo \"\""
        print "                    print_error \"Database failed to start within ${DB_READY_TIMEOUT}s\""
        print "                    exit 1"
        print "                fi"
        # Skip the old implementation (until we hit "else" for docker-compose failure)
        while (getline > 0) {
            if (/else/) {
                print
                break
            }
        }
        next
    }
    { print }
    ' "$SCRIPT_FILE" > "$SCRIPT_FILE.tmp"
    
    mv "$SCRIPT_FILE.tmp" "$SCRIPT_FILE"
    print_success "  Fixed database wait logic with success flag"
    
    print_success "$lab: Fixed"
done

print_header "Summary"
print_success "Fixed database wait logic in all labs"
echo ""
print_info "Changes made:"
echo "  1. Added db_ready flag to track success"
echo "  2. Set flag to true when pg_isready succeeds"
echo "  3. Check flag after loop to determine success/failure"
echo "  4. Only exit with error if flag is still false"

# Made with Bob
