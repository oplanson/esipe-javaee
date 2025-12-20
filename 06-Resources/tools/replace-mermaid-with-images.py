#!/usr/bin/env python3
"""
Script to replace Mermaid blocks with image references while keeping the original code in HTML comments.
This allows the diagrams to be updated later if needed.
"""

import sys
import re
from pathlib import Path

def replace_mermaid_blocks(md_file, image_mappings):
    """
    Replace Mermaid blocks with images while preserving the original code in comments.
    Only replaces if not already replaced (checks for <details> tag before mermaid block).
    
    Args:
        md_file: Path to the markdown file
        image_mappings: Dict mapping line numbers to image paths
    """
    with open(md_file, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    new_lines = []
    i = 0
    diagram_num = 0
    replaced_count = 0
    skipped_count = 0
    
    while i < len(lines):
        line = lines[i]
        
        # Check if this is a <details> tag (already replaced)
        if line.strip() == '<details>':
            # Check if next line is the summary for Mermaid code
            if i + 1 < len(lines) and '📝 Original Mermaid Code' in lines[i + 1]:
                # This is an already replaced block, skip to after the image
                new_lines.append(line)
                i += 1
                # Copy lines until we find the image reference
                while i < len(lines):
                    new_lines.append(lines[i])
                    if lines[i].strip().startswith('![width:') or lines[i].strip().startswith('![Diagram'):
                        diagram_num += 1
                        skipped_count += 1
                        i += 1
                        break
                    i += 1
                continue
        
        # Check if this is the start of a mermaid block (not yet replaced)
        if line.strip() == '```mermaid':
            diagram_num += 1
            mermaid_start = i
            mermaid_lines = [line]
            i += 1
            
            # Collect all lines in the mermaid block
            while i < len(lines) and lines[i].strip() != '```':
                mermaid_lines.append(lines[i])
                i += 1
            
            # Add the closing backticks
            if i < len(lines):
                mermaid_lines.append(lines[i])
            
            # Get the image path for this diagram
            # Use stem to get filename without extension
            base_name = Path(md_file).stem
            image_path = f"images/{base_name}-diagram-{diagram_num}.png"
            
            # Write the mermaid block in a collapsible HTML details section
            # This avoids issues with --> in comments and keeps code accessible
            new_lines.append('<details>\n')
            new_lines.append('<summary>📝 Original Mermaid Code (click to expand)</summary>\n')
            new_lines.append('\n')
            for mermaid_line in mermaid_lines:
                new_lines.append(mermaid_line)
            new_lines.append('\n')
            new_lines.append('</details>\n')
            new_lines.append('\n')
            
            # Write the image reference with size constraint to prevent overflow
            # Using width:70% to ensure diagrams fit within slide boundaries
            new_lines.append(f'![width:70%](images/{base_name}-diagram-{diagram_num}.png)\n')
            new_lines.append('\n')
            
            replaced_count += 1
            i += 1
        else:
            new_lines.append(line)
            i += 1
    
    # Write the modified content back
    with open(md_file, 'w', encoding='utf-8') as f:
        f.writelines(new_lines)
    
    print(f"✓ Processed {diagram_num} Mermaid diagram(s) in {md_file}")
    if replaced_count > 0:
        print(f"  Replaced: {replaced_count} new block(s)")
    if skipped_count > 0:
        print(f"  Skipped: {skipped_count} already replaced block(s)")
    if replaced_count > 0:
        print(f"  Original Mermaid code preserved in <details> tags")

def main():
    if len(sys.argv) < 2:
        print("Usage: replace-mermaid-with-images.py <markdown-file>")
        sys.exit(1)
    
    md_file = sys.argv[1]
    
    if not Path(md_file).exists():
        print(f"Error: File not found: {md_file}")
        sys.exit(1)
    
    # For now, we don't need the mappings file since we can infer the image names
    replace_mermaid_blocks(md_file, {})

if __name__ == '__main__':
    main()

# Made with Bob
