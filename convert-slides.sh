#!/bin/bash

# Markdown to PowerPoint Conversion Script
# Converts all lecture Markdown files to PowerPoint using Marp

echo "======================================"
echo "Jakarta EE Course - Slide Converter"
echo "======================================"
echo ""

# Check if marp-cli is installed
if ! command -v marp &> /dev/null; then
    echo "❌ Error: marp-cli is not installed"
    echo ""
    echo "Install with: npm install -g @marp-team/marp-cli"
    echo "Or with Homebrew: brew install marp-cli"
    exit 1
fi

echo "✓ Marp CLI found"
echo ""

# Create output directory
mkdir -p slides
echo "✓ Output directory created: slides/"
echo ""

# Convert all lecture files
echo "Converting lecture files..."
echo "----------------------------"

cd 02-Lectures

for file in *.md; do
    if [ -f "$file" ]; then
        echo "Converting: $file"
        marp "$file" -o "../slides/${file%.md}.pptx" --allow-local-files
        
        if [ $? -eq 0 ]; then
            echo "  ✓ Success: ../slides/${file%.md}.pptx"
        else
            echo "  ❌ Failed: $file"
        fi
    fi
done

cd ..

echo ""
echo "======================================"
echo "Conversion Complete!"
echo "======================================"
echo ""
echo "PowerPoint files saved in: slides/"
echo ""
echo "Next steps:"
echo "1. Review slides in PowerPoint"
echo "2. Customize if needed"
echo "3. Share with students"
echo ""
