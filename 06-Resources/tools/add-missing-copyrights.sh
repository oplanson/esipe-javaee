#!/bin/bash

# Add Copyright to Files Without One
# Adds copyright notice with current year to HTML, JSP, and Java files that don't have one

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "======================================"
echo "Add Missing Copyrights Script"
echo "======================================"
echo ""

# Determine the base directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$SCRIPT_DIR/../.."
cd "$REPO_ROOT"

# Get current year
CURRENT_YEAR=$(date +%Y)

# Copyright notice with current year
COPYRIGHT="© Copyright 2025-$CURRENT_YEAR Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob."

# Counters
added=0
skipped=0

echo "Adding copyright to files without one..."
echo "----------------------------"

# Function to add copyright to HTML file
add_html_copyright() {
    local file="$1"
    
    if [ -f "$file" ]; then
        # Check if file already has copyright
        if ! grep -q "© Copyright.*Olivier Planson" "$file"; then
            # Create temp file with copyright
            {
                echo "<!-- $COPYRIGHT -->"
                cat "$file"
            } > "$file.tmp"
            
            mv "$file.tmp" "$file"
            echo -e "${GREEN}✓ Added copyright: $file${NC}"
            ((added++))
        else
            echo -e "${BLUE}⏭️  Already has copyright: $file${NC}"
            ((skipped++))
        fi
    fi
}

# Function to add copyright to JSP file
add_jsp_copyright() {
    local file="$1"
    
    if [ -f "$file" ]; then
        # Check if file already has copyright
        if ! grep -q "© Copyright.*Olivier Planson" "$file"; then
            # Create temp file with copyright
            {
                echo "<%-- $COPYRIGHT --%>"
                cat "$file"
            } > "$file.tmp"
            
            mv "$file.tmp" "$file"
            echo -e "${GREEN}✓ Added copyright: $file${NC}"
            ((added++))
        else
            echo -e "${BLUE}⏭️  Already has copyright: $file${NC}"
            ((skipped++))
        fi
    fi
}

# Function to add copyright to Java file
add_java_copyright() {
    local file="$1"
    
    if [ -f "$file" ]; then
        # Check if file already has copyright
        if ! grep -q "© Copyright.*Olivier Planson" "$file"; then
            # Check if file starts with package declaration
            if head -1 "$file" | grep -q "^package"; then
                # Insert copyright before package
                {
                    echo "/* $COPYRIGHT */"
                    cat "$file"
                } > "$file.tmp"
            else
                # Insert at beginning
                {
                    echo "/* $COPYRIGHT */"
                    echo ""
                    cat "$file"
                } > "$file.tmp"
            fi
            
            mv "$file.tmp" "$file"
            echo -e "${GREEN}✓ Added copyright: $file${NC}"
            ((added++))
        else
            echo -e "${BLUE}⏭️  Already has copyright: $file${NC}"
            ((skipped++))
        fi
    fi
}

# Process HTML files
echo ""
echo "Processing HTML files..."
find ./03-Labs -name "*.html" -type f | while read file; do
    add_html_copyright "$file"
done

# Process JSP files
echo ""
echo "Processing JSP files..."
find ./03-Labs -name "*.jsp" -type f | while read file; do
    add_jsp_copyright "$file"
done

# Process Java files
echo ""
echo "Processing Java files..."
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
echo "Copyright format:"
echo "$COPYRIGHT"
echo "Current year: $CURRENT_YEAR"
echo ""

# Made with Bob
