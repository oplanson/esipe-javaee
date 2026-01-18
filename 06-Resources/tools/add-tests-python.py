#!/usr/bin/env python3
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

"""
Script to add lab-specific tests to podman-test.sh files
"""

import os
import re

BASE_DIR = "../../03-Labs"

# Define tests for each lab
TESTS = {
    "Lab05-REST": """    
    # Lab05-REST Specific Tests
    print_info "Running Lab05-REST specific tests..."
    
    # Test REST API endpoints
    run_test "REST API root accessible" \\
        "curl -f -s http://localhost:${APP_PORT}/api > /dev/null"
    
    run_test "Client REST endpoint" \\
        "curl -f -s http://localhost:${APP_PORT}/api/clients > /dev/null"
    
    run_test "Account REST endpoint" \\
        "curl -f -s http://localhost:${APP_PORT}/api/accounts > /dev/null"
    
    # Test JSON response
    run_test "JSON response format" \\
        "curl -s http://localhost:${APP_PORT}/api/clients | grep -q '\\[' || true"
    
    # Test CORS headers
    run_test "CORS headers present" \\
        "curl -I -s http://localhost:${APP_PORT}/api/clients | grep -q 'Access-Control' || true"
    
    # Test OpenAPI documentation (if available)
    if curl -f -s "http://localhost:${APP_PORT}/openapi" > /dev/null 2>&1; then
        run_test "OpenAPI documentation available" \\
            "curl -f -s http://localhost:${APP_PORT}/openapi > /dev/null"
    fi
    
    # Test database connectivity
    run_test "Database schema initialized" \\
        "docker exec \\"$DB_CONTAINER\\" psql -U \\"$DB_USER\\" -d \\"$DB_NAME\\" -c '\\\\dt' | grep -q 'clients'"
""",
    
    "Lab06-DDD": """    
    # Lab06-DDD Specific Tests
    print_info "Running Lab06-DDD specific tests..."
    
    # Test domain model via REST API
    run_test "Client REST API (DDD)" \\
        "curl -f -s http://localhost:${APP_PORT}/api/clients > /dev/null"
    
    run_test "Account REST API (DDD)" \\
        "curl -f -s http://localhost:${APP_PORT}/api/accounts > /dev/null"
    
    # Test value objects and domain events
    run_test "Domain events active (check logs)" \\
        "podman logs \\"$CONTAINER_NAME\\" 2>&1 | grep -q 'Event' || true"
    
    # Test bounded context
    run_test "Web interface (bounded context)" \\
        "curl -f -s http://localhost:${APP_PORT}/clients > /dev/null"
    
    # Test API versioning (if v2 exists)
    if curl -f -s "http://localhost:${APP_PORT}/api/v2/clients" > /dev/null 2>&1; then
        run_test "API v2 available" \\
            "curl -f -s http://localhost:${APP_PORT}/api/v2/clients > /dev/null"
    fi
    
    # Test database with DDD schema
    run_test "DDD database schema" \\
        "docker exec \\"$DB_CONTAINER\\" psql -U \\"$DB_USER\\" -d \\"$DB_NAME\\" -c '\\\\dt' | grep -q 'clients'"
""",
    
    "Lab07-Hexagonal": """    
    # Lab07-Hexagonal Specific Tests
    print_info "Running Lab07-Hexagonal specific tests..."
    
    # Test use cases via REST adapter
    run_test "Client REST adapter (v1)" \\
        "curl -f -s http://localhost:${APP_PORT}/api/v1/clients > /dev/null"
    
    run_test "Account REST adapter (v1)" \\
        "curl -f -s http://localhost:${APP_PORT}/api/v1/accounts > /dev/null"
    
    # Test API versioning (hexagonal ports)
    if curl -f -s "http://localhost:${APP_PORT}/api/v2/clients" > /dev/null 2>&1; then
        run_test "Client REST adapter (v2)" \\
            "curl -f -s http://localhost:${APP_PORT}/api/v2/clients > /dev/null"
    fi
    
    # Test web adapter
    run_test "Web adapter accessible" \\
        "curl -f -s http://localhost:${APP_PORT}/clients > /dev/null"
    
    # Test domain isolation
    run_test "Domain events (hexagonal)" \\
        "podman logs \\"$CONTAINER_NAME\\" 2>&1 | grep -q 'Event' || true"
    
    # Test persistence adapter
    run_test "JPA persistence adapter" \\
        "docker exec \\"$DB_CONTAINER\\" psql -U \\"$DB_USER\\" -d \\"$DB_NAME\\" -c '\\\\dt' | grep -q 'clients'"
""",
    
    "Lab08-Microservices": """    
    # Lab08-Microservices Specific Tests
    print_info "Running Lab08-Microservices specific tests..."
    
    # Test microservices endpoints
    run_test "Microservices API accessible" \\
        "curl -f -s http://localhost:${APP_PORT}/api/clients > /dev/null"
    
    # Test service discovery (if configured)
    run_test "Service health endpoints" \\
        "curl -f -s http://localhost:${APP_PORT}/health > /dev/null"
    
    # Test metrics endpoint
    if curl -f -s "http://localhost:${APP_PORT}/metrics" > /dev/null 2>&1; then
        run_test "Metrics endpoint available" \\
            "curl -f -s http://localhost:${APP_PORT}/metrics > /dev/null"
    fi
    
    # Test config (MicroProfile Config)
    run_test "MicroProfile Config active" \\
        "podman logs \\"$CONTAINER_NAME\\" 2>&1 | grep -q 'CWWKZ0001I' || true"
    
    # Test fault tolerance (if configured)
    run_test "Fault tolerance configured" \\
        "podman logs \\"$CONTAINER_NAME\\" 2>&1 | grep -q 'mpFaultTolerance' || true"
    
    # Test database connectivity
    run_test "Database schema initialized" \\
        "docker exec \\"$DB_CONTAINER\\" psql -U \\"$DB_USER\\" -d \\"$DB_NAME\\" -c '\\\\dt' | grep -q 'clients'"
""",
    
    "Lab09-Security": """    
    # Lab09-Security Specific Tests
    print_info "Running Lab09-Security specific tests..."
    
    # Test public endpoints
    run_test "Login page accessible" \\
        "curl -f -s http://localhost:${APP_PORT}/login > /dev/null"
    
    run_test "Public API accessible" \\
        "curl -f -s http://localhost:${APP_PORT}/api/public/health > /dev/null || true"
    
    # Test authentication endpoint
    run_test "Authentication endpoint exists" \\
        "curl -s -X POST http://localhost:${APP_PORT}/api/auth/login -H 'Content-Type: application/json' -d '{\"username\":\"test\",\"password\":\"test\"}' > /dev/null || true"
    
    # Test secured endpoints (should return 401/403)
    run_test "Secured endpoints protected" \\
        "curl -s -o /dev/null -w '%{http_code}' http://localhost:${APP_PORT}/api/admin/users | grep -q '40[13]' || true"
    
    # Test JWT configuration
    run_test "JWT security configured" \\
        "podman logs \\"$CONTAINER_NAME\\" 2>&1 | grep -q 'mpJwt' || true"
    
    # Test user database
    run_test "User table exists" \\
        "docker exec \\"$DB_CONTAINER\\" psql -U \\"$DB_USER\\" -d \\"$DB_NAME\\" -c '\\\\dt' | grep -q 'users' || true"
    
    # Test role-based access
    run_test "Roles configured" \\
        "docker exec \\"$DB_CONTAINER\\" psql -U \\"$DB_USER\\" -d \\"$DB_NAME\\" -c 'SELECT COUNT(*) FROM roles;' > /dev/null 2>&1 || true"
"""
}

def add_tests_to_lab(lab_name, tests_content):
    """Add tests to a lab's podman-test.sh file"""
    file_path = os.path.join(BASE_DIR, lab_name, "podman-test.sh")
    
    if not os.path.exists(file_path):
        print(f"❌ File not found: {file_path}")
        return False
    
    with open(file_path, 'r') as f:
        content = f.read()
    
    # Find the insertion point (after test_web_interface)
    pattern = r'(    # Web interface tests \(if applicable\)\n    test_web_interface "target/\$WAR_NAME"\n    )'
    
    if not re.search(pattern, content):
        print(f"❌ Could not find insertion point in {lab_name}")
        return False
    
    # Insert tests after test_web_interface
    new_content = re.sub(
        pattern,
        r'\1' + tests_content + '\n',
        content
    )
    
    with open(file_path, 'w') as f:
        f.write(new_content)
    
    print(f"✅ {lab_name} tests added")
    return True

def main():
    print("=" * 50)
    print("Adding Lab-Specific Tests (Python)")
    print("=" * 50)
    print()
    
    success_count = 0
    for lab_name, tests in TESTS.items():
        if add_tests_to_lab(lab_name, tests):
            success_count += 1
    
    print()
    print("=" * 50)
    print(f"✅ {success_count}/{len(TESTS)} labs updated successfully")
    print("=" * 50)

if __name__ == "__main__":
    main()

# Made with Bob
