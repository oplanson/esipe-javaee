#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Script to extract Mermaid diagrams from markdown files and convert them to images
# Usage: ./convert-mermaid-to-images.sh [markdown-file or directory]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
OUTPUT_FORMAT="png"  # Can be: png, svg, pdf
IMAGE_DIR="images"
TEMP_DIR="/tmp/mermaid-extract-$$"

# Function to print colored messages
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if mermaid-cli is installed
check_mermaid_cli() {
    if ! command -v mmdc &> /dev/null; then
        print_error "mermaid-cli (mmdc) is not installed!"
        echo ""
        echo "To install mermaid-cli, run:"
        echo "  npm install -g @mermaid-js/mermaid-cli"
        echo ""
        echo "Or using yarn:"
        echo "  yarn global add @mermaid-js/mermaid-cli"
        exit 1
    fi
    print_success "mermaid-cli is installed: $(mmdc --version)"
}

# Function to extract mermaid diagrams from a markdown file
extract_mermaid_diagrams() {
    local md_file="$1"
    local output_dir="$2"
    local diagram_count=0
    local in_mermaid=false
    local current_diagram=""
    local line_number=0
    
    print_info "Processing: $md_file"
    
    # Create temp directory for this file
    local file_temp_dir="$TEMP_DIR/$(basename "$md_file" .md)"
    mkdir -p "$file_temp_dir"
    
    # Read file line by line
    while IFS= read -r line || [ -n "$line" ]; do
        ((line_number++))
        
        # Check for start of mermaid block
        if [[ "$line" =~ ^\`\`\`mermaid ]]; then
            in_mermaid=true
            current_diagram=""
            continue
        fi
        
        # Check for end of code block
        if [[ "$line" =~ ^\`\`\` ]] && [ "$in_mermaid" = true ]; then
            in_mermaid=false
            ((diagram_count++))
            
            # Save diagram to temp file
            local diagram_file="$file_temp_dir/diagram-${diagram_count}.mmd"
            echo "$current_diagram" > "$diagram_file"
            
            # Convert to image
            local base_name="$(basename "$md_file" .md)-diagram-${diagram_count}"
            local output_file="$output_dir/${base_name}.${OUTPUT_FORMAT}"
            
            print_info "Converting diagram #${diagram_count} (line ${line_number})..."
            
            if mmdc -i "$diagram_file" -o "$output_file" -b transparent 2>/dev/null; then
                print_success "Created: $output_file"
                
                # Store mapping for later reference
                echo "$md_file|$line_number|$output_file" >> "$TEMP_DIR/mappings.txt"
            else
                print_error "Failed to convert diagram #${diagram_count}"
            fi
            
            current_diagram=""
            continue
        fi
        
        # Accumulate diagram content
        if [ "$in_mermaid" = true ]; then
            current_diagram="${current_diagram}${line}"$'\n'
        fi
    done < "$md_file"
    
    if [ $diagram_count -eq 0 ]; then
        print_warning "No Mermaid diagrams found in $md_file"
    else
        print_success "Extracted and converted $diagram_count diagram(s) from $md_file"
    fi
    
    return $diagram_count
}

# Function to process a single markdown file
process_markdown_file() {
    local md_file="$1"
    
    if [ ! -f "$md_file" ]; then
        print_error "File not found: $md_file"
        return 1
    fi
    
    # Create output directory relative to the markdown file
    local md_dir="$(dirname "$md_file")"
    local output_dir="$md_dir/$IMAGE_DIR"
    mkdir -p "$output_dir"
    
    extract_mermaid_diagrams "$md_file" "$output_dir"
}

# Function to process all markdown files in a directory
process_directory() {
    local dir="$1"
    local total_files=0
    local total_diagrams=0
    
    print_info "Searching for markdown files in: $dir"
    
    # Find all .md files recursively
    while IFS= read -r -d '' md_file; do
        ((total_files++))
        process_markdown_file "$md_file"
    done < <(find "$dir" -type f -name "*.md" -print0)
    
    if [ $total_files -eq 0 ]; then
        print_warning "No markdown files found in $dir"
    else
        print_success "Processed $total_files markdown file(s)"
    fi
}

# Function to generate a summary report
generate_report() {
    if [ ! -f "$TEMP_DIR/mappings.txt" ]; then
        print_warning "No diagrams were converted"
        return
    fi
    
    echo ""
    echo "=========================================="
    echo "         CONVERSION SUMMARY"
    echo "=========================================="
    echo ""
    
    local total_count=$(wc -l < "$TEMP_DIR/mappings.txt")
    print_success "Total diagrams converted: $total_count"
    echo ""
    
    echo "Diagram mappings:"
    echo "----------------"
    while IFS='|' read -r md_file line_num output_file; do
        echo "  • $(basename "$md_file") (line $line_num) → $(basename "$output_file")"
    done < "$TEMP_DIR/mappings.txt"
    
    echo ""
    echo "=========================================="
}

# Function to update markdown files with image references
update_markdown_with_images() {
    local update_mode="$1"
    
    if [ "$update_mode" != "update" ]; then
        return
    fi
    
    if [ ! -f "$TEMP_DIR/mappings.txt" ]; then
        return
    fi
    
    print_info "Updating markdown files with image references..."
    
    # Process each mapping
    while IFS='|' read -r md_file line_num output_file; do
        local image_path="images/$(basename "$output_file")"
        local temp_file="${md_file}.tmp"
        local current_line=0
        local in_mermaid=false
        local mermaid_start_line=0
        
        # Read and modify the markdown file
        while IFS= read -r line || [ -n "$line" ]; do
            ((current_line++))
            
            # Check for start of mermaid block
            if [[ "$line" =~ ^\`\`\`mermaid ]]; then
                in_mermaid=true
                mermaid_start_line=$current_line
                continue
            fi
            
            # Check for end of mermaid block
            if [[ "$line" =~ ^\`\`\` ]] && [ "$in_mermaid" = true ]; then
                in_mermaid=false
                
                # Check if this is the block we want to replace
                if [ $current_line -eq $line_num ]; then
                    # Replace with image reference
                    echo "" >> "$temp_file"
                    echo "![Diagram](${image_path})" >> "$temp_file"
                    echo "" >> "$temp_file"
                    print_success "Replaced Mermaid block at line $line_num with image reference"
                fi
                continue
            fi
            
            # Skip lines inside the mermaid block we're replacing
            if [ "$in_mermaid" = true ] && [ $mermaid_start_line -lt $line_num ] && [ $current_line -le $line_num ]; then
                continue
            fi
            
            # Write line to temp file if not in a block we're replacing
            if [ "$in_mermaid" = false ] || [ $mermaid_start_line -ge $line_num ]; then
                echo "$line" >> "$temp_file"
            fi
        done < "$md_file"
        
        # Replace original file with modified version
        mv "$temp_file" "$md_file"
        
    done < "$TEMP_DIR/mappings.txt"
    
    print_success "Markdown files updated with image references"
}

# Cleanup function
cleanup() {
    if [ -d "$TEMP_DIR" ]; then
        rm -rf "$TEMP_DIR"
    fi
}

# Main script
main() {
    echo ""
    echo "╔════════════════════════════════════════╗"
    echo "║  Mermaid Diagram to Image Converter   ║"
    echo "╚════════════════════════════════════════╝"
    echo ""
    
    # Check dependencies
    check_mermaid_cli
    
    # Create temp directory
    mkdir -p "$TEMP_DIR"
    
    # Set trap to cleanup on exit
    trap cleanup EXIT
    
    # Parse arguments
    local target="${1:-.}"
    local mode="${2:-convert}"  # convert or update
    
    if [ -f "$target" ]; then
        # Single file
        process_markdown_file "$target"
    elif [ -d "$target" ]; then
        # Directory
        process_directory "$target"
    else
        print_error "Invalid target: $target"
        echo ""
        echo "Usage: $0 [file.md|directory] [convert|update]"
        echo ""
        echo "Examples:"
        echo "  $0                                    # Process current directory"
        echo "  $0 lecture.md                         # Process single file"
        echo "  $0 ./lectures                         # Process directory"
        echo "  $0 lecture.md update                  # Convert and update markdown"
        exit 1
    fi
    
    # Update markdown files if requested
    update_markdown_with_images "$mode"
    
    # Generate report
    generate_report
    
    echo ""
    print_success "Conversion complete!"
    echo ""
}

# Run main function
main "$@"

# Made with Bob
