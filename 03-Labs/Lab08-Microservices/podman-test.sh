#!/usr/bin/env bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Strict mode for better error handling
set -o pipefail
set -o nounset

################################################################################
# TEMPLATE: Unified Podman Test Script for Jakarta EE Labs
# 
# This template provides a standardized structure for testing labs with:
# - Complete environment cleanup
# - Maven build verification
# - Container deployment
# - Comprehensive test execution
# - Detailed result reporting
#
# Usage:
#   ./podman-test.sh              # Test solution/ directory (default)
#   ./podman-test.sh -dir starter # Test starter/ directory
#   ./podman-test.sh -h           # Show help
#
# Exit Codes:
#   0 - All tests passed
#   1 - One or more tests failed
################################################################################

# Note: NOT using 'set -e' so all tests run even if some fail
# This allows complete test reporting

################################################################################
# CONFIGURATION SECTION - CUSTOMIZE FOR EACH LAB
################################################################################

# Lab identification
LAB_NAME="Lab 08 - Microservices Architecture"
LAB_NUMBER="08"

# Container configuration - API Gateway is the main application
IMAGE_NAME="banking-api-gateway-lab08"
CONTAINER_NAME="banking-api-gateway-lab08"
APP_PORT=9080

# Database deployment mode (choose one):
# - "none"           : No database (simple app)
# - "docker-compose" : Use docker-compose.yml for PostgreSQL
DB_MODE="docker-compose"

# Database configuration (only if DB_MODE = "docker-compose")
# Lab08 has 2 databases: client-db and account-db
DB_CONTAINER="banking-client-db"
DB_PORT=5433
DB_USER="bankuser"
DB_PASSWORD="bankpass"
DB_NAME="banking_client_db"

# Build configuration
BUILD_DIR="solution"  # Default directory to build
WAR_NAME="banking-microservices.war"

# Timeouts (in seconds)
DB_READY_TIMEOUT=30
APP_READY_TIMEOUT=60
HEALTH_CHECK_INTERVAL=2

################################################################################
# COLOR DEFINITIONS
################################################################################

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

################################################################################
# TEST TRACKING VARIABLES
################################################################################

TESTS_PASSED=0
TESTS_FAILED=0
TEST_NUMBER=0
declare -a TEST_RESULTS
declare -a TEST_NAMES
declare -a FAILED_COMMANDS

################################################################################
# UTILITY FUNCTIONS
################################################################################

# Print colored messages
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ $1${NC}"
}

print_step() {
    echo -e "${CYAN}▶ $1${NC}"
}

print_header() {
    echo ""
    echo "=========================================="
    echo "$1"
    echo "=========================================="
    echo ""
}

# Show usage information
show_usage() {
    cat << EOF
Usage: $0 [OPTIONS]

Test the $LAB_NAME application using Podman.

OPTIONS:
    -dir, --directory PATH    Directory to build and test (default: solution)
    -h, --help               Show this help message

EXAMPLES:
    $0                       # Test solution/ directory
    $0 -dir starter          # Test starter/ directory
    $0 -dir /path/to/code    # Test custom directory

EXIT CODES:
    0    All tests passed
    1    One or more tests failed

EOF
}

# Parse command line arguments
parse_arguments() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            -dir|--directory)
                if [[ -z "${2:-}" ]]; then
                    print_error "Option -dir requires an argument"
                    show_usage
                    exit 1
                fi
                BUILD_DIR="$2"
                shift 2
                ;;
            -h|--help)
                show_usage
                exit 0
                ;;
            *)
                print_error "Unknown option: $1"
                show_usage
                exit 1
                ;;
        esac
    done
}

################################################################################
# CLEANUP FUNCTIONS
################################################################################

# Complete environment cleanup
cleanup_environment() {
    local container_name="$1"
    local db_container="${2:-}"
    local image_name="$3"
    local db_mode="${4:-none}"
    
    print_info "Cleaning up Lab08 microservices environment (DB mode: $db_mode)..."
    
    # Stop and remove all Lab08 microservice containers
    local lab08_containers=("banking-client-service-lab08" "banking-account-service-lab08" "banking-api-gateway-lab08")
    
    for container in "${lab08_containers[@]}"; do
        if podman ps -a --format "{{.Names}}" 2>/dev/null | grep -q "^${container}$"; then
            print_info "Stopping $container..."
            podman stop "${container}" 2>/dev/null || true
            podman rm -f "${container}" 2>/dev/null || true
            print_success "$container removed"
        fi
    done
    
    # Database cleanup (docker-compose mode only)
    if [ "$db_mode" = "docker-compose" ]; then
        if [ -f "${BUILD_DIR}/docker-compose.yml" ]; then
            print_info "Stopping docker-compose services..."
            (cd "${BUILD_DIR}" && docker-compose down -v 2>/dev/null) || true
            print_success "Docker-compose services stopped"
        fi
    fi
    
    # Remove network if exists
    local network_name="lab08-network"
    if podman network exists "$network_name" 2>/dev/null; then
        print_info "Removing network $network_name..."
        podman network rm "$network_name" 2>/dev/null || true
        print_success "Network removed"
    fi
    
    # Remove all Lab08 images
    local lab08_images=("banking-client-service-lab08" "banking-account-service-lab08" "banking-api-gateway-lab08")
    
    for image in "${lab08_images[@]}"; do
        if podman image exists "${image}" 2>/dev/null; then
            print_info "Removing image $image..."
            podman rmi -f "${image}" 2>/dev/null || true
        fi
    done
    print_success "All Lab08 images removed"
    
    # Check for port conflicts on all microservice ports
    local microservice_ports=("9080" "9081" "9082")
    
    for port in "${microservice_ports[@]}"; do
        check_port_conflicts "$port" ""
    done
    
    # Prune dangling images and volumes
    print_info "Pruning dangling resources..."
    podman image prune -f 2>/dev/null || true
    podman volume prune -f 2>/dev/null || true
    
    print_success "Environment cleanup complete"
}

# Check and resolve port conflicts
check_port_conflicts() {
    local port="$1"
    local exclude_container="${2:-}"
    
    print_info "Checking for port conflicts on $port..."
    
    local conflicting
    conflicting=$(podman ps --format "{{.Names}}" 2>/dev/null | while IFS= read -r name; do
        if [ -n "$name" ] && podman port "$name" 2>/dev/null | grep -q "0.0.0.0:$port"; then
            if [ "$name" != "$exclude_container" ]; then
                echo "$name"
            fi
        fi
    done)
    
    if [ -n "$conflicting" ]; then
        print_warning "Found containers using port $port:"
        while IFS= read -r container; do
            if [ -n "$container" ]; then
                print_warning "  Stopping $container..."
                podman stop "$container" 2>/dev/null || true
                podman rm -f "$container" 2>/dev/null || true
                print_success "  ✓ $container removed"
            fi
        done <<< "$conflicting"
    else
        print_success "No port conflicts detected"
    fi
}

################################################################################
# SERVICE MANAGEMENT FUNCTIONS
################################################################################

# Wait for a service to be ready
wait_for_service() {
    local service_name="$1"
    local health_check_cmd="$2"
    local max_wait="${3:-60}"
    local wait_interval="${4:-2}"
    
    print_info "Waiting for $service_name to be ready..."
    
    local elapsed=0
    while [ "$elapsed" -lt "$max_wait" ]; do
        if bash -c "$health_check_cmd" >/dev/null 2>&1; then
            print_success "$service_name is ready! (${elapsed}s)"
            return 0
        fi
        echo -n "."
        sleep "$wait_interval"
        elapsed=$((elapsed + wait_interval))
    done
    
    echo ""
    print_error "$service_name failed to start within ${max_wait}s"
    return 1
}

################################################################################
# TEST EXECUTION FUNCTIONS
################################################################################

# Run a single test with tracking
run_test() {
    local test_name="$1"
    local test_command="$2"
    
    ((TEST_NUMBER++))
    TEST_NAMES[$TEST_NUMBER]="$test_name"
    
    echo -n "Test $TEST_NUMBER: $test_name... "
    
    if eval "$test_command" >/dev/null 2>&1; then
        echo -e "${GREEN}✓ PASSED${NC}"
        TEST_RESULTS[$TEST_NUMBER]="PASSED"
        ((TESTS_PASSED++))
        return 0
    else
        echo -e "${RED}✗ FAILED${NC}"
        TEST_RESULTS[$TEST_NUMBER]="FAILED"
        FAILED_COMMANDS[$TEST_NUMBER]="$test_command"
        ((TESTS_FAILED++))
        return 1
    fi
}

# Test web interface accessibility and copyright
test_web_interface() {
    local war_file="$1"
    
    # Check if index.html exists in WAR
    if unzip -l "$war_file" 2>/dev/null | grep -q "index.html"; then
        print_info "Web interface detected in WAR, testing..."
        
        # Test 1: HTTP 200 status code
        run_test "Web interface returns HTTP 200" \
            "[ \"\$(curl -s -o /dev/null -w '%{http_code}' http://localhost:${APP_PORT}/)\" -eq 200 ]"
        
        # Test 2: Copyright notice present
        run_test "Web interface contains copyright notice" \
            "curl -s http://localhost:${APP_PORT}/ | grep -q '© Copyright.*Olivier Planson'"
    else
        print_info "No index.html found in WAR, skipping web interface tests"
    fi
}

################################################################################
# REPORTING FUNCTIONS
################################################################################

# Print final test results summary
print_test_summary() {
    print_header "Test Results Summary"
    
    echo "Total Tests: $((TESTS_PASSED + TESTS_FAILED))"
    echo -e "${GREEN}Passed: $TESTS_PASSED${NC}"
    echo -e "${RED}Failed: $TESTS_FAILED${NC}"
    echo ""
    
    # Detailed results table
    echo "Detailed Results:"
    echo "┌────┬─────────────────────────────────────────────┬──────────┐"
    echo "│ #  │ Test Name                                   │ Status   │"
    echo "├────┼─────────────────────────────────────────────┼──────────┤"
    
    local i
    for i in $(seq 1 "$TEST_NUMBER"); do
        local status="${TEST_RESULTS[$i]:-}"
        local status_display
        
        if [ "$status" = "PASSED" ]; then
            status_display="${GREEN}✓ PASSED${NC}"
        else
            status_display="${RED}✗ FAILED${NC}"
        fi
        
        printf "│ %-2d │ %-43s │ " "$i" "${TEST_NAMES[$i]:-Unknown}"
        echo -e "${status_display} │"
    done
    
    echo "└────┴─────────────────────────────────────────────┴──────────┘"
    echo ""
    
    # Show failed test commands
    if [ $TESTS_FAILED -gt 0 ]; then
        echo "Failed Test Commands:"
        echo "─────────────────────"
        local i
        for i in $(seq 1 "$TEST_NUMBER"); do
            if [ "${TEST_RESULTS[$i]:-}" = "FAILED" ]; then
                echo "Test $i: ${TEST_NAMES[$i]:-Unknown}"
                echo "  Command: ${FAILED_COMMANDS[$i]:-}"
                echo ""
            fi
        done
    fi
}

################################################################################
# MAIN EXECUTION
################################################################################

main() {
    # Parse command line arguments
    parse_arguments "$@"
    
    print_header "$LAB_NAME - Podman Test"
    
    # Check prerequisites
    print_step "Step 0: Checking prerequisites"
    
    if ! command -v podman >/dev/null 2>&1; then
        print_error "Podman not found"
        echo ""
        echo "Please install Podman:"
        echo "  macOS: brew install podman"
        echo "  Linux: sudo apt-get install podman"
        echo "  Windows: Download from https://podman.io/getting-started/installation"
        exit 1
    fi
    
    print_success "Podman found"
    podman --version
    echo ""
    
    if ! command -v mvn >/dev/null 2>&1; then
        print_error "Maven not found"
        echo ""
        echo "Please install Maven:"
        echo "  macOS: brew install maven"
        echo "  Linux: sudo apt-get install maven"
        exit 1
    fi
    
    print_success "Maven found"
    mvn --version | head -1
    echo ""
    
    # Phase 1: Environment Cleanup
    print_header "Phase 1: Environment Cleanup"
    cleanup_environment "$CONTAINER_NAME" "$DB_CONTAINER" "$IMAGE_NAME" "$DB_MODE"
    echo ""
    
    # Phase 2: Build Application
    print_header "Phase 2: Build Microservices"
    
    # Verify build directory exists
    if [ ! -d "$BUILD_DIR" ]; then
        print_error "Directory not found: $BUILD_DIR"
        exit 1
    fi
    
    print_info "Building from directory: $BUILD_DIR"
    if ! cd "$BUILD_DIR"; then
        print_error "Failed to change to directory: $BUILD_DIR"
        exit 1
    fi
    
    # Detect microservices (directories with pom.xml)
    local microservices=()
    for dir in */; do
        if [ -f "${dir}pom.xml" ]; then
            microservices+=("${dir%/}")
        fi
    done
    
    if [ ${#microservices[@]} -eq 0 ]; then
        print_error "No microservices found (no directories with pom.xml)"
        exit 1
    fi
    
    print_info "Found ${#microservices[@]} microservice(s): ${microservices[*]}"
    echo ""
    
    # Build each microservice
    local build_failed=0
    for service in "${microservices[@]}"; do
        print_step "Building $service..."
        
        if ! cd "$service"; then
            print_error "Failed to change to directory: $service"
            build_failed=1
            continue
        fi
        
        if mvn clean package -DskipTests -q; then
            # Special handling for api-gateway: build and copy root-redirect AFTER main build
            if [ "$service" = "api-gateway" ] && [ -d "root-redirect" ]; then
                print_info "Building root-redirect submodule..."
                # Build in subshell but check result in parent shell
                if (cd root-redirect && mvn clean package -DskipTests -q); then
                    # Copy root-redirect WAR to parent target directory for Containerfile
                    local root_redirect_war="root-redirect/target/root-redirect-1.0.0.war"
                    if [ -f "$root_redirect_war" ]; then
                        cp "$root_redirect_war" target/
                        print_success "root-redirect submodule built and copied to target/"
                        print_info "WAR files in target/:"
                        ls -lh target/*.war 2>/dev/null
                    else
                        print_warning "root-redirect WAR not found at: $root_redirect_war"
                        print_info "Contents of root-redirect/target/:"
                        ls -la root-redirect/target/ 2>/dev/null || print_error "Directory not found"
                    fi
                else
                    print_warning "root-redirect submodule build failed (non-critical)"
                fi
            fi
            
            # Find WAR file in target directory
            local war_file
            war_file=$(find target -name "*.war" -type f 2>/dev/null | head -1)
            
            if [ -n "$war_file" ]; then
                local war_size
                war_size=$(ls -lh "$war_file" | awk '{print $5}')
                print_success "$service: WAR created ($war_size)"
            else
                print_warning "$service: No WAR file found (may be JAR-based service)"
            fi
        else
            print_error "$service: Build failed"
            build_failed=1
        fi
        
        cd ..
    done
    
    if [ $build_failed -eq 1 ]; then
        print_error "One or more microservices failed to build"
        exit 1
    fi
    
    print_success "All microservices built successfully"
    echo ""
    
    # Phase 3: Build and Deploy Containers
    print_header "Phase 3: Build and Deploy Containers (DB mode: $DB_MODE)"
    
    # Deploy database (docker-compose mode only)
    if [ "$DB_MODE" = "docker-compose" ]; then
        print_step "Starting databases with docker-compose..."
        if [ -f "docker-compose.yml" ]; then
            if docker-compose up -d; then
                print_success "Database containers starting..."
                
                # Wait for each database to be ready
                # Lab08 has 2 databases: client-db and account-db
                wait_for_service "Client Database" \
                    "podman exec \"banking-client-db\" pg_isready -U \"$DB_USER\" -d \"banking_client_db\"" \
                    "$DB_READY_TIMEOUT" \
                    "$HEALTH_CHECK_INTERVAL"
                
                wait_for_service "Account Database" \
                    "podman exec \"banking-account-db\" pg_isready -U \"$DB_USER\" -d \"banking_account_db\"" \
                    "$DB_READY_TIMEOUT" \
                    "$HEALTH_CHECK_INTERVAL"
            else
                print_error "Failed to start databases"
                exit 1
            fi
        else
            print_error "docker-compose.yml not found but DB_MODE=docker-compose"
            exit 1
        fi
    elif [ "$DB_MODE" = "none" ]; then
        print_info "No database required for this lab"
    else
        print_error "Invalid DB_MODE: $DB_MODE (must be: none or docker-compose)"
        exit 1
    fi
    
    # Get the network created by docker-compose
    local network_name
    network_name=$(podman network ls --format "{{.Name}}" | grep -E "lab08|solution" | head -1)
    
    if [ -z "$network_name" ]; then
        print_warning "No docker-compose network found, creating lab08-network..."
        network_name="lab08-network"
        if podman network create "$network_name" 2>/dev/null; then
            print_success "Network created: $network_name"
        else
            print_error "Failed to create network"
            exit 1
        fi
    else
        print_success "Using existing network: $network_name"
    fi
    
    # Build and deploy each microservice container
    local container_failed=0
    
    for service in "${microservices[@]}"; do
        local service_image="banking-${service}-lab08"
        local service_container="banking-${service}-lab08"
        
        # Assign ports based on service name
        local service_port
        case "$service" in
            "client-service")
                service_port="9081"
                ;;
            "account-service")
                service_port="9082"
                ;;
            "api-gateway")
                service_port="9080"  # Main application
                ;;
            *)
                print_warning "$service: Unknown service, using default port 9090"
                service_port="9090"
                ;;
        esac
        
        print_step "Building container image for $service..."
        
        if ! cd "$service"; then
            print_error "Failed to change to directory: $service"
            container_failed=1
            continue
        fi
        
        # Check if Containerfile exists
        if [ ! -f "Containerfile" ]; then
            print_error "$service: No Containerfile found"
            container_failed=1
            cd ..
            continue
        fi
        
        # Build image
        if podman build -t "$service_image" -f Containerfile . -q; then
            print_success "$service: Image built ($service_image)"
        else
            print_error "$service: Image build failed"
            container_failed=1
            cd ..
            continue
        fi
        
        # Start container with network and environment variables
        print_step "Starting $service container on port $service_port..."
        
        # Set environment variables based on service
        local env_vars=""
        case "$service" in
            "client-service")
                env_vars="-e DB_HOST=banking-client-db -e DB_PORT=5432 -e DB_NAME=banking_client_db -e DB_USER=$DB_USER -e DB_PASSWORD=$DB_PASSWORD"
                ;;
            "account-service")
                env_vars="-e DB_HOST=banking-account-db -e DB_PORT=5432 -e DB_NAME=banking_account_db -e DB_USER=$DB_USER -e DB_PASSWORD=$DB_PASSWORD -e CLIENT_SERVICE_URL=http://banking-client-service-lab08:9080"
                ;;
            "api-gateway")
                env_vars="-e CLIENT_SERVICE_URL=http://banking-client-service-lab08:9080 -e ACCOUNT_SERVICE_URL=http://banking-account-service-lab08:9080"
                ;;
        esac
        
        if podman run -d \
            --name "$service_container" \
            --network "$network_name" \
            -p "$service_port:9080" \
            $env_vars \
            "$service_image"; then
            print_success "$service: Container started ($service_container)"
        else
            print_error "$service: Container failed to start"
            container_failed=1
            cd ..
            continue
        fi
        
        # Wait for service to be ready
        wait_for_service "$service" \
            "curl -f -s http://localhost:${service_port}/health/live > /dev/null" \
            "$APP_READY_TIMEOUT" \
            "$HEALTH_CHECK_INTERVAL"
        
        cd ..
    done
    
    if [ $container_failed -eq 1 ]; then
        print_error "One or more microservice containers failed to deploy"
        exit 1
    fi
    
    print_success "All microservice containers deployed successfully"
    
    echo ""
    
    # Phase 4: Execute Tests
    print_header "Phase 4: Execute Tests"
    
    # Test each microservice
    print_info "Testing individual microservices..."
    
    run_test "Client Service: Liveness probe" \
        "curl -f -s http://localhost:9081/health/live > /dev/null"
    
    run_test "Client Service: Readiness probe" \
        "curl -f -s http://localhost:9081/health/ready > /dev/null"
    
    run_test "Account Service: Liveness probe" \
        "curl -f -s http://localhost:9082/health/live > /dev/null"
    
    run_test "Account Service: Readiness probe" \
        "curl -f -s http://localhost:9082/health/ready > /dev/null"
    
    # Test API Gateway (main application on port 9080)
    print_info "Testing API Gateway (main application)..."
    
    run_test "API Gateway: Liveness probe" \
        "curl -f -s http://localhost:9080/health/live > /dev/null"
    
    run_test "API Gateway: Readiness probe" \
        "curl -f -s http://localhost:9080/health/ready > /dev/null"
    
    run_test "API Gateway: Root redirect (/api/ -> /web/api/)" \
        "curl -f -s http://localhost:9080/api/clients > /dev/null"
    
    # Test API Gateway routing to microservices
    print_info "Testing API Gateway routing..."
    
    run_test "API Gateway: Clients API routing (via redirect)" \
        "curl -f -s -L http://localhost:9080/api/clients > /dev/null"
    
    run_test "API Gateway: Accounts API routing (via redirect)" \
        "curl -f -s -L http://localhost:9080/api/accounts > /dev/null"
    
    run_test "API Gateway: Direct web endpoint" \
        "curl -f -s http://localhost:9080/web/api/clients > /dev/null"
    
    # Test database connectivity
    print_info "Testing database connectivity..."
    
    run_test "Client DB: Schema initialized" \
        "podman exec banking-client-db psql -U \"$DB_USER\" -d banking_client_db -c '\dt' | grep -q 'clients'"
    
    run_test "Account DB: Schema initialized" \
        "podman exec banking-account-db psql -U \"$DB_USER\" -d banking_account_db -c '\dt' | grep -q 'accounts'"



    
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
        
        # Open browser to API Gateway (main application)
        print_info "Opening API Gateway in browser..."
        if command -v open >/dev/null 2>&1; then
            # macOS
            open "http://localhost:9080/" 2>/dev/null || true
        elif command -v xdg-open >/dev/null 2>&1; then
            # Linux
            xdg-open "http://localhost:9080/" 2>/dev/null || true
        elif command -v start >/dev/null 2>&1; then
            # Windows
            start "http://localhost:9080/" 2>/dev/null || true
        else
            print_info "Could not detect browser command. Please open manually:"
            echo "  API Gateway: http://localhost:9080/"
            echo "  Client Service: http://localhost:9081/"
            echo "  Account Service: http://localhost:9082/"
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

# Run main function with all arguments
main "$@"

# Made with IBM Bob
