#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Script to fix podman run command for docker-compose mode
# Adds network connection and environment variables

set -o pipefail
set -o nounset

echo "=========================================="
echo "Fixing podman run for docker-compose mode"
echo "=========================================="
echo ""

# Labs that use docker-compose
LABS=(
    "Lab03-JPA"
    "Lab04-CDI"
    "Lab04B-EJB"
    "Lab05-REST"
    "Lab06-DDD"
    "Lab07-Hexagonal"
    "Lab09-Security"
)

FIXED=0
FAILED=0

for LAB in "${LABS[@]}"; do
    SCRIPT_PATH="esipe-javaee/03-Labs/${LAB}/podman-test.sh"
    
    if [ ! -f "$SCRIPT_PATH" ]; then
        echo "❌ File not found: $SCRIPT_PATH"
        ((FAILED++))
        continue
    fi
    
    echo "🔧 $LAB: Fixing podman run command"
    
    # Create backup
    cp "$SCRIPT_PATH" "${SCRIPT_PATH}.bak"
    
    # Check if already fixed
    if grep -q "\\-\\-network" "$SCRIPT_PATH"; then
        echo "   ✅ Already fixed (--network found)"
        ((FIXED++))
        continue
    fi
    
    # Find the podman run command and add network + env vars
    # The fix adds:
    # --network solution_default \
    # -e DB_HOST=banking-db \
    # -e DB_PORT=5432 \
    # -e DB_NAME=bankdb \
    # -e DB_USER=bankuser \
    # -e DB_PASSWORD=bankpass \
    
    # Use awk to insert lines after "podman run -d \"
    awk '
    /podman run -d \\$/ {
        print
        print "        --network solution_default \\"
        print "        -e DB_HOST=banking-db \\"
        print "        -e DB_PORT=5432 \\"
        print "        -e DB_NAME=bankdb \\"
        print "        -e DB_USER=bankuser \\"
        print "        -e DB_PASSWORD=bankpass \\"
        next
    }
    { print }
    ' "${SCRIPT_PATH}.bak" > "$SCRIPT_PATH"
    
    if [ $? -eq 0 ]; then
        echo "   ✅ Fixed successfully"
        ((FIXED++))
    else
        echo "   ❌ Failed to fix"
        mv "${SCRIPT_PATH}.bak" "$SCRIPT_PATH"
        ((FAILED++))
    fi
    echo ""
done

echo "=========================================="
echo "Summary:"
echo "  Fixed: $FIXED"
echo "  Failed: $FAILED"
echo "=========================================="

if [ $FAILED -eq 0 ]; then
    echo "✅ All labs fixed successfully!"
    echo ""
    echo "Changes applied:"
    echo "  - Added --network solution_default"
    echo "  - Added DB environment variables"
    echo "  - Container can now connect to PostgreSQL"
    exit 0
else
    echo "❌ Some fixes failed. Check the output above."
    exit 1
fi

# Made with Bob
