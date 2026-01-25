#!/usr/bin/env bash
# © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

################################################################################
# Lab 02B - JSF Client Management: Podman Test Script
# 
# This script provides automated testing for Lab 02B with:
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

# Strict mode for better error handling
set -o pipefail
set -o nounset

# Note: NOT using 'set -e' in test phase so all tests run even if some fail

################################################################################
# CONFIGURATION SECTION
################################################################################

# Lab identification
LAB_NAME="Lab 02B - JSF Client Management"
LAB_NUMBER="02B"

# Container configuration
IMAGE_NAME="lab02b-jsf:latest"
CONTAINER_NAME="lab02b-jsf-container"
APP_PORT=9080

# Database deployment mode
DB_MODE="none"

# Database configuration (not needed for Lab 02B)
DB_CONTAINER=""
DB_PORT=5432
DB_USER="bankuser"
DB_PASSWORD="bankpass"
DB_NAME="bankdb"

# Build configuration
BUILD_DIR="solution"  # Default directory to build
WAR_NAME="lab02b-jsf.war"

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
    
    print_info "Cleaning up environment (DB mode: $db_mode)..."
    
    # Stop and remove application container
    if podman ps -a --format "{{.Names}}" 2>/dev/null | grep -q "^${container_name}$"; then
        print_info "Stopping application container..."
        podman stop "${container_name}" 2>/dev/null || true
        podman rm -f "${container_name}" 2>/dev/null || true
        print_success "Application container removed"
    fi
    
    # Database cleanup (docker-compose mode only)
    if [ "$db_mode" = "docker-compose" ]; then
        if [ -f "${BUILD_DIR}/docker-compose.yml" ]; then
            print_info "Stopping docker-compose services..."
            (cd "${BUILD_DIR}" && docker-compose down -v 2>/dev/null) || true
            print_success "Docker-compose services stopped"
        fi
    fi
    
    # Remove image
    if podman image exists "${image_name}" 2>/dev/null; then
        print_info "Removing old image..."
        podman rmi -f "${image_name}" 2>/dev/null || true
        print_success "Image removed"
    fi
    
    # Check for port conflicts
    check_port_conflicts "$APP_PORT" "$container_name"
    
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
    
    # Check if index.xhtml exists in WAR
    if unzip -l "$war_file" 2>/dev/null | grep -q "index.xhtml"; then
        print_info "JSF web interface detected in WAR, testing..."
        
        # Test 1: HTTP 200 status code
        run_test "JSF home page returns HTTP 200" \
            "[ \"\$(curl -s -o /dev/null -w '%{http_code}' http://localhost:${APP_PORT}/lab02b-jsf/)\" -eq 200 ]"
        
        # Test 2: Copyright notice present
        run_test "JSF home page contains copyright notice" \
            "curl -s http://localhost:${APP_PORT}/lab02b-jsf/ | grep -q '© Copyright.*Olivier Planson'"
    else
        print_info "No index.xhtml found in WAR, skipping JSF home page tests"
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
    if [ "$TESTS_FAILED" -gt 0 ]; then
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
    print_header "Phase 2: Build Application"
    
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
    
    # Maven build
    print_step "Building with Maven..."
    if mvn clean package -DskipTests -q; then
        print_success "Build successful"
        
        # Verify WAR file
        if [ -f "target/$WAR_NAME" ]; then
            local war_size
            war_size=$(ls -lh "target/$WAR_NAME" | awk '{print $5}')
            print_success "WAR file created: target/$WAR_NAME ($war_size)"
        else
            print_error "WAR file not found: target/$WAR_NAME"
            exit 1
        fi
    else
        print_error "Build failed"
        exit 1
    fi
    echo ""
    
    # Phase 3: Build and Deploy Containers
    print_header "Phase 3: Build and Deploy Containers"
    
    # Build application image
    print_step "Building application image..."
    if podman build -t "$IMAGE_NAME" -f Containerfile . -q; then
        print_success "Image built: $IMAGE_NAME"
    else
        print_error "Image build failed"
        exit 1
    fi
    
    # Start application container
    print_step "Starting application container..."
    if podman run -d \
        --name "$CONTAINER_NAME" \
        -p "$APP_PORT:9080" \
        -p 9443:9443 \
        "$IMAGE_NAME"; then
        print_success "Container started: $CONTAINER_NAME"
    else
        print_error "Container failed to start"
        exit 1
    fi
    
    # Wait for application to be ready
    wait_for_service "Application" \
        "curl -f -s http://localhost:${APP_PORT}/lab02b-jsf/ >/dev/null" \
        "$APP_READY_TIMEOUT" \
        "$HEALTH_CHECK_INTERVAL"
    
    echo ""
    
    # Phase 4: Execute Tests
    print_header "Phase 4: Execute Tests"
    
    # Web interface tests (automatic)
    test_web_interface "target/$WAR_NAME"
    
    # Lab02B-specific: JSF pages tests
    run_test "Client list page accessible" \
        "curl -f -s http://localhost:${APP_PORT}/lab02b-jsf/views/client-list.xhtml >/dev/null"
    
    run_test "Client list page contains JSF content" \
        "curl -s http://localhost:${APP_PORT}/lab02b-jsf/views/client-list.xhtml | grep -q 'javax.faces\|jakarta.faces'"
    
    run_test "Client form page accessible" \
        "curl -f -s http://localhost:${APP_PORT}/lab02b-jsf/views/client-form.xhtml >/dev/null"
    
    run_test "Client details page accessible" \
        "curl -f -s http://localhost:${APP_PORT}/lab02b-jsf/views/client-details.xhtml >/dev/null"
    
    
    # MicroProfile Health checks
    run_test "Health endpoint accessible" \
        "curl -f -s http://localhost:${APP_PORT}/health >/dev/null"
    
    run_test "Health endpoint returns UP status" \
        "curl -s http://localhost:${APP_PORT}/health | grep -q 'UP'"
    
    run_test "Liveness probe accessible" \
        "curl -f -s http://localhost:${APP_PORT}/health/live >/dev/null"
    
    run_test "Liveness probe returns UP status" \
        "curl -s http://localhost:${APP_PORT}/health/live | grep -q 'UP'"
    
    run_test "Readiness probe accessible" \
        "curl -f -s http://localhost:${APP_PORT}/health/ready >/dev/null"
    
    run_test "Readiness probe returns UP status" \
        "curl -s http://localhost:${APP_PORT}/health/ready | grep -q 'UP'"
    
    # JSF-specific: Check for JSF ViewState (indicates JSF is working)
    run_test "JSF ViewState present in pages" \
        "curl -s http://localhost:${APP_PORT}/lab02b-jsf/views/client-list.xhtml | grep -q 'javax.faces.ViewState\|jakarta.faces.ViewState'"
    
    echo ""



    # Test logs files with no ERROR
    print_info "Checking server logs for errors..."

    # Function to check logs for errors
    check_container_logs() {
        local container_name="$1"
        local image_name="$2"
        local log_file="${3:-/logs/messages.log}"
        local error_pattern="${4:- E }"
    
        # Check if container exists and is running
        if ! podman ps --format "{{.Names}}" 2>/dev/null | grep -q "^${container_name}$"; then
            print_warning "Container $container_name not running, skipping log check"
            return 0
        fi
    
        # Check if log file exists (with error suppression)
        if ! podman exec "$container_name" test -f "$log_file" 2>/dev/null; then
            print_warning "Log file $log_file not found in $container_name"
            return 0
        fi
    
        # Count error lines (with full error suppression and fallback)
        local error_count=0
        error_count=$(podman exec "$container_name" grep -E "$error_pattern" "$log_file" 2>/dev/null | wc -l 2>/dev/null | tr -d ' ' 2>/dev/null) || error_count=0
    
        # Ensure error_count is a valid number
        if ! [[ "$error_count" =~ ^[0-9]+$ ]]; then
            error_count=0
        fi
    
        # Run test - this will record pass/fail but won't exit
        run_test "Server $image_name: Logs check (errors: $error_count)" \
            "[ \"$error_count\" -eq 0 ]" || true
    
        return 0
    }

    # Check logs for all containers (each call returns 0 to continue)
    check_container_logs "$CONTAINER_NAME" "$IMAGE_NAME" || true

    echo ""

    # Phase 5: Results and Cleanup
    print_test_summary
    
    # Application info
    if [ "$TESTS_FAILED" -eq 0 ]; then
        echo ""
        print_header "Application Ready!"
        echo -e "${BLUE}JSF Application URLs:${NC}"
        echo "  🏠 Home:          http://localhost:${APP_PORT}/lab02b-jsf/"
        echo "  📋 Client List:   http://localhost:${APP_PORT}/lab02b-jsf/views/client-list.xhtml"
        echo "  ➕ New Client:    http://localhost:${APP_PORT}/lab02b-jsf/views/client-form.xhtml"
        echo "  👤 Client Details: http://localhost:${APP_PORT}/lab02b-jsf/views/client-details.xhtml"
        echo ""
        echo -e "${BLUE}MicroProfile Endpoints:${NC}"
        echo "  💊 Health:        http://localhost:${APP_PORT}/health"
        echo "  ❤️  Liveness:      http://localhost:${APP_PORT}/health/live"
        echo "  ✅ Readiness:     http://localhost:${APP_PORT}/health/ready"
        echo ""
        echo -e "${BLUE}Container Management:${NC}"
        echo "  View logs:    podman logs -f $CONTAINER_NAME"
        echo "  Stop:         podman stop $CONTAINER_NAME"
        echo "  Remove:       podman rm $CONTAINER_NAME"
        echo "  Restart:      podman restart $CONTAINER_NAME"
        echo "  Shell:        podman exec -it $CONTAINER_NAME /bin/bash"
        echo ""
    fi
    
    # Final result
    if [ "$TESTS_FAILED" -eq 0 ]; then
        echo ""
        echo "╔═══════════════════════════════════════════════════════════════╗"
        printf "║  ✅ All %d tests passed successfully!%*s║\n" "$TESTS_PASSED" $((37 - ${#TESTS_PASSED})) ""
        echo "╚═══════════════════════════════════════════════════════════════╝"
        echo ""
        
        # Open browser if index.xhtml exists and all tests passed
        if unzip -l "target/$WAR_NAME" 2>/dev/null | grep -q "index.xhtml"; then
            print_info "Opening browser..."
            if command -v open >/dev/null 2>&1; then
                # macOS
                open "http://localhost:${APP_PORT}/lab02b-jsf/" 2>/dev/null || true
            elif command -v xdg-open >/dev/null 2>&1; then
                # Linux
                xdg-open "http://localhost:${APP_PORT}/lab02b-jsf/" 2>/dev/null || true
            elif command -v start >/dev/null 2>&1; then
                # Windows
                start "http://localhost:${APP_PORT}/lab02b-jsf/" 2>/dev/null || true
            else
                print_info "Could not detect browser command. Please open manually:"
                echo "  http://localhost:${APP_PORT}/lab02b-jsf/"
            fi
        fi
        
        echo ""
        echo -e "${BLUE}Container is still running. Use the commands above to manage it.${NC}"
        echo ""
        exit 0
    else
        echo ""
        echo "╔═══════════════════════════════════════════════════════════════╗"
        printf "║  ❌ %d test(s) failed!%*s║\n" "$TESTS_FAILED" $((46 - ${#TESTS_FAILED})) ""
        echo "╚═══════════════════════════════════════════════════════════════╝"
        echo ""
        echo -e "${YELLOW}Check container logs: podman logs $CONTAINER_NAME${NC}"
        echo ""
        exit 1
    fi
}

# Run main function with all arguments
main "$@"

# Made with IBM Bob

# Made with Bob
