#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Script to fix WAR_NAME in all podman-test.sh scripts to match pom.xml artifactId

set -o pipefail
set -o nounset

echo "=========================================="
echo "Fixing WAR_NAME in podman-test.sh scripts"
echo "=========================================="
echo ""

# Define the corrections needed
declare -a LABS=(
    "Lab03-JPA:banking-jpa-app-1.0-SNAPSHOT.war"
    "Lab04-CDI:banking-cdi-app-1.0-SNAPSHOT.war"
    "Lab04B-EJB:banking-ejb-app-1.0-SNAPSHOT.war"
    "Lab05-REST:banking-rest-app-1.0-SNAPSHOT.war"
    "Lab06-DDD:banking-ddd-app-1.0-SNAPSHOT.war"
    "Lab07-Hexagonal:banking-hexagonal-app-1.0-SNAPSHOT.war"
    "Lab09-Security:bank-security-1.0.0.war"
)

FIXED=0
FAILED=0

for entry in "${LABS[@]}"; do
    LAB_DIR=$(echo "$entry" | cut -d: -f1)
    NEW_WAR=$(echo "$entry" | cut -d: -f2)
    
    SCRIPT_PATH="esipe-javaee/03-Labs/${LAB_DIR}/podman-test.sh"
    
    if [ ! -f "$SCRIPT_PATH" ]; then
        echo "❌ File not found: $SCRIPT_PATH"
        ((FAILED++))
        continue
    fi
    
    # Get current WAR_NAME
    CURRENT_WAR=$(grep '^WAR_NAME=' "$SCRIPT_PATH" | cut -d'"' -f2)
    
    if [ "$CURRENT_WAR" = "$NEW_WAR" ]; then
        echo "✅ $LAB_DIR: Already correct ($NEW_WAR)"
        ((FIXED++))
        continue
    fi
    
    echo "🔧 $LAB_DIR: Updating WAR_NAME"
    echo "   Old: $CURRENT_WAR"
    echo "   New: $NEW_WAR"
    
    # Create backup
    cp "$SCRIPT_PATH" "${SCRIPT_PATH}.bak"
    
    # Update WAR_NAME using sed
    if sed -i '' "s|^WAR_NAME=.*|WAR_NAME=\"${NEW_WAR}\"|" "$SCRIPT_PATH"; then
        echo "   ✅ Updated successfully"
        ((FIXED++))
    else
        echo "   ❌ Failed to update"
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
    echo "✅ All WAR names fixed successfully!"
    exit 0
else
    echo "❌ Some fixes failed. Check the output above."
    exit 1
fi

# Made with Bob
