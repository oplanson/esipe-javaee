#!/bin/bash

# Add Copyright to Java Files - Correct Placement
# Places copyright AFTER package declaration to avoid compilation errors

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "======================================"
echo "Add Copyright to Java Files (Correct)"
echo "======================================"
echo ""

# Determine the base directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$SCRIPT_DIR/../.."
cd "$REPO_ROOT"

# Get current year
CURRENT_YEAR=$(date +%Y)

# Copyright notice
COPYRIGHT="/* © Copyright 2025-$CURRENT_YEAR Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */"

# Counters
added=0
skipped=0

echo "Adding copyright to Java files..."
echo "Current year: $CURRENT_YEAR"
echo "----------------------------"
echo ""

# Function to add copyright to Java file AFTER package declaration
add_java_copyright() {
    local file="$1"
    
    if [ -f "$file" ]; then
        # Check if file already has copyright
        if grep -q "© Copyright.*Olivier Planson" "$file"; then
            echo -e "${BLUE}⏭️  Already has copyright: $file${NC}"
            ((skipped++))
            return
        fi
        
        # Check if file has package declaration
        if grep -q "^package " "$file"; then
            # Insert copyright after package declaration
            awk -v copyright="$COPYRIGHT" '
                /^package / {
                    print $0
                    print ""
                    print copyright
                    next
                }
                { print }
            ' "$file" > "$file.tmp"
            
            mv "$file.tmp" "$file"
            echo -e "${GREEN}✓ Added copyright after package: $file${NC}"
            ((added++))
        else
            # No package declaration, add at beginning
            {
                echo "$COPYRIGHT"
                echo ""
                cat "$file"
            } > "$file.tmp"
            
            mv "$file.tmp" "$file"
            echo -e "${GREEN}✓ Added copyright at beginning: $file${NC}"
            ((added++))
        fi
    fi
}

# Process all Java files
find ./03-Labs -name "*.java" -type f | while read file; do
    add_java_copyright "$file"
done

echo ""
echo "======================================"
echo "Summary"
echo "======================================"
echo -e "${GREEN}✓ Added: $added${NC}"
echo -e "${BLUE}⏭️  Skipped (already has copyright): $skipped${NC}"
echo ""
echo "Copyright format (placed after package declaration):"
echo "$COPYRIGHT"
echo "Current year: $CURRENT_YEAR"
echo ""

# Made with Bob
