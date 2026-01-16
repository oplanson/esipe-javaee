#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Lab 05B - JMS Banking Application - Local Testing Script
# This script builds and tests the application locally using Liberty Maven Plugin

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test counters
TESTS_RUN=0
TESTS_PASSED=0
TESTS_FAILED=0

echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   Lab 05B - JMS Banking Application - Local Testing       ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Function to print test result
print_test_result() {
    local test_name=$1
    local result=$2
    TESTS_RUN=$((TESTS_RUN + 1))
    
    if [ "$result" -eq 0 ]; then
        echo -e "${GREEN}✓ PASS${NC} - $test_name"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        echo -e "${RED}✗ FAIL${NC} - $test_name"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
echo -e "${YELLOW}Checking prerequisites...${NC}"

if ! command_exists mvn; then
    echo -e "${RED}Error: Maven is not installed${NC}"
    exit 1
fi

if ! command_exists java; then
    echo -e "${RED}Error: Java is not installed${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Maven found: $(mvn -version | head -n 1)${NC}"
echo -e "${GREEN}✓ Java found: $(java -version 2>&1 | head -n 1)${NC}"
echo ""

# Navigate to solution directory
cd "$(dirname "$0")/solution"

# Clean previous builds
echo -e "${YELLOW}Cleaning previous builds...${NC}"
mvn clean > /dev/null 2>&1
echo -e "${GREEN}✓ Clean complete${NC}"
echo ""

# Compile the application
echo -e "${YELLOW}Compiling application...${NC}"
if mvn compile -q; then
    print_test_result "Compilation" 0
else
    print_test_result "Compilation" 1
    echo -e "${RED}Compilation failed. Exiting.${NC}"
    exit 1
fi
echo ""

# Run tests (if any)
echo -e "${YELLOW}Running unit tests...${NC}"
if mvn test -q; then
    print_test_result "Unit Tests" 0
else
    print_test_result "Unit Tests" 1
fi
echo ""

# Package the application
echo -e "${YELLOW}Packaging application...${NC}"
if mvn package -DskipTests -q; then
    print_test_result "Packaging (WAR creation)" 0
else
    print_test_result "Packaging (WAR creation)" 1
    echo -e "${RED}Packaging failed. Exiting.${NC}"
    exit 1
fi
echo ""

# Verify WAR file exists
echo -e "${YELLOW}Verifying build artifacts...${NC}"
if [ -f "target/banking-jms-app.war" ]; then
    WAR_SIZE=$(du -h target/banking-jms-app.war | cut -f1)
    print_test_result "WAR file exists (Size: $WAR_SIZE)" 0
else
    print_test_result "WAR file exists" 1
fi
echo ""

# Check for required classes in WAR
echo -e "${YELLOW}Verifying JMS components in WAR...${NC}"

# Extract WAR to temp directory for inspection
TEMP_DIR=$(mktemp -d)
unzip -q target/banking-jms-app.war -d "$TEMP_DIR"

# Check for event classes
if [ -f "$TEMP_DIR/WEB-INF/classes/com/bank/event/TransactionEvent.class" ]; then
    print_test_result "TransactionEvent class" 0
else
    print_test_result "TransactionEvent class" 1
fi

# Check for producer classes
if [ -f "$TEMP_DIR/WEB-INF/classes/com/bank/producer/TransactionEventProducer.class" ]; then
    print_test_result "TransactionEventProducer class" 0
else
    print_test_result "TransactionEventProducer class" 1
fi

# Check for MDB classes
if [ -f "$TEMP_DIR/WEB-INF/classes/com/bank/mdb/EmailNotificationMDB.class" ]; then
    print_test_result "EmailNotificationMDB class" 0
else
    print_test_result "EmailNotificationMDB class" 1
fi

if [ -f "$TEMP_DIR/WEB-INF/classes/com/bank/mdb/AuditLoggingMDB.class" ]; then
    print_test_result "AuditLoggingMDB class" 0
else
    print_test_result "AuditLoggingMDB class" 1
fi

if [ -f "$TEMP_DIR/WEB-INF/classes/com/bank/mdb/DeadLetterQueueMDB.class" ]; then
    print_test_result "DeadLetterQueueMDB class" 0
else
    print_test_result "DeadLetterQueueMDB class" 1
fi

if [ -f "$TEMP_DIR/WEB-INF/classes/com/bank/mdb/TransactionEventMDB.class" ]; then
    print_test_result "TransactionEventMDB class" 0
else
    print_test_result "TransactionEventMDB class" 1
fi

# Check for model classes
if [ -f "$TEMP_DIR/WEB-INF/classes/com/bank/model/AuditLog.class" ]; then
    print_test_result "AuditLog entity" 0
else
    print_test_result "AuditLog entity" 1
fi

if [ -f "$TEMP_DIR/WEB-INF/classes/com/bank/model/FailedMessage.class" ]; then
    print_test_result "FailedMessage entity" 0
else
    print_test_result "FailedMessage entity" 1
fi

# Check for service classes
if [ -f "$TEMP_DIR/WEB-INF/classes/com/bank/service/EmailService.class" ]; then
    print_test_result "EmailService class" 0
else
    print_test_result "EmailService class" 1
fi

# Check for servlet
if [ -f "$TEMP_DIR/WEB-INF/classes/com/bank/web/MessagingTestServlet.class" ]; then
    print_test_result "MessagingTestServlet class" 0
else
    print_test_result "MessagingTestServlet class" 1
fi

# Clean up temp directory
rm -rf "$TEMP_DIR"

echo ""

# Check configuration files
echo -e "${YELLOW}Verifying configuration files...${NC}"

if [ -f "src/main/liberty/config/server.xml" ]; then
    # Check for JMS configuration in server.xml
    if grep -q "messaging-3.1" src/main/liberty/config/server.xml; then
        print_test_result "JMS feature in server.xml" 0
    else
        print_test_result "JMS feature in server.xml" 1
    fi
    
    if grep -q "jms/connectionFactory" src/main/liberty/config/server.xml; then
        print_test_result "JMS ConnectionFactory configured" 0
    else
        print_test_result "JMS ConnectionFactory configured" 1
    fi
    
    if grep -q "jms/transactionQueue" src/main/liberty/config/server.xml; then
        print_test_result "Transaction Queue configured" 0
    else
        print_test_result "Transaction Queue configured" 1
    fi
    
    if grep -q "jms/auditTopic" src/main/liberty/config/server.xml; then
        print_test_result "Audit Topic configured" 0
    else
        print_test_result "Audit Topic configured" 1
    fi
fi

if [ -f "src/main/resources/META-INF/persistence.xml" ]; then
    print_test_result "persistence.xml exists" 0
else
    print_test_result "persistence.xml exists" 1
fi

echo ""

# Print summary
echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║                     TEST SUMMARY                           ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo -e "Total Tests: ${TESTS_RUN}"
echo -e "${GREEN}Passed: ${TESTS_PASSED}${NC}"
if [ $TESTS_FAILED -gt 0 ]; then
    echo -e "${RED}Failed: ${TESTS_FAILED}${NC}"
else
    echo -e "Failed: ${TESTS_FAILED}"
fi
echo ""

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║              ✓ ALL TESTS PASSED!                           ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${YELLOW}Next steps:${NC}"
    echo "1. Start PostgreSQL database"
    echo "2. Run: mvn liberty:dev"
    echo "3. Access: http://localhost:9080"
    echo "4. Test messaging: http://localhost:9080/test-messaging"
    echo ""
    exit 0
else
    echo -e "${RED}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${RED}║              ✗ SOME TESTS FAILED                           ║${NC}"
    echo -e "${RED}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    exit 1
fi

# Made with Bob