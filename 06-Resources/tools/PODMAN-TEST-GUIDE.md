<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Podman Test Template - Usage Guide

**Version:** 2.0 (Multi-Mode Support)  
**Date:** January 18, 2026  
**Template File:** `podman-test-template.sh` (640 lines)

---

## 📋 Overview

This guide explains how to use the unified Podman test template (`podman-test-template.sh`) to create standardized testing scripts for Jakarta EE labs.

The template provides:
- ✅ Complete environment cleanup
- ✅ Maven build verification
- ✅ Container deployment with **3 database modes**
- ✅ Comprehensive test execution
- ✅ Detailed result reporting
- ✅ Security and portability optimizations

### 🆕 Version 2.0 Features

**Multi-Mode Database Support:**
1. **Mode "none"** - Simple applications without database (Labs 01, 02, 02B)
2. **Mode "docker-compose"** - PostgreSQL via docker-compose.yml (Labs 03, 04, 04B, 05, 06, 07, 08)
3. **Mode "podman-network"** - PostgreSQL via Podman network (Labs 09, 05B)

**Key Improvements:**
- Automatic database deployment based on `DB_MODE` configuration
- Support for both docker-compose and native Podman networks
- Unified cleanup for all deployment modes
- Enhanced portability (macOS/Linux compatible)
- Strict error handling with `set -o pipefail -o nounset`

---

## 🔧 Configuration Variables

### Required Configuration

```bash
# Lab identification
LAB_NAME="Lab XX - Description"
LAB_NUMBER="XX"

# Container configuration
IMAGE_NAME="banking-app-labXX"
CONTAINER_NAME="banking-app-labXX"
APP_PORT=9080

# Database deployment mode (choose one):
# - "none"           : No database (simple app)
# - "docker-compose" : Use docker-compose.yml for PostgreSQL
# - "podman-network" : Use Podman network with manual PostgreSQL container
DB_MODE="none"

# Database configuration (only if DB_MODE != "none")
DB_CONTAINER="banking-db-labXX"
DB_PORT=5432
DB_USER="bankuser"
DB_PASSWORD="bankpass"
DB_NAME="bankdb"
NETWORK_NAME="banking-network-labXX"

# Build configuration
BUILD_DIR="solution"  # Default directory to build
WAR_NAME="banking-app.war"

# Timeouts (in seconds)
DB_READY_TIMEOUT=30
APP_READY_TIMEOUT=60
HEALTH_CHECK_INTERVAL=2
```

### Database Mode Selection Guide

| Lab | DB_MODE | Reason |
|-----|---------|--------|
| Lab01-FirstServlet | `none` | No database needed |
| Lab02-ServletsJSP | `none` | In-memory data only |
| Lab02B-JSF | `none` | In-memory data only |
| Lab03-JPA | `docker-compose` | PostgreSQL + Flyway migrations |
| Lab04-CDI | `docker-compose` | PostgreSQL required |
| Lab04B-EJB | `docker-compose` | PostgreSQL + JMS |
| Lab05-REST | `docker-compose` | PostgreSQL required |
| Lab05B-JMS | `podman-network` | PostgreSQL + JMS (modern approach) |
| Lab06-DDD | `docker-compose` | PostgreSQL + Flyway |
| Lab07-Hexagonal | `docker-compose` | PostgreSQL + Flyway |
| Lab08-Microservices | `docker-compose` | Multi-container setup |
| Lab09-Security | `podman-network` | PostgreSQL (modern approach) |

---

## 📝 Usage Examples

### Example 1: Simple Lab (No Database)

```bash
# Lab01-FirstServlet configuration
LAB_NAME="Lab 01 - First Servlet"
LAB_NUMBER="01"
IMAGE_NAME="banking-servlet-lab01"
CONTAINER_NAME="banking-servlet-lab01"
APP_PORT=9080
DB_MODE="none"  # No database
BUILD_DIR="solution"
WAR_NAME="banking-servlet.war"
```

### Example 2: Lab with docker-compose

```bash
# Lab03-JPA configuration
LAB_NAME="Lab 03 - JPA & Database Integration"
LAB_NUMBER="03"
IMAGE_NAME="banking-jpa-lab03"
CONTAINER_NAME="banking-jpa-lab03"
APP_PORT=9080
DB_MODE="docker-compose"  # Use docker-compose.yml
DB_CONTAINER="lab03-postgres"
DB_PORT=5432
DB_USER="bankuser"
DB_PASSWORD="bankpass"
DB_NAME="bankdb"
BUILD_DIR="solution"
WAR_NAME="banking-jpa.war"
```

**Required:** `docker-compose.yml` in solution/ directory:
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:16-alpine
    container_name: lab03-postgres
    environment:
      POSTGRES_USER: bankuser
      POSTGRES_PASSWORD: bankpass
      POSTGRES_DB: bankdb
    ports:
      - "5432:5432"
```

### Example 3: Lab with Podman Network

```bash
# Lab09-Security configuration
LAB_NAME="Lab 09 - Jakarta EE Security"
LAB_NUMBER="09"
IMAGE_NAME="bank-security:latest"
CONTAINER_NAME="bank-security-app"
APP_PORT=9080
DB_MODE="podman-network"  # Use Podman network
DB_CONTAINER="bank-security-db"
DB_PORT=5432
DB_USER="bankuser"
DB_PASSWORD="bankpass"
DB_NAME="bankdb"
NETWORK_NAME="bank-security-network"
BUILD_DIR="solution"
WAR_NAME="bank-security.war"
```

**Note:** No docker-compose.yml needed. PostgreSQL container is created automatically.

---

## 🏗️ Template Structure

### 5-Phase Testing Process

```
Phase 0: Prerequisites Check
├── Podman installed
├── Maven installed
└── Parse arguments (-dir)

Phase 1: Environment Cleanup
├── Stop containers (app + DB)
├── Remove containers
├── Remove networks (if podman-network mode)
├── Stop docker-compose (if docker-compose mode)
├── Remove images
└── Check port conflicts

Phase 2: Build Application
├── Navigate to BUILD_DIR
├── Maven clean package
└── Verify WAR file

Phase 3: Build and Deploy Containers
├── Deploy database (based on DB_MODE):
│   ├── docker-compose: Start services
│   ├── podman-network: Create network + PostgreSQL container
│   └── none: Skip
├── Build application image
├── Start application container
└── Wait for health checks

Phase 4: Execute Tests
├── Health checks (liveness/readiness)
├── Web interface tests (if index.html)
├── Functional tests (API)
└── Lab-specific tests

Phase 5: Results and Cleanup
├── Test results table
├── Failed commands report
└── Exit code (0/1)
```

---

## 🧪 Adding Lab-Specific Tests

### Using the `run_test()` Function

```bash
# Phase 4: Execute Tests
print_header "Phase 4: Execute Tests"

# Standard health checks
run_test "Liveness probe" \
    "curl -f -s http://localhost:${APP_PORT}/health/live > /dev/null"

run_test "Readiness probe" \
    "curl -f -s http://localhost:${APP_PORT}/health/ready > /dev/null"

# Web interface tests (automatic if index.html exists)
test_web_interface "target/$WAR_NAME"

# Lab-specific functional tests
run_test "Create client via API" \
    "curl -f -s -X POST http://localhost:${APP_PORT}/api/clients \
     -H 'Content-Type: application/json' \
     -d '{\"name\":\"John Doe\",\"email\":\"john@example.com\"}' > /dev/null"

run_test "List clients returns 200" \
    "[ \"\$(curl -s -o /dev/null -w '%{http_code}' http://localhost:${APP_PORT}/api/clients)\" -eq 200 ]"

run_test "Client details accessible" \
    "curl -f -s http://localhost:${APP_PORT}/api/clients/1 > /dev/null"
```

### Test Tracking

The template automatically tracks:
- Test number
- Test name
- Pass/fail status
- Failed command (for debugging)

Results are displayed in a formatted table at the end.

---

## 🔍 Troubleshooting

### Common Issues

#### 1. Port Already in Use

**Symptom:** Container fails to start with port binding error

**Solution:** The template automatically detects and stops conflicting containers. If issue persists:
```bash
# Manual cleanup
podman ps -a | grep 9080
podman stop <container-name>
podman rm <container-name>
```

#### 2. Database Not Ready

**Symptom:** Application fails to connect to database

**Solution:** Increase timeout values:
```bash
DB_READY_TIMEOUT=60  # Increase from 30
APP_READY_TIMEOUT=120  # Increase from 60
```

#### 3. docker-compose Not Found

**Symptom:** Error when DB_MODE="docker-compose"

**Solution:** Install docker-compose or use podman-compose:
```bash
# macOS
brew install docker-compose

# Or use podman-compose
pip3 install podman-compose
# Then replace 'docker-compose' with 'podman-compose' in template
```

#### 4. Network Already Exists

**Symptom:** Network creation fails in podman-network mode

**Solution:** The template handles this automatically with `|| true`. If issue persists:
```bash
podman network rm <network-name>
```

---

## ✅ Best Practices

### 1. Test Naming

Use descriptive test names:
```bash
# Good
run_test "Create client with valid data returns 201"

# Bad
run_test "Test 1"
```

### 2. Test Independence

Each test should be independent:
```bash
# Good - Each test is self-contained
run_test "Create client" "..."
run_test "Update client" "..."

# Bad - Second test depends on first
run_test "Create client and get ID" "..."
run_test "Update client with ID from previous test" "..."
```

### 3. Error Messages

Provide clear error context:
```bash
run_test "Database connection successful" \
    "curl -f -s http://localhost:${APP_PORT}/health/ready | grep -q 'database.*UP'"
```

### 4. Cleanup

Always test cleanup works:
```bash
# Run script twice to verify cleanup
./podman-test.sh
./podman-test.sh  # Should work without manual cleanup
```

### 5. Directory Testing

Test both solution and starter:
```bash
# Test solution
./podman-test.sh

# Test starter (student code)
./podman-test.sh -dir starter
```

---

## 📊 Test Result Interpretation

### Success Output

```
╔═══════════════════════════════════════════════════════════════╗
║  ✅ All 12 tests passed successfully!                         ║
╚═══════════════════════════════════════════════════════════════╝
```

Browser opens automatically if index.html exists.

### Failure Output

```
╔═══════════════════════════════════════════════════════════════╗
║  ❌ 2 test(s) failed!                                         ║
╚═══════════════════════════════════════════════════════════════╝

Failed Test Commands:
─────────────────────
Test 5: Create client via API
  Command: curl -f -s -X POST http://localhost:9080/api/clients ...

Test 8: Database persistence check
  Command: curl -f -s http://localhost:9080/api/clients/1 ...
```

---

## 🚀 Quick Start Checklist

1. ✅ Copy `podman-test-template.sh` to lab directory
2. ✅ Rename to `podman-test.sh`
3. ✅ Configure variables (LAB_NAME, IMAGE_NAME, DB_MODE, etc.)
4. ✅ Add lab-specific tests in Phase 4
5. ✅ Test with solution: `./podman-test.sh`
6. ✅ Test with starter: `./podman-test.sh -dir starter`
7. ✅ Verify cleanup: Run script twice
8. ✅ Commit to repository

---

## 📚 Related Documentation

- **UNIFICATION_PODMAN.md** - Implementation plan and progress tracking
- **podman-test-template.sh** - The actual template file
- **Lab-specific README.md** - Lab instructions and requirements
- **TESTING-GUIDE.md** - Comprehensive testing documentation (per lab)

---

## 🔄 Version History

### Version 2.0 (January 18, 2026)
- ✅ Added multi-mode database support (none/docker-compose/podman-network)
- ✅ Enhanced cleanup for all deployment modes
- ✅ Improved database configuration variables
- ✅ Better error handling and validation
- ✅ Template size: 640 lines

### Version 1.1 (January 18, 2026)
- ✅ Security optimizations (portable shebang, strict mode)
- ✅ Portability improvements (macOS/Linux)
- ✅ Robust error handling
- ✅ Template size: 598 lines

### Version 1.0 (January 15, 2026)
- ✅ Initial unified template
- ✅ 5-phase testing structure
- ✅ Test tracking and reporting
- ✅ Template size: 550 lines

---

**© Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited.**