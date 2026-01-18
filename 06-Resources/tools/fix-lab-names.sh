#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

################################################################################
# Script to Fix LAB_NAME Duplication Issue
################################################################################

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo "=========================================="
echo "Fixing LAB_NAME Duplication"
echo "=========================================="
echo ""

BASE_DIR="../../03-Labs"

fix_lab() {
    local lab_dir="$1"
    local correct_name="$2"
    
    echo -e "${BLUE}Fixing $lab_dir...${NC}"
    
    local target_file="$BASE_DIR/$lab_dir/podman-test.sh"
    
    if [ -f "$target_file" ]; then
        # Fix the LAB_NAME line by removing the duplicate pattern
        sed -i '' 's/LAB_NAME="Lab [0-9A-Z]* - [^"]*LAB_NAME="Lab XX - Description" /LAB_NAME="Lab /' "$target_file"
        sed -i '' "s|LAB_NAME=\"Lab |LAB_NAME=\"$correct_name|" "$target_file"
        echo -e "${GREEN}  ✓ Fixed${NC}"
    else
        echo "  File not found: $target_file"
    fi
    echo ""
}

# Fix each lab
fix_lab "Lab03-JPA" "Lab 03 - JPA & Database Integration"
fix_lab "Lab04-CDI" "Lab 04 - CDI & Dependency Injection"
fix_lab "Lab04B-EJB" "Lab 04B - EJB Banking Services"
fix_lab "Lab05-REST" "Lab 05 - JAX-RS RESTful Services"
fix_lab "Lab06-DDD" "Lab 06 - Domain-Driven Design"
fix_lab "Lab07-Hexagonal" "Lab 07 - Hexagonal Architecture"
fix_lab "Lab08-Microservices" "Lab 08 - Microservices Architecture"
fix_lab "Lab09-Security" "Lab 09 - Jakarta EE Security"

echo "=========================================="
echo -e "${GREEN}✓ All LAB_NAME issues fixed!${NC}"
echo "=========================================="

# Made with Bob
