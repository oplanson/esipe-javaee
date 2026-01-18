#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

################################################################################
# Script to Apply Template v2.1 to Remaining Labs
#
# This script automates the application of podman-test-template.sh v2.1
# to all remaining labs that need it.
#
# Usage: bash apply-template-to-labs.sh
################################################################################

set -e

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo "=========================================="
echo "Applying Template v2.1 to Remaining Labs"
echo "=========================================="
echo ""

# Base directory
BASE_DIR="../../03-Labs"
TEMPLATE="./podman-test-template.sh"

# Lab configurations
declare -A LAB_CONFIGS=(
    ["Lab03-JPA"]="Lab 03 - JPA & Database Integration|banking-jpa-lab03|banking-jpa-lab03|lab03-postgres|banking-jpa.war|docker-compose"
    ["Lab04-CDI"]="Lab 04 - CDI & Dependency Injection|banking-cdi-lab04|banking-cdi-lab04|lab04-postgres|banking-cdi.war|docker-compose"
    ["Lab04B-EJB"]="Lab 04B - EJB Banking Services|banking-ejb-lab04b|banking-ejb-lab04b|lab04b-postgres|banking-ejb.war|docker-compose"
    ["Lab05-REST"]="Lab 05 - JAX-RS RESTful Services|banking-rest-lab05|banking-rest-lab05|lab05-postgres|banking-rest.war|docker-compose"
    ["Lab06-DDD"]="Lab 06 - Domain-Driven Design|banking-ddd-lab06|banking-ddd-lab06|lab06-postgres|banking-ddd.war|docker-compose"
    ["Lab07-Hexagonal"]="Lab 07 - Hexagonal Architecture|banking-hexagonal-lab07|banking-hexagonal-lab07|lab07-postgres|banking-hexagonal.war|docker-compose"
    ["Lab08-Microservices"]="Lab 08 - Microservices Architecture|banking-microservices-lab08|banking-microservices-lab08|lab08-postgres|banking-microservices.war|docker-compose"
    ["Lab09-Security"]="Lab 09 - Jakarta EE Security|bank-security:latest|bank-security-app|bank-security-db|bank-security.war|docker-compose"
)

# Function to apply template to a lab
apply_template() {
    local lab_dir="$1"
    local config="$2"
    
    IFS='|' read -r lab_name image_name container_name db_container war_name db_mode <<< "$config"
    
    echo -e "${BLUE}Processing $lab_dir...${NC}"
    
    local target_file="$BASE_DIR/$lab_dir/podman-test.sh"
    
    # Backup existing file if it exists
    if [ -f "$target_file" ]; then
        echo "  Backing up existing file..."
        cp "$target_file" "$target_file.backup-$(date +%Y%m%d-%H%M%S)"
    fi
    
    # Copy template
    echo "  Copying template..."
    cp "$TEMPLATE" "$target_file"
    
    # Update configuration variables
    echo "  Updating configuration..."
    sed -i '' "s|LAB_NAME=\"Lab XX - Description\"|LAB_NAME=\"$lab_name\"|" "$target_file"
    sed -i '' "s|LAB_NUMBER=\"XX\"|LAB_NUMBER=\"${lab_dir:3:2}\"|" "$target_file"
    sed -i '' "s|IMAGE_NAME=\"banking-app-labXX\"|IMAGE_NAME=\"$image_name\"|" "$target_file"
    sed -i '' "s|CONTAINER_NAME=\"banking-app-labXX\"|CONTAINER_NAME=\"$container_name\"|" "$target_file"
    sed -i '' "s|DB_MODE=\"none\"|DB_MODE=\"$db_mode\"|" "$target_file"
    sed -i '' "s|DB_CONTAINER=\"banking-db-labXX\"|DB_CONTAINER=\"$db_container\"|" "$target_file"
    sed -i '' "s|WAR_NAME=\"banking-app.war\"|WAR_NAME=\"$war_name\"|" "$target_file"
    
    # Make executable
    chmod +x "$target_file"
    
    echo -e "${GREEN}  ✓ $lab_dir completed${NC}"
    echo ""
}

# Apply template to each lab
for lab_dir in "${!LAB_CONFIGS[@]}"; do
    if [ -d "$BASE_DIR/$lab_dir" ]; then
        apply_template "$lab_dir" "${LAB_CONFIGS[$lab_dir]}"
    else
        echo -e "${YELLOW}⚠ Directory not found: $BASE_DIR/$lab_dir${NC}"
        echo ""
    fi
done

echo "=========================================="
echo -e "${GREEN}✓ Template application complete!${NC}"
echo "=========================================="
echo ""
echo "Next steps:"
echo "1. Review each lab's podman-test.sh"
echo "2. Add lab-specific tests in Phase 4"
echo "3. Test each lab individually"
echo "4. Run verify-all-labs.sh"
echo ""

# Made with Bob
