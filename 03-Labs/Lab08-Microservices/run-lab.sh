#!/bin/bash

# © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Lab08 - Microservices Architecture - Development Mode Runner
# This script starts all microservices in development mode with Liberty dev mode

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

# Function to check prerequisites
check_prerequisites() {
    print_info "Checking prerequisites..."
    
    local missing_tools=()
    
    if ! command_exists mvn; then
        missing_tools+=("Maven")
    fi
    
    if ! command_exists docker-compose && ! command_exists podman-compose; then
        missing_tools+=("Docker Compose or Podman Compose")
    fi
    
    if [ ${#missing_tools[@]} -gt 0 ]; then
        print_error "Missing required tools: ${missing_tools[*]}"
        print_info "Please install the missing tools and try again."
        exit 1
    fi
    
    print_success "All prerequisites satisfied"
}

# Function to start databases
start_databases() {
    print_info "Starting PostgreSQL databases..."
    
    cd "${SCRIPT_DIR}"
    
    if command_exists docker-compose; then
        docker-compose up -d client-db account-db
    elif command_exists podman-compose; then
        podman-compose up -d client-db account-db
    fi
    
    print_info "Waiting for databases to be ready..."
    sleep 10
    
    print_success "Databases started"
}

# Function to start a service in dev mode
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    
    print_info "Starting ${service_name} in dev mode on port ${port}..."
    
    if [ ! -d "${service_dir}" ]; then
        print_error "Service directory not found: ${service_dir}"
        return 1
    fi
    
    cd "${service_dir}"
    
    # Start Liberty dev mode in background
    mvn liberty:dev &
    
    print_success "${service_name} starting in background (PID: $!)"
}

# Function to display service URLs
display_urls() {
    echo ""
    print_info "=== Microservices Development Environment ==="
    echo ""
    print_info "Client Service:"
    echo "  - Application: http://localhost:9081/"
    echo "  - Health: http://localhost:9081/health"
    echo "  - Metrics: http://localhost:9081/metrics"
    echo "  - OpenAPI: http://localhost:9081/openapi"
    echo ""
    print_info "Account Service:"
    echo "  - Application: http://localhost:9082/"
    echo "  - Health: http://localhost:9082/health"
    echo "  - Metrics: http://localhost:9082/metrics"
    echo "  - OpenAPI: http://localhost:9082/openapi"
    echo ""
    print_info "API Gateway:"
    echo "  - Application: http://localhost:9080/"
    echo "  - Health: http://localhost:9080/health"
    echo "  - Metrics: http://localhost:9080/metrics"
    echo "  - OpenAPI: http://localhost:9080/openapi"
    echo ""
    print_info "Databases:"
    echo "  - Client DB: localhost:5433 (banking_client_db)"
    echo "  - Account DB: localhost:5434 (banking_account_db)"
    echo ""
    print_warning "Press Ctrl+C in each terminal to stop the services"
    print_warning "Run './stop-lab.sh' to stop all databases"
    echo ""
}

# Function to cleanup on exit
cleanup() {
    print_info "Cleaning up..."
    
    # Kill all Maven processes
    pkill -f "mvn liberty:dev" || true
    
    print_success "Cleanup complete"
}

# Main execution
main() {
    print_info "=== Lab08 - Microservices Architecture - Development Mode ==="
    echo ""
    
    # Check prerequisites
    check_prerequisites
    
    # Start databases
    start_databases
    
    # Display instructions
    echo ""
    print_info "Starting services in Liberty dev mode..."
    print_warning "Each service will start in a separate terminal window"
    print_warning "Make sure you have at least 3 terminal windows available"
    echo ""
    
    # Check if services exist
    if [ ! -d "${CLIENT_SERVICE_DIR}" ] || [ ! -d "${ACCOUNT_SERVICE_DIR}" ] || [ ! -d "${API_GATEWAY_DIR}" ]; then
        print_error "Service directories not found. Please ensure the solution is complete."
        exit 1
    fi
    
    # Display URLs
    display_urls
    
    # Instructions for manual start
    print_info "To start each service manually, open 3 terminals and run:"
    echo ""
    echo "Terminal 1 (Client Service):"
    echo "  cd ${CLIENT_SERVICE_DIR}"
    echo "  mvn liberty:dev"
    echo ""
    echo "Terminal 2 (Account Service):"
    echo "  cd ${ACCOUNT_SERVICE_DIR}"
    echo "  mvn liberty:dev"
    echo ""
    echo "Terminal 3 (API Gateway):"
    echo "  cd ${API_GATEWAY_DIR}"
    echo "  mvn liberty:dev"
    echo ""
    
    print_info "Liberty dev mode features:"
    echo "  - Automatic reload on code changes"
    echo "  - Run tests with 'mvn test' in each terminal"
    echo "  - Press Enter to run tests"
    echo "  - Press Ctrl+C to stop"
    echo ""
    
    print_success "Development environment ready!"
    print_info "Access the API Gateway at http://localhost:9080/"
}

# Trap Ctrl+C and cleanup
trap cleanup EXIT INT TERM

# Run main function
main

# Made with Bob
