#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

################################################################################
# Script to Test All Labs and Generate Report
################################################################################

set -e

GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "=========================================="
echo "Testing All Labs - Automated Report"
echo "=========================================="
echo ""

BASE_DIR="../../03-Labs"
REPORT_FILE="test-results-$(date +%Y%m%d-%H%M%S).txt"

# Arrays to track results
declare -a LABS
declare -a RESULTS
declare -a PASSED_TESTS
declare -a TOTAL_TESTS

test_lab() {
    local lab_dir="$1"
    local lab_name="$2"
    
    echo -e "${BLUE}Testing $lab_name...${NC}"
    
    cd "$BASE_DIR/$lab_dir"
    
    # Run test and capture output
    local output=$(./podman-test.sh 2>&1)
    local exit_code=$?
    
    # Extract test counts
    local passed=$(echo "$output" | grep "^Passed:" | awk '{print $2}')
    local total=$(echo "$output" | grep "^Total Tests:" | awk '{print $3}')
    
    # Store results
    LABS+=("$lab_name")
    PASSED_TESTS+=("$passed")
    TOTAL_TESTS+=("$total")
    
    if [ $exit_code -eq 0 ]; then
        RESULTS+=("PASS")
        echo -e "${GREEN}  ✓ $lab_name: $passed/$total tests passed${NC}"
    else
        RESULTS+=("FAIL")
        echo -e "${RED}  ✗ $lab_name: $passed/$total tests passed${NC}"
    fi
    
    # Save detailed output
    echo "========================================" >> "$REPORT_FILE"
    echo "$lab_name - $(date)" >> "$REPORT_FILE"
    echo "========================================" >> "$REPORT_FILE"
    echo "$output" >> "$REPORT_FILE"
    echo "" >> "$REPORT_FILE"
    
    cd - > /dev/null
    echo ""
}

# Test all labs
test_lab "Lab01-FirstServlet" "Lab 01 - First Servlet"
test_lab "Lab02-ServletsJSP" "Lab 02 - Servlets & JSP"
test_lab "Lab02B-JSF" "Lab 02B - JSF"

echo -e "${YELLOW}Note: Labs with databases (Lab03-Lab09) require docker-compose setup${NC}"
echo -e "${YELLOW}Skipping database labs for now. Run them individually if needed.${NC}"
echo ""

# Generate summary
echo "=========================================="
echo "Test Summary"
echo "=========================================="
echo ""

total_labs=${#LABS[@]}
passed_labs=0

for i in "${!LABS[@]}"; do
    if [ "${RESULTS[$i]}" = "PASS" ]; then
        echo -e "${GREEN}✓${NC} ${LABS[$i]}: ${PASSED_TESTS[$i]}/${TOTAL_TESTS[$i]} tests"
        ((passed_labs++))
    else
        echo -e "${RED}✗${NC} ${LABS[$i]}: ${PASSED_TESTS[$i]}/${TOTAL_TESTS[$i]} tests"
    fi
done

echo ""
echo "=========================================="
echo -e "Labs Tested: $total_labs"
echo -e "${GREEN}Passed: $passed_labs${NC}"
echo -e "${RED}Failed: $((total_labs - passed_labs))${NC}"
echo "=========================================="
echo ""
echo "Detailed report saved to: $REPORT_FILE"

# Made with Bob
