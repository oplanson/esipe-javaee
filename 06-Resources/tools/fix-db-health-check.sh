#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Script to fix database health check command
# Changes: docker exec -> podman exec

set -o pipefail
set -o nounset

echo "=========================================="
echo "Fixing database health check command"
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
    
    echo "🔧 $LAB: Fixing database health check"
    
    # Create backup
    cp "$SCRIPT_PATH" "${SCRIPT_PATH}.bak"
    
    # Check if already fixed
    if grep -q 'podman exec.*pg_isready' "$SCRIPT_PATH"; then
        echo "   ✅ Already fixed (podman exec found)"
        ((FIXED++))
        continue
    fi
    
    # Replace docker exec with podman exec
    if sed -i '' 's/docker exec/podman exec/g' "$SCRIPT_PATH"; then
        echo "   ✅ Fixed successfully (docker exec → podman exec)"
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
    echo "  - Changed 'docker exec' to 'podman exec'"
    echo "  - Database health check now uses podman"
    exit 0
else
    echo "❌ Some fixes failed. Check the output above."
    exit 1
fi

# Made with Bob
