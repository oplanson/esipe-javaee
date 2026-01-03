#!/bin/bash

# © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Lab08 - Microservices Architecture - Build Verification Script
# This script verifies that all microservices can be built successfully

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SOLUTION_DIR="${SCRIPT_DIR}/solution"
CLIENT_SERVICE_DIR="${SOLUTION_DIR}/client-service"
ACCOUNT_SERVICE_DIR="${SOLUTION_DIR}/account-service"
API_GATEWAY_DIR="${SOLUTION_DIR}/api-gateway"

# Test results
TESTS_PASSED=0
TESTS_FAILED=0
FAILED_TESTS=()

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

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to record test result
record_test() {
    local test_name=$1
    local result=$2
    
    if [ "$result" -eq 0 ]; then
        TESTS_PASSED=$((TESTS_PASSED + 1))
        print_success "✓ ${test_name}"
    else
        TESTS_FAILED=$((TESTS_FAILED + 1))
        FAILED_TESTS+=("${test_name}")
        print_error "✗ ${test_name}"
    fi
}

# Function to check prerequisites
check_prerequisites() {
    print_info "Checking prerequisites..."
    
    if ! command_exists mvn; then
        print_error "Maven is not installed"
        exit 1
    fi
    
    print_success "Maven found: $(mvn -version | head -n 1)"
}

# Function to verify directory structure
verify_structure() {
    print_info "Verifying directory structure..."
    
    local dirs=(
        "${CLIENT_SERVICE_DIR}"
        "${ACCOUNT_SERVICE_DIR}"
        "${API_GATEWAY_DIR}"
    )
    
    for dir in "${dirs[@]}"; do
        if [ -d "$dir" ]; then
            print_success "Found: $(basename "$dir")"
        else
            print_error "Missing: $(basename "$dir")"
            return 1
        fi
    done
    
    return 0
}

# Function to verify pom.xml files
verify_pom_files() {
    print_info "Verifying pom.xml files..."
    
    local services=(
        "client-service"
        "account-service"
        "api-gateway"
    )
    
    for service in "${services[@]}"; do
        local pom="${SOLUTION_DIR}/${service}/pom.xml"
        if [ -f "$pom" ]; then
            print_success "Found: ${service}/pom.xml"
        else
            print_error "Missing: ${service}/pom.xml"
            return 1
        fi
    done
    
    return 0
}

# Function to build a service
build_service() {
    local service_name=$1
    local service_dir=$2
    
    print_info "Building ${service_name}..."
    
    if [ ! -d "${service_dir}" ]; then
        print_error "Service directory not found: ${service_dir}"
        return 1
    fi
    
    cd "${service_dir}"
    
    # Clean and compile
    if mvn clean compile > /dev/null 2>&1; then
        print_success "${service_name} compiled successfully"
        return 0
    else
        print_error "${service_name} compilation failed"
        return 1
    fi
}

# Function to package a service
package_service() {
    local service_name=$1
    local service_dir=$2
    
    print_info "Packaging ${service_name}..."
    
    if [ ! -d "${service_dir}" ]; then
        print_error "Service directory not found: ${service_dir}"
        return 1
    fi
    
    cd "${service_dir}"
    
    # Package
    if mvn package -DskipTests > /dev/null 2>&1; then
        print_success "${service_name} packaged successfully"
        
        # Verify WAR file
        local war_file=$(find target -name "*.war" | head -n 1)
        if [ -n "$war_file" ]; then
            local war_size=$(du -h "$war_file" | cut -f1)
            print_info "WAR file: $(basename "$war_file") (${war_size})"
            return 0
        else
            print_error "WAR file not found"
            return 1
        fi
    else
        print_error "${service_name} packaging failed"
        return 1
    fi
}

# Function to verify server.xml
verify_server_config() {
    print_info "Verifying server.xml configurations..."
    
    local services=(
        "client-service"
        "account-service"
        "api-gateway"
    )
    
    for service in "${services[@]}"; do
        local server_xml="${SOLUTION_DIR}/${service}/src/main/liberty/config/server.xml"
        if [ -f "$server_xml" ]; then
            print_success "Found: ${service}/server.xml"
        else
            print_error "Missing: ${service}/server.xml"
            return 1
        fi
    done
    
    return 0
}

# Function to verify persistence.xml
verify_persistence_config() {
    print_info "Verifying persistence.xml configurations..."
    
    local services=(
        "client-service"
        "account-service"
    )
    
    for service in "${services[@]}"; do
        local persistence_xml="${SOLUTION_DIR}/${service}/src/main/resources/META-INF/persistence.xml"
        if [ -f "$persistence_xml" ]; then
            print_success "Found: ${service}/persistence.xml"
        else
            print_error "Missing: ${service}/persistence.xml"
            return 1
        fi
    done
    
    return 0
}

# Function to verify Flyway migrations
verify_migrations() {
    print_info "Verifying Flyway migrations..."
    
    local services=(
        "client-service"
        "account-service"
    )
    
    for service in "${services[@]}"; do
        local migration_dir="${SOLUTION_DIR}/${service}/src/main/resources/db/migration"
        if [ -d "$migration_dir" ]; then
            local migration_count=$(find "$migration_dir" -name "V*.sql" | wc -l)
            print_success "Found: ${service} migrations (${migration_count} files)"
        else
            print_error "Missing: ${service}/db/migration"
            return 1
        fi
    done
    
    return 0
}

# Function to display summary
display_summary() {
    echo ""
    echo "========================================"
    echo "         BUILD VERIFICATION SUMMARY     "
    echo "========================================"
    echo ""
    echo "Tests Passed: ${TESTS_PASSED}"
    echo "Tests Failed: ${TESTS_FAILED}"
    echo ""
    
    if [ ${TESTS_FAILED} -gt 0 ]; then
        echo "Failed Tests:"
        for test in "${FAILED_TESTS[@]}"; do
            echo "  - ${test}"
        done
        echo ""
        print_error "Build verification FAILED"
        return 1
    else
        print_success "All build verification tests PASSED"
        return 0
    fi
}

# Main execution
main() {
    print_info "=== Lab08 - Microservices Architecture - Build Verification ==="
    echo ""
    
    # Check prerequisites
    check_prerequisites
    
    # Verify structure
    verify_structure
    record_test "Directory Structure" $?
    
    # Verify pom.xml files
    verify_pom_files
    record_test "POM Files" $?
    
    # Verify server.xml files
    verify_server_config
    record_test "Server Configuration" $?
    
    # Verify persistence.xml files
    verify_persistence_config
    record_test "Persistence Configuration" $?
    
    # Verify Flyway migrations
    verify_migrations
    record_test "Flyway Migrations" $?
    
    echo ""
    print_info "Building services..."
    echo ""
    
    # Build Client Service
    build_service "Client Service" "${CLIENT_SERVICE_DIR}"
    record_test "Client Service Build" $?
    
    # Build Account Service
    build_service "Account Service" "${ACCOUNT_SERVICE_DIR}"
    record_test "Account Service Build" $?
    
    # Build API Gateway
    build_service "API Gateway" "${API_GATEWAY_DIR}"
    record_test "API Gateway Build" $?
    
    echo ""
    print_info "Packaging services..."
    echo ""
    
    # Package Client Service
    package_service "Client Service" "${CLIENT_SERVICE_DIR}"
    record_test "Client Service Package" $?
    
    # Package Account Service
    package_service "Account Service" "${ACCOUNT_SERVICE_DIR}"
    record_test "Account Service Package" $?
    
    # Package API Gateway
    package_service "API Gateway" "${API_GATEWAY_DIR}"
    record_test "API Gateway Package" $?
    
    # Display summary
    display_summary
    exit $?
}

# Run main function
main

# Made with Bob
