#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

################################################################################
# Script to Apply Template v2.1 to Remaining Labs (Simple Version)
################################################################################

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo "=========================================="
echo "Applying Template v2.1 to Remaining Labs"
echo "=========================================="
echo ""

BASE_DIR="../../03-Labs"
TEMPLATE="./podman-test-template.sh"

apply_template() {
    local lab_dir="$1"
    local lab_name="$2"
    local lab_num="$3"
    local image_name="$4"
    local container_name="$5"
    local db_container="$6"
    local war_name="$7"
    
    echo -e "${BLUE}Processing $lab_dir...${NC}"
    
    local target_file="$BASE_DIR/$lab_dir/podman-test.sh"
    
    if [ -f "$target_file" ]; then
        echo "  Backing up existing file..."
        cp "$target_file" "$target_file.backup-$(date +%Y%m%d-%H%M%S)"
    fi
    
    echo "  Copying template..."
    cp "$TEMPLATE" "$target_file"
    
    echo "  Updating configuration..."
    sed -i '' "s|LAB_NAME=\"Lab XX - Description\"|LAB_NAME=\"$lab_name\"|" "$target_file"
    sed -i '' "s|LAB_NUMBER=\"XX\"|LAB_NUMBER=\"$lab_num\"|" "$target_file"
    sed -i '' "s|IMAGE_NAME=\"banking-app-labXX\"|IMAGE_NAME=\"$image_name\"|" "$target_file"
    sed -i '' "s|CONTAINER_NAME=\"banking-app-labXX\"|CONTAINER_NAME=\"$container_name\"|" "$target_file"
    sed -i '' "s|DB_MODE=\"none\"|DB_MODE=\"docker-compose\"|" "$target_file"
    sed -i '' "s|DB_CONTAINER=\"banking-db-labXX\"|DB_CONTAINER=\"$db_container\"|" "$target_file"
    sed -i '' "s|WAR_NAME=\"banking-app.war\"|WAR_NAME=\"$war_name\"|" "$target_file"
    
    chmod +x "$target_file"
    
    echo -e "${GREEN}  ✓ $lab_dir completed${NC}"
    echo ""
}

# Apply to each lab
apply_template "Lab03-JPA" "Lab 03 - JPA & Database Integration" "03" "banking-jpa-lab03" "banking-jpa-lab03" "lab03-postgres" "banking-jpa.war"
apply_template "Lab04-CDI" "Lab 04 - CDI & Dependency Injection" "04" "banking-cdi-lab04" "banking-cdi-lab04" "lab04-postgres" "banking-cdi.war"
apply_template "Lab04B-EJB" "Lab 04B - EJB Banking Services" "04B" "banking-ejb-lab04b" "banking-ejb-lab04b" "lab04b-postgres" "banking-ejb.war"
apply_template "Lab05-REST" "Lab 05 - JAX-RS RESTful Services" "05" "banking-rest-lab05" "banking-rest-lab05" "lab05-postgres" "banking-rest.war"
apply_template "Lab06-DDD" "Lab 06 - Domain-Driven Design" "06" "banking-ddd-lab06" "banking-ddd-lab06" "lab06-postgres" "banking-ddd.war"
apply_template "Lab07-Hexagonal" "Lab 07 - Hexagonal Architecture" "07" "banking-hexagonal-lab07" "banking-hexagonal-lab07" "lab07-postgres" "banking-hexagonal.war"
apply_template "Lab08-Microservices" "Lab 08 - Microservices Architecture" "08" "banking-microservices-lab08" "banking-microservices-lab08" "lab08-postgres" "banking-microservices.war"
apply_template "Lab09-Security" "Lab 09 - Jakarta EE Security" "09" "bank-security:latest" "bank-security-app" "bank-security-db" "bank-security.war"

echo "=========================================="
echo -e "${GREEN}✓ Template application complete!${NC}"
echo "=========================================="
echo ""
echo "Next steps:"
echo "1. Review each lab's podman-test.sh"
echo "2. Add lab-specific tests in Phase 4"
echo "3. Test each lab individually"
echo ""

# Made with Bob
