#!/bin/bash

# Smart Copyright Update Script
# Only updates copyright year if the file has been modified since last commit
# Uses Git to detect file modifications

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "======================================"
echo "Smart Copyright Update Script"
echo "======================================"
echo ""

# Determine the base directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$SCRIPT_DIR/../.."
cd "$REPO_ROOT"

# Check if we're in a git repository
if ! git rev-parse --git-dir > /dev/null 2>&1; then
    echo -e "${RED}❌ Error: Not a git repository${NC}"
    exit 1
fi

# Get current year
CURRENT_YEAR=$(date +%Y)

# Counters
updated=0
skipped_unmodified=0
skipped_current=0
no_copyright=0

echo "Checking for modified files with outdated copyrights..."
echo "Current year: $CURRENT_YEAR"
echo "----------------------------"
echo ""

# Function to get file's last modification year from Git
get_last_modified_year() {
    local file="$1"
    # Get the year of the last commit that modified this file
    git log -1 --format="%ad" --date=format:"%Y" -- "$file" 2>/dev/null
}

# Function to extract copyright year from file
get_copyright_year() {
    local file="$1"
    # Look for copyright pattern and extract year or year range
    grep -o "© Copyright [0-9-]* Olivier Planson" "$file" 2>/dev/null | grep -o "[0-9-]*" | head -1
}

# Function to update copyright in file
update_copyright() {
    local file="$1"
    local old_year="$2"
    local new_year_range="$3"
    
    # Create backup
    cp "$file" "$file.bak"
    
    # Update copyright based on file type
    case "$file" in
        *.java)
            sed -i '' "s|/\* © Copyright $old_year Olivier Planson\. All rights reserved\. Reproduction prohibited\. Made with IBM Bob\. \*/|/* © Copyright $new_year_range Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */|g" "$file"
            ;;
        *.xml|*.html)
            sed -i '' "s|<!-- © Copyright $old_year Olivier Planson\. All rights reserved\. Reproduction prohibited\. Made with IBM Bob\. -->|<!-- © Copyright $new_year_range Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->|g" "$file"
            ;;
        *.sh|*.py)
            sed -i '' "s|# © Copyright $old_year Olivier Planson\. All rights reserved\. Reproduction prohibited\. Made with IBM Bob\.|# © Copyright $new_year_range Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.|g" "$file"
            ;;
        *.md)
            sed -i '' "s|# © Copyright $old_year Olivier Planson\. All rights reserved\. Reproduction prohibited\. Made with IBM Bob\.|# © Copyright $new_year_range Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.|g" "$file"
            ;;
        *.jsp)
            sed -i '' "s|<%-- © Copyright $old_year Olivier Planson\. All rights reserved\. Reproduction prohibited\. Made with IBM Bob\. --%>|<%-- © Copyright $new_year_range Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>|g" "$file"
            ;;
    esac
    
    # Check if file was actually modified
    if ! diff -q "$file" "$file.bak" > /dev/null 2>&1; then
        rm "$file.bak"
        return 0
    else
        mv "$file.bak" "$file"
        return 1
    fi
}

# Get list of all files with copyrights
echo "Scanning files..."
FILES=$(find . -type f \( -name "*.java" -o -name "*.md" -o -name "*.sh" -o -name "*.xml" -o -name "*.html" -o -name "*.jsp" -o -name "*.py" \) | grep -v ".git" | grep -v "target/" | grep -v "node_modules/")

for file in $FILES; do
    # Check if file has copyright
    if ! grep -q "© Copyright.*Olivier Planson" "$file"; then
        continue
    fi
    
    # Get copyright year from file
    copyright_year=$(get_copyright_year "$file")
    
    if [ -z "$copyright_year" ]; then
        continue
    fi
    
    # Check if copyright already has current year
    if echo "$copyright_year" | grep -q "$CURRENT_YEAR"; then
        echo -e "${BLUE}⏭️  Already current: $file (${copyright_year})${NC}"
        ((skipped_current++))
        continue
    fi
    
    # Get last modification year from Git
    last_modified_year=$(get_last_modified_year "$file")
    
    if [ -z "$last_modified_year" ]; then
        echo -e "${YELLOW}⚠️  No Git history: $file${NC}"
        continue
    fi
    
    # Check if file was modified this year
    if [ "$last_modified_year" != "$CURRENT_YEAR" ]; then
        echo -e "${BLUE}⏭️  Not modified this year: $file (last: $last_modified_year, copyright: $copyright_year)${NC}"
        ((skipped_unmodified++))
        continue
    fi
    
    # File was modified this year, update copyright
    # Determine new year range
    if echo "$copyright_year" | grep -q "-"; then
        # Already has range, update end year
        start_year=$(echo "$copyright_year" | cut -d'-' -f1)
        new_year_range="$start_year-$CURRENT_YEAR"
    else
        # Single year, create range
        new_year_range="$copyright_year-$CURRENT_YEAR"
    fi
    
    # Update copyright
    if update_copyright "$file" "$copyright_year" "$new_year_range"; then
        echo -e "${GREEN}✓ Updated: $file ($copyright_year → $new_year_range)${NC}"
        ((updated++))
    else
        echo -e "${YELLOW}⚠️  No change needed: $file${NC}"
    fi
done

echo ""
echo "======================================"
echo "Update Summary"
echo "======================================"
echo -e "${GREEN}✓ Updated: $updated${NC}"
echo -e "${BLUE}⏭️  Skipped (not modified this year): $skipped_unmodified${NC}"
echo -e "${BLUE}⏭️  Skipped (already current): $skipped_current${NC}"
echo ""
echo "Logic:"
echo "- Only updates files modified in $CURRENT_YEAR (via Git history)"
echo "- Preserves original creation year"
echo "- Creates or updates year range (e.g., 2025 → 2025-2026)"
echo "- Skips files already showing current year"
echo ""

# Made with Bob
