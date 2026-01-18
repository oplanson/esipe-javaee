#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

################################################################################
# Script to Add Missing Phase 5 Section
################################################################################

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo "=========================================="
echo "Adding Missing Phase 5 Sections"
echo "=========================================="
echo ""

BASE_DIR="../../03-Labs"

PHASE5_CONTENT='
    
    echo ""
    
    # Phase 5: Results and Cleanup
    print_test_summary
    
    # Final result
    if [ "$TESTS_FAILED" -eq 0 ]; then
        echo ""
        echo "╔═══════════════════════════════════════════════════════════════╗"
        printf "║  ✅ All %d tests passed successfully!%*s║\\n" "$TESTS_PASSED" $((37 - ${#TESTS_PASSED})) ""
        echo "╚═══════════════════════════════════════════════════════════════╝"
        echo ""
        
        # Open browser if index.html exists and all tests passed
        if unzip -l "target/$WAR_NAME" 2>/dev/null | grep -q "index.html"; then
            print_info "Opening browser..."
            if command -v open >/dev/null 2>&1; then
                # macOS
                open "http://localhost:${APP_PORT}/" 2>/dev/null || true
            elif command -v xdg-open >/dev/null 2>&1; then
                # Linux
                xdg-open "http://localhost:${APP_PORT}/" 2>/dev/null || true
            elif command -v start >/dev/null 2>&1; then
                # Windows
                start "http://localhost:${APP_PORT}/" 2>/dev/null || true
            else
                print_info "Could not detect browser command. Please open manually:"
                echo "  http://localhost:${APP_PORT}/"
            fi
        fi
        
        exit 0
    else
        echo ""
        echo "╔═══════════════════════════════════════════════════════════════╗"
        printf "║  ❌ %d test(s) failed!%*s║\\n" "$TESTS_FAILED" $((46 - ${#TESTS_FAILED})) ""
        echo "╚═══════════════════════════════════════════════════════════════╝"
        echo ""
        exit 1
    fi
}
'

fix_lab() {
    local lab_dir="$1"
    local file="$BASE_DIR/$lab_dir/podman-test.sh"
    
    echo -e "${BLUE}Checking $lab_dir...${NC}"
    
    if [ ! -f "$file" ]; then
        echo "  File not found"
        return
    fi
    
    # Check if Phase 5 is missing (file ends with main "$@" without Phase 5)
    if grep -q "# Phase 5: Results and Cleanup" "$file"; then
        echo "  Phase 5 already present"
        return
    fi
    
    # Find the line before "# Run main function"
    local insert_line=$(grep -n "^# Run main function" "$file" | cut -d: -f1)
    
    if [ -z "$insert_line" ]; then
        echo "  Could not find insertion point"
        return
    fi
    
    # Insert Phase 5 before "# Run main function"
    local temp_file=$(mktemp)
    head -n $((insert_line - 1)) "$file" > "$temp_file"
    echo "$PHASE5_CONTENT" >> "$temp_file"
    tail -n +$insert_line "$file" >> "$temp_file"
    mv "$temp_file" "$file"
    
    echo -e "${GREEN}  ✓ Phase 5 added${NC}"
}

# Fix labs that need Phase 5
fix_lab "Lab05-REST"
fix_lab "Lab06-DDD"
fix_lab "Lab07-Hexagonal"
fix_lab "Lab08-Microservices"
fix_lab "Lab09-Security"

echo ""
echo "=========================================="
echo -e "${GREEN}✓ Phase 5 sections added!${NC}"
echo "=========================================="

# Made with Bob
