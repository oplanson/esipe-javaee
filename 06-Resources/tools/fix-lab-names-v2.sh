#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

################################################################################
# Script to Fix LAB_NAME Issues - Version 2
################################################################################

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo "=========================================="
echo "Fixing LAB_NAME Issues (v2)"
echo "=========================================="
echo ""

BASE_DIR="../../03-Labs"

fix_lab() {
    local lab_dir="$1"
    local lab_num="$2"
    local correct_name="$3"
    
    echo -e "${BLUE}Fixing $lab_dir...${NC}"
    
    local target_file="$BASE_DIR/$lab_dir/podman-test.sh"
    
    if [ -f "$target_file" ]; then
        # Direct replacement of the entire LAB_NAME line
        sed -i '' "36s/.*/LAB_NAME=\"$correct_name\"/" "$target_file"
        sed -i '' "37s/.*/LAB_NUMBER=\"$lab_num\"/" "$target_file"
        echo -e "${GREEN}  ✓ Fixed${NC}"
    else
        echo "  File not found: $target_file"
    fi
    echo ""
}

# Fix each lab with correct names
fix_lab "Lab03-JPA" "03" "Lab 03 - JPA & Database Integration"
fix_lab "Lab04-CDI" "04" "Lab 04 - CDI & Dependency Injection"
fix_lab "Lab04B-EJB" "04B" "Lab 04B - EJB Banking Services"
fix_lab "Lab05-REST" "05" "Lab 05 - JAX-RS RESTful Services"
fix_lab "Lab06-DDD" "06" "Lab 06 - Domain-Driven Design"
fix_lab "Lab07-Hexagonal" "07" "Lab 07 - Hexagonal Architecture"
fix_lab "Lab08-Microservices" "08" "Lab 08 - Microservices Architecture"
fix_lab "Lab09-Security" "09" "Lab 09 - Jakarta EE Security"

echo "=========================================="
echo -e "${GREEN}✓ All LAB_NAME issues fixed!${NC}"
echo "=========================================="

# Made with Bob
