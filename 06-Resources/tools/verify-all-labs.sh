#!/usr/bin/env bash

# © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

################################################################################
# Script: verify-all-labs.sh
# Description: Execute all lab podman-test.sh scripts and report results
# Author: Olivier Planson
# Date: 2026-01-01
################################################################################

# Don't use set -e here as we want to continue testing all labs even if one fails
# Don't use set -u either as it conflicts with empty arrays in Bash 3.2
set +u

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
LABS_DIR="$PROJECT_ROOT/03-Labs"

# Results tracking (Bash 3.2 compatible - using indexed arrays)
lab_names=()
lab_results=()
lab_durations=()
total_labs=0
passed_labs=0
failed_labs=0

# Helper functions for array management
get_lab_index() {
    local lab_name=$1
    local i=0
    # Check if array has elements before iterating
    if [ ${#lab_names[@]} -gt 0 ]; then
        for name in "${lab_names[@]}"; do
            if [ "$name" = "$lab_name" ]; then
                echo $i
                return 0
            fi
            i=$((i + 1))
        done
    fi
    echo -1
}

set_lab_result() {
    local lab_name=$1
    local result=$2
    local duration=${3:-"N/A"}
    
    local idx=$(get_lab_index "$lab_name")
    if [ "$idx" -eq -1 ]; then
        lab_names+=("$lab_name")
        lab_results+=("$result")
        lab_durations+=("$duration")
    else
        lab_results[$idx]="$result"
        lab_durations[$idx]="$duration"
    fi
}

get_lab_result() {
    local lab_name=$1
    local idx=$(get_lab_index "$lab_name")
    if [ "$idx" -ge 0 ]; then
        echo "${lab_results[$idx]}"
    fi
}

get_lab_duration() {
    local lab_name=$1
    local idx=$(get_lab_index "$lab_name")
    if [ "$idx" -ge 0 ]; then
        echo "${lab_durations[$idx]}"
    fi
}

# Function to print section header
print_header() {
    echo ""
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}  $1${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
    echo ""
}

# Function to print lab header
print_lab_header() {
    echo ""
    echo -e "${YELLOW}───────────────────────────────────────────────────────────────${NC}"
    echo -e "${YELLOW}  Testing: $1${NC}"
    echo -e "${YELLOW}───────────────────────────────────────────────────────────────${NC}"
}

# Function to run a lab test
run_lab_test() {
    local lab_name=$1
    local lab_path=$2
    local test_script="$lab_path/podman-test.sh"
    
    print_lab_header "$lab_name"
    
    echo -e "${BLUE}Lab Path:${NC} $lab_path"
    echo -e "${BLUE}Test Script:${NC} $test_script"
    echo ""
    
    # Check if test script exists
    if [ ! -f "$test_script" ]; then
        echo -e "${YELLOW}⚠️  No podman-test.sh found for $lab_name${NC}"
        set_lab_result "$lab_name" "SKIPPED"
        ((total_labs++))
        return
    fi
    
    echo -e "${BLUE}✓ Test script found${NC}"
    
    # Make script executable
    chmod +x "$test_script"
    echo -e "${BLUE}✓ Script made executable${NC}"
    
    # Run the test and capture output
    local start_time=$(date +%s)
    local output_file=$(mktemp)
    
    echo ""
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}Starting test execution for $lab_name...${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
    
    # Change to lab directory and run test (don't exit on error)
    local test_result=0
    (cd "$lab_path" && bash podman-test.sh > "$output_file" 2>&1) || test_result=$?
    
    local end_time=$(date +%s)
    local duration=$((end_time - start_time))
    
    echo ""
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}Test execution completed for $lab_name${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
    
    if [ $test_result -eq 0 ]; then
        echo -e "${GREEN}✅ $lab_name: PASSED${NC} (${duration}s)"
        set_lab_result "$lab_name" "PASSED" "$duration"
        passed_labs=$((passed_labs + 1))
        
        # Show summary from output
        if grep -q "All tests passed" "$output_file"; then
            local test_count=$(grep -o "All [0-9]* tests passed" "$output_file" | grep -o "[0-9]*" | head -1)
            if [ -n "$test_count" ]; then
                echo -e "   ${GREEN}All $test_count tests passed${NC}"
            fi
        fi
    else
        echo -e "${RED}❌ $lab_name: FAILED${NC} (${duration}s, exit code: $test_result)"
        set_lab_result "$lab_name" "FAILED" "$duration"
        failed_labs=$((failed_labs + 1))
        
        # Show error details
        echo ""
        echo -e "${RED}Error output (last 30 lines):${NC}"
        echo -e "${RED}─────────────────────────────────────────────────────────────${NC}"
        tail -30 "$output_file" | sed 's/^/   /'
        echo -e "${RED}─────────────────────────────────────────────────────────────${NC}"
    fi
    
    rm -f "$output_file"
    total_labs=$((total_labs + 1))
    
    echo ""
    echo -e "${BLUE}Progress: $total_labs/$lab_count labs tested${NC}"
    echo ""
}

# Main execution
main() {
    print_header "Jakarta EE Labs Verification Suite"
    
    echo -e "${BLUE}Project Root:${NC} $PROJECT_ROOT"
    echo -e "${BLUE}Labs Directory:${NC} $LABS_DIR"
    echo -e "${BLUE}Start Time:${NC} $(date '+%Y-%m-%d %H:%M:%S')"
    echo ""
    
    # Check if labs directory exists
    if [ ! -d "$LABS_DIR" ]; then
        echo -e "${RED}Error: Labs directory not found: $LABS_DIR${NC}"
        exit 1
    fi
    
    # Find all labs with podman-test.sh automatically
    echo -e "${BLUE}Scanning for labs...${NC}"
    
    # Find all directories in Labs directory that start with "Lab"
    # Sort them naturally (Lab01, Lab02, ..., Lab10, Lab11, etc.)
    lab_dirs=$(find "$LABS_DIR" -maxdepth 1 -type d -name "Lab*" | sort -V)
    
    if [ -z "$lab_dirs" ]; then
        echo -e "${YELLOW}⚠️  No lab directories found in $LABS_DIR${NC}"
        exit 0
    fi
    
    # Count discovered labs
    lab_count=$(echo "$lab_dirs" | wc -l | tr -d ' ')
    echo -e "${BLUE}Found $lab_count lab(s)${NC}"
    echo ""
    
    # Process each lab directory
    echo -e "${BLUE}Starting lab tests...${NC}"
    echo ""
    
    local lab_number=0
    while IFS= read -r lab_path; do
        lab_number=$((lab_number + 1))
        
        # Extract lab name from path
        lab_name=$(basename "$lab_path")
        
        echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
        echo -e "${BLUE}Processing lab $lab_number of $lab_count: $lab_name${NC}"
        echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
        echo ""
        
        # Run the test for this lab
        run_lab_test "$lab_name" "$lab_path"
        
        # Add separator between labs
        if [ $lab_number -lt $lab_count ]; then
            echo ""
            echo -e "${YELLOW}───────────────────────────────────────────────────────────────${NC}"
            echo -e "${YELLOW}Moving to next lab...${NC}"
            echo -e "${YELLOW}───────────────────────────────────────────────────────────────${NC}"
            echo ""
            sleep 2
        fi
    done <<< "$lab_dirs"
    
    # Print final summary
    print_header "Test Results Summary"
    
    echo -e "${BLUE}Total Labs Tested:${NC} $total_labs"
    echo -e "${GREEN}Passed:${NC} $passed_labs"
    echo -e "${RED}Failed:${NC} $failed_labs"
    echo ""
    
    # Detailed results table
    echo -e "${BLUE}Detailed Results:${NC}"
    echo "┌─────────────────────────┬──────────┬──────────┐"
    echo "│ Lab                     │ Status   │ Duration │"
    echo "├─────────────────────────┼──────────┼──────────┤"
    
    # Sort lab names and iterate (only if we have results)
    if [ ${#lab_names[@]} -gt 0 ]; then
        IFS=$'\n' sorted_labs=($(printf '%s\n' "${lab_names[@]}" | sort))
        unset IFS
        
        for lab in "${sorted_labs[@]}"; do
        local status=$(get_lab_result "$lab")
        local duration=$(get_lab_duration "$lab")
        
        # Format status with color
        case $status in
            "PASSED")
                status_colored="${GREEN}✅ PASSED${NC}"
                ;;
            "FAILED")
                status_colored="${RED}❌ FAILED${NC}"
                ;;
            "SKIPPED")
                status_colored="${YELLOW}⚠️  SKIPPED${NC}"
                ;;
        esac
        
        # Format duration
        if [ "$duration" != "N/A" ]; then
            duration="${duration}s"
        fi
        
            printf "│ %-23s │ %-8s │ %8s │\n" "$lab" "$status_colored" "$duration"
        done
    else
        printf "│ %-23s │ %-8s │ %8s │\n" "No labs tested" "N/A" "N/A"
    fi
    
    echo "└─────────────────────────┴──────────┴──────────┘"
    echo ""
    
    # Calculate total duration
    local total_duration=0
    if [ ${#lab_durations[@]} -gt 0 ]; then
        for duration in "${lab_durations[@]}"; do
            if [ "$duration" != "N/A" ]; then
                total_duration=$((total_duration + duration))
            fi
        done
    fi
    
    echo -e "${BLUE}Total Duration:${NC} ${total_duration}s"
    echo -e "${BLUE}End Time:${NC} $(date '+%Y-%m-%d %H:%M:%S')"
    
    # Exit with appropriate code
    if [ $failed_labs -eq 0 ]; then
        echo ""
        echo -e "${GREEN}╔═══════════════════════════════════════════════════════════════╗${NC}"
        echo -e "${GREEN}║  🎉 All labs passed successfully!                            ║${NC}"
        echo -e "${GREEN}╚═══════════════════════════════════════════════════════════════╝${NC}"
        exit 0
    else
        echo ""
        echo -e "${RED}╔═══════════════════════════════════════════════════════════════╗${NC}"
        echo -e "${RED}║  ⚠️  Some labs failed. Please review the errors above.       ║${NC}"
        echo -e "${RED}╚═══════════════════════════════════════════════════════════════╝${NC}"
        exit 1
    fi
}

# Run main function
main "$@"

# Made with Bob
