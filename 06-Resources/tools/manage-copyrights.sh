#!/bin/bash
# © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Unified Copyright Management Script
# Manages copyright notices across all file types with smart detection
# Categories:
# 1. Java source files (internal code)
# 2. Source code files (sh, py, xml, md - internal)
# 3. User-visible files (html, jsp - visible in browser)

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "======================================"
echo "Unified Copyright Management"
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
skipped_current=0
skipped_unmodified=0
added=0

echo "Checking copyright consistency..."
echo "Current year: $CURRENT_YEAR"
echo "----------------------------"
echo ""

# Function to get file's last modification year from Git
get_last_modified_year() {
    local file="$1"
    git log -1 --format="%ad" --date=format:"%Y" -- "$file" 2>/dev/null
}

# Function to extract copyright year from file
get_copyright_year() {
    local file="$1"
    grep -o "© Copyright [0-9-]* Olivier Planson" "$file" 2>/dev/null | grep -o "[0-9-]*" | head -1
}

# Function to check if file has copyright
has_copyright() {
    local file="$1"
    grep -q "© Copyright.*Olivier Planson" "$file" 2>/dev/null
}

# Function to add copyright to file (for files without one)
add_copyright() {
    local file="$1"
    local copyright_text="© Copyright $CURRENT_YEAR Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob."
    
    case "$file" in
        *.java)
            # Java: Add after package declaration
            if grep -q "^package " "$file"; then
                awk -v copyright="/* $copyright_text */" '
                    /^package / {
                        print $0
                        print ""
                        print copyright
                        next
                    }
                    { print }
                ' "$file" > "$file.tmp"
            else
                {
                    echo "/* $copyright_text */"
                    echo ""
                    cat "$file"
                } > "$file.tmp"
            fi
            ;;
        *.html)
            # HTML: Add at beginning (internal comment)
            {
                echo "<!-- $copyright_text -->"
                cat "$file"
            } > "$file.tmp"
            ;;
        *.jsp)
            # JSP: Add at beginning (internal comment)
            {
                echo "<%-- $copyright_text --%>"
                cat "$file"
            } > "$file.tmp"
            ;;
        *.sh|*.py)
            # Shell/Python: Add after shebang if present
            if head -1 "$file" | grep -q "^#!"; then
                {
                    head -1 "$file"
                    echo "# $copyright_text"
                    tail -n +2 "$file"
                } > "$file.tmp"
            else
                {
                    echo "# $copyright_text"
                    cat "$file"
                } > "$file.tmp"
            fi
            ;;
        *.xml)
            # XML: Add after XML declaration if present
            if head -1 "$file" | grep -q "^<?xml"; then
                {
                    head -1 "$file"
                    echo "<!-- $copyright_text -->"
                    tail -n +2 "$file"
                } > "$file.tmp"
            else
                {
                    echo "<!-- $copyright_text -->"
                    cat "$file"
                } > "$file.tmp"
            fi
            ;;
        *.md)
            # Markdown: Check if file has YAML front matter (Marp slides)
            if head -1 "$file" | grep -q "^---$"; then
                # Has YAML front matter
                # 1. Update footer copyright year in YAML (visible to users)
                # 2. Add internal copyright comment after YAML
                awk -v copyright="<!-- $copyright_text -->" -v year="$CURRENT_YEAR" '
                    BEGIN { in_frontmatter = 0; frontmatter_ended = 0 }
                    /^---$/ {
                        if (in_frontmatter == 0) {
                            in_frontmatter = 1
                            print $0
                            next
                        } else if (in_frontmatter == 1 && frontmatter_ended == 0) {
                            frontmatter_ended = 1
                            print $0
                            print ""
                            print copyright
                            print ""
                            next
                        }
                    }
                    /^footer:/ && in_frontmatter == 1 {
                        # Update copyright year and ensure full copyright message
                        # Format: "Lecture X: Title | © YEAR Olivier Planson - All rights reserved. Reproduction prohibited."
                        gsub(/© [0-9]{4}[^|]*/, "© " year " Olivier Planson - All rights reserved. Reproduction prohibited.")
                        print $0
                        next
                    }
                    { print }
                ' "$file" > "$file.tmp"
            else
                # No YAML front matter, add at beginning
                {
                    echo "<!-- $copyright_text -->"
                    echo ""
                    cat "$file"
                } > "$file.tmp"
            fi
            ;;
    esac
    
    if [ -f "$file.tmp" ]; then
        mv "$file.tmp" "$file"
        return 0
    fi
    return 1
}

# Function to update copyright year
update_copyright() {
    local file="$1"
    local old_year="$2"
    local new_year_range="$3"
    
    # Create backup
    cp "$file" "$file.bak"
    
    # Escape dots for sed
    old_year_escaped=$(echo "$old_year" | sed 's/\./\\./g')
    
    # Update copyright based on file type
    case "$file" in
        *.java)
            sed -i '' "s|/\* © Copyright $old_year_escaped Olivier Planson\. All rights reserved\. Reproduction prohibited\. Made with IBM Bob\. \*/|/* © Copyright $new_year_range Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */|g" "$file"
            ;;
        *.xml|*.html)
            sed -i '' "s|<!-- © Copyright $old_year_escaped Olivier Planson\. All rights reserved\. Reproduction prohibited\. Made with IBM Bob\. -->|<!-- © Copyright $new_year_range Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->|g" "$file"
            ;;
        *.sh|*.py)
            sed -i '' "s|# © Copyright $old_year_escaped Olivier Planson\. All rights reserved\. Reproduction prohibited\. Made with IBM Bob\.|# © Copyright $new_year_range Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.|g" "$file"
            ;;
        *.md)
            # Markdown files: copyright can be at beginning or after YAML front matter
            sed -i '' "s|<!-- © Copyright $old_year_escaped Olivier Planson\. All rights reserved\. Reproduction prohibited\. Made with IBM Bob\. -->|<!-- © Copyright $new_year_range Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->|g" "$file"
            ;;
        *.jsp)
            sed -i '' "s|<%-- © Copyright $old_year_escaped Olivier Planson\. All rights reserved\. Reproduction prohibited\. Made with IBM Bob\. --%>|<%-- © Copyright $new_year_range Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>|g" "$file"
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

# Get list of all relevant files
echo "Scanning files..."
FILES=$(find . -type f \( -name "*.java" -o -name "*.md" -o -name "*.sh" -o -name "*.xml" -o -name "*.html" -o -name "*.jsp" -o -name "*.py" \) | grep -v ".git" | grep -v "target/" | grep -v "node_modules/" | grep -v ".bak")

for file in $FILES; do
    # Category 1: Java files (internal code)
    # Category 2: Source code files (sh, py, xml, md - internal)
    # Category 3: User-visible files (html, jsp)
    
    # Check if file has copyright
    if ! has_copyright "$file"; then
        # Add copyright to files without one
        if add_copyright "$file"; then
            echo -e "${GREEN}✓ Added copyright: $file${NC}"
            ((added++))
        fi
        continue
    fi
    
    # Get copyright year from file
    copyright_year=$(get_copyright_year "$file")
    
    if [ -z "$copyright_year" ]; then
        continue
    fi
    
    # Check if copyright already has current year
    if echo "$copyright_year" | grep -q "$CURRENT_YEAR"; then
        echo -e "${BLUE}⏭️  Already current: $file ($copyright_year)${NC}"
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
        echo -e "${BLUE}⏭️  Not modified this year: $file (last: $last_modified_year)${NC}"
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
echo "Summary"
echo "======================================"
echo -e "${GREEN}✓ Updated: $updated${NC}"
echo -e "${GREEN}✓ Added: $added${NC}"
echo -e "${BLUE}⏭️  Skipped (not modified this year): $skipped_unmodified${NC}"
echo -e "${BLUE}⏭️  Skipped (already current): $skipped_current${NC}"
echo ""
echo "Copyright Management Logic:"
echo "1. Java files: Copyright after package declaration"
echo "2. Source files (sh, py, xml, md): Copyright at beginning"
echo "3. User-visible files (html, jsp): Internal comments only"
echo "4. Only updates files modified in $CURRENT_YEAR (via Git)"
echo "5. Preserves original creation year"
echo "6. Creates or updates year range (e.g., 2025 → 2025-2026)"
echo ""

if [ $updated -gt 0 ] || [ $added -gt 0 ]; then
    echo -e "${GREEN}✓ Copyright management completed${NC}"
else
    echo -e "${BLUE}✓ All copyrights are up to date${NC}"
fi
echo ""

# Made with Bob