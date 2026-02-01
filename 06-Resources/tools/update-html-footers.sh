#!/bin/bash
# © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Script to update footer copyright in all HTML files

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Copyright text for footer
FOOTER_COPYRIGHT="© Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob."

echo "🔄 Updating HTML footers with copyright notice..."
echo ""

# Counter
updated=0
skipped=0
errors=0

# Get the script directory and workspace root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WORKSPACE_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"

# Find all HTML files in Labs
find "$WORKSPACE_ROOT/esipe-javaee/03-Labs" -name "*.html" -type f | while read -r file; do
    # Check if file has a footer tag
    if grep -q "<footer>" "$file"; then
        # Check if footer content (not comment) has the correct copyright
        if grep -A 5 "<footer>" "$file" | grep -qE "(©|&copy;) Copyright.*Olivier Planson.*All rights reserved.*Reproduction prohibited.*Made with IBM Bob"; then
            echo -e "${YELLOW}⏭️  Already correct: $file${NC}"
            ((skipped++)) || true
        else
            # Update the footer content
            # Create a temporary file
            temp_file=$(mktemp)
            
            # Use awk to replace footer content
            awk -v copyright="$FOOTER_COPYRIGHT" '
                /<footer>/ {
                    in_footer = 1
                    print "        <footer>"
                    print "            <p>" copyright "</p>"
                    next
                }
                /<\/footer>/ {
                    in_footer = 0
                    print "        </footer>"
                    next
                }
                !in_footer {
                    print
                }
            ' "$file" > "$temp_file"
            
            # Replace original file
            mv "$temp_file" "$file"
            
            echo -e "${GREEN}✓ Updated: $file${NC}"
            ((updated++)) || true
        fi
    else
        # No footer tag found - add one before </body>
        if grep -q "</body>" "$file"; then
            # Create a temporary file
            temp_file=$(mktemp)
            
            # Add footer before </body>
            awk -v copyright="$FOOTER_COPYRIGHT" '
                /<\/body>/ {
                    print "        <footer>"
                    print "            <p>" copyright "</p>"
                    print "        </footer>"
                    print "    </body>"
                    next
                }
                { print }
            ' "$file" > "$temp_file"
            
            # Replace original file
            mv "$temp_file" "$file"
            
            echo -e "${GREEN}✓ Added footer: $file${NC}"
            ((updated++)) || true
        else
            echo -e "${RED}⚠️  No </body> tag found: $file${NC}"
            ((errors++)) || true
        fi
    fi
done

echo ""
echo "📊 Summary:"
echo "  ✓ Updated: $updated files"
echo "  ⏭️  Skipped: $skipped files (already correct)"
echo "  ⚠️  Errors: $errors files"
echo ""
echo "✅ Footer update complete!"

# Made with Bob
