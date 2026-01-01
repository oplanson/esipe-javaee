#!/bin/bash

# Copyright Update Script
# Updates all copyright notices to the new format with current year
# © Copyright 2025-CURRENT_YEAR Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "======================================"
echo "Copyright Update Script"
echo "======================================"
echo ""

# Determine the base directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$SCRIPT_DIR/../.."
cd "$REPO_ROOT"

# Get current year
CURRENT_YEAR=$(date +%Y)

# New copyright notice with current year
NEW_COPYRIGHT="© Copyright 2025-$CURRENT_YEAR Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob."

# Counters
updated=0
skipped=0
failed=0

echo "Updating copyright notices..."
echo "----------------------------"

# Function to update copyright in a file
update_copyright() {
    local file="$1"
    local pattern="$2"
    local replacement="$3"
    
    if [ -f "$file" ]; then
        # Check if file contains old copyright
        if grep -q "© Copyright.*202[0-9]" "$file"; then
            # Create backup
            cp "$file" "$file.bak"
            
            # Update copyright based on file type
            case "$file" in
                *.java)
                    # Java files: /* ... */
                    sed -i '' 's|/\*[[:space:]]*© Copyright.*202[0-9][[:space:]]*\*/|/* '"$NEW_COPYRIGHT"' */|g' "$file"
                    sed -i '' 's|/\*[[:space:]]*\*[[:space:]]*© Copyright.*202[0-9][[:space:]]*\*/|/* '"$NEW_COPYRIGHT"' */|g' "$file"
                    ;;
                *.xml|*.html)
                    # XML/HTML files: <!-- ... -->
                    sed -i '' 's|<!--[[:space:]]*© Copyright.*202[0-9][[:space:]]*-->|<!-- '"$NEW_COPYRIGHT"' -->|g' "$file"
                    sed -i '' 's|&copy; Copyright.*202[0-9]|© Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.|g' "$file"
                    ;;
                *.sh|*.py)
                    # Shell/Python files: #
                    sed -i '' 's|^#[[:space:]]*© Copyright.*202[0-9].*$|# '"$NEW_COPYRIGHT"'|g' "$file"
                    ;;
                *.md)
                    # Markdown files: #
                    sed -i '' 's|^#[[:space:]]*© Copyright.*202[0-9].*$|# '"$NEW_COPYRIGHT"'|g' "$file"
                    ;;
                *.jsp)
                    # JSP files: <%-- ... --%>
                    sed -i '' 's|<%--[[:space:]]*© Copyright.*202[0-9][[:space:]]*--%>|<%-- '"$NEW_COPYRIGHT"' --%>|g' "$file"
                    sed -i '' 's|&copy; Copyright.*202[0-9]|© Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.|g' "$file"
                    ;;
            esac
            
            # Check if file was actually modified
            if ! diff -q "$file" "$file.bak" > /dev/null 2>&1; then
                echo -e "${GREEN}✓ Updated: $file${NC}"
                rm "$file.bak"
                ((updated++))
            else
                echo -e "${YELLOW}⚠️  No changes: $file${NC}"
                rm "$file.bak"
                ((skipped++))
            fi
        else
            echo -e "${BLUE}⏭️  Skipped (no copyright): $file${NC}"
            ((skipped++))
        fi
    fi
}

# Find and update all relevant files
echo ""
echo "Processing Java files..."
find . -name "*.java" -type f | while read file; do
    update_copyright "$file"
done

echo ""
echo "Processing Markdown files..."
find . -name "*.md" -type f | while read file; do
    update_copyright "$file"
done

echo ""
echo "Processing Shell scripts..."
find . -name "*.sh" -type f | while read file; do
    update_copyright "$file"
done

echo ""
echo "Processing XML files..."
find . -name "*.xml" -type f | while read file; do
    update_copyright "$file"
done

echo ""
echo "Processing HTML files..."
find . -name "*.html" -type f | while read file; do
    update_copyright "$file"
done

echo ""
echo "Processing JSP files..."
find . -name "*.jsp" -type f | while read file; do
    update_copyright "$file"
done

echo ""
echo "Processing Python files..."
find . -name "*.py" -type f | while read file; do
    update_copyright "$file"
done

echo ""
echo "======================================"
echo "Update Summary"
echo "======================================"
echo -e "${GREEN}✓ Updated: $updated${NC}"
echo -e "${BLUE}⏭️  Skipped: $skipped${NC}"
if [ $failed -gt 0 ]; then
    echo -e "${RED}❌ Failed: $failed${NC}"
fi
echo ""
echo "New copyright format:"
echo "$NEW_COPYRIGHT"
echo "Current year: $CURRENT_YEAR"
echo ""

# Made with Bob
