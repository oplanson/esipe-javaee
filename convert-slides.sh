#!/bin/bash

# Markdown to PowerPoint Conversion Script
# Converts all lecture Markdown files to PowerPoint using Marp
# Only converts if MD file is newer than PPTX or PPTX doesn't exist

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "======================================"
echo "Jakarta EE Course - Slide Converter"
echo "======================================"
echo ""

# Check if marp-cli is installed
if ! command -v marp &> /dev/null; then
    echo -e "${RED}❌ Error: marp-cli is not installed${NC}"
    echo ""
    echo "Install with: npm install -g @marp-team/marp-cli"
    echo "Or with Homebrew: brew install marp-cli"
    exit 1
fi

echo -e "${GREEN}✓ Marp CLI found${NC}"
echo ""

# Determine the base directory (where the script is located)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Check if mermaid conversion script exists
MERMAID_SCRIPT="$SCRIPT_DIR/convert-mermaid-to-images.sh"
REPLACE_SCRIPT="$SCRIPT_DIR/replace-mermaid-with-images.py"

if [ ! -f "$MERMAID_SCRIPT" ]; then
    echo -e "${YELLOW}⚠️  Warning: Mermaid conversion script not found${NC}"
    echo "   Expected at: $MERMAID_SCRIPT"
    echo "   Mermaid diagrams will not be converted to images"
    MERMAID_SCRIPT=""
else
    echo -e "${GREEN}✓ Mermaid conversion script found${NC}"
fi

if [ ! -f "$REPLACE_SCRIPT" ]; then
    echo -e "${YELLOW}⚠️  Warning: Python replacement script not found${NC}"
    echo "   Expected at: $REPLACE_SCRIPT"
    echo "   Mermaid blocks will not be replaced with images"
    REPLACE_SCRIPT=""
else
    echo -e "${GREEN}✓ Python replacement script found${NC}"
fi
echo ""

# Create output directory
SLIDES_DIR="$SCRIPT_DIR/slides"
mkdir -p "$SLIDES_DIR"
echo -e "${GREEN}✓ Output directory created: $SLIDES_DIR${NC}"

# Check for custom theme
THEME_FILE="$SCRIPT_DIR/02-Lectures/esipe-theme.css"
if [ -f "$THEME_FILE" ]; then
    echo -e "${GREEN}✓ Custom theme found: esipe-theme.css${NC}"
    THEME_ARG="--theme $THEME_FILE"
else
    echo -e "${YELLOW}⚠️  Custom theme not found, using default${NC}"
    THEME_ARG=""
fi
echo ""

# Counters
converted=0
skipped=0
failed=0

# Convert all lecture files
echo "Checking lecture files for conversion..."
echo "----------------------------"

LECTURES_DIR="$SCRIPT_DIR/02-Lectures"
cd "$LECTURES_DIR"

for file in *.md; do
    if [ -f "$file" ]; then
        output_file="$SLIDES_DIR/${file%.md}.pptx"
        
        # Check if output file exists
        if [ -f "$output_file" ]; then
            # Get modification times
            md_time=$(stat -f %m "$file" 2>/dev/null || stat -c %Y "$file" 2>/dev/null)
            pptx_time=$(stat -f %m "$output_file" 2>/dev/null || stat -c %Y "$output_file" 2>/dev/null)
            
            # Compare modification times
            if [ "$md_time" -gt "$pptx_time" ]; then
                echo -e "${YELLOW}📝 $file${NC} - MD is newer, removing old PPTX and reconverting..."
                rm -f "$output_file"
                
                # Convert Mermaid diagrams first if script is available
                if [ -n "$MERMAID_SCRIPT" ]; then
                    echo -e "  ${BLUE}🔄 Converting Mermaid diagrams to images...${NC}"
                    bash "$MERMAID_SCRIPT" "$file" > /dev/null 2>&1
                    
                    # Replace Mermaid blocks with images (keeping original in comments)
                    if [ -n "$REPLACE_SCRIPT" ]; then
                        echo -e "  ${BLUE}🔄 Replacing Mermaid blocks with image references...${NC}"
                        python3 "$REPLACE_SCRIPT" "$file" > /dev/null 2>&1
                    fi
                fi
                
                # Convert to PPTX
                marp "$file" -o "$output_file" $THEME_ARG --allow-local-files --no-stdin
                
                if [ $? -eq 0 ]; then
                    echo -e "  ${GREEN}✓ Success: $output_file${NC}"
                    ((converted++))
                else
                    echo -e "  ${RED}❌ Failed: $file${NC}"
                    ((failed++))
                fi
            else
                echo -e "${BLUE}⏭️  $file${NC} - PPTX is up-to-date, skipping"
                ((skipped++))
            fi
        else
            # PPTX doesn't exist, convert
            echo -e "${YELLOW}📝 $file${NC} - No PPTX found, converting..."
            
            # Convert Mermaid diagrams first if script is available
            if [ -n "$MERMAID_SCRIPT" ]; then
                echo -e "  ${BLUE}🔄 Converting Mermaid diagrams to images...${NC}"
                bash "$MERMAID_SCRIPT" "$file" > /dev/null 2>&1
                
                # Replace Mermaid blocks with images (keeping original in comments)
                if [ -n "$REPLACE_SCRIPT" ]; then
                    echo -e "  ${BLUE}🔄 Replacing Mermaid blocks with image references...${NC}"
                    python3 "$REPLACE_SCRIPT" "$file" > /dev/null 2>&1
                fi
            fi
            
            # Convert to PPTX
            marp "$file" -o "$output_file" $THEME_ARG --allow-local-files --no-stdin
            
            if [ $? -eq 0 ]; then
                echo -e "  ${GREEN}✓ Success: $output_file${NC}"
                ((converted++))
            else
                echo -e "  ${RED}❌ Failed: $file${NC}"
                ((failed++))
            fi
        fi
    fi
done

cd ..

echo ""
echo "======================================"
echo "Conversion Summary"
echo "======================================"
echo -e "${GREEN}✓ Converted: $converted${NC}"
echo -e "${BLUE}⏭️  Skipped (up-to-date): $skipped${NC}"
if [ $failed -gt 0 ]; then
    echo -e "${RED}❌ Failed: $failed${NC}"
fi
echo ""
echo "PowerPoint files saved in: slides/"
echo ""
echo "Next steps:"
echo "1. Review slides in PowerPoint"
echo "2. Customize if needed"
echo "3. Share with students"
echo ""
