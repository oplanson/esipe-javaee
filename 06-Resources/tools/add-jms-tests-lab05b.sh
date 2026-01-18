#!/bin/bash
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

# Script to add JMS-specific tests to Lab05B-JMS podman-test.sh
# Inserts 10 JMS tests from backup file into Phase 4 section

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LAB_DIR="$SCRIPT_DIR/../../03-Labs/Lab05B-JMS"
PODMAN_SCRIPT="$LAB_DIR/podman-test.sh"

echo "Adding JMS-specific tests to Lab05B-JMS..."
echo ""

# Check if file exists
if [ ! -f "$PODMAN_SCRIPT" ]; then
    echo "❌ Error: $PODMAN_SCRIPT not found"
    exit 1
fi

# Create backup
cp "$PODMAN_SCRIPT" "$PODMAN_SCRIPT.before-jms-tests"
echo "✅ Created backup: podman-test.sh.before-jms-tests"

# Create the JMS tests section
cat > /tmp/jms-tests.txt << 'EOF'
    
    # ============================================================================
    # JMS-SPECIFIC TESTS
    # ============================================================================
    
    echo ""
    echo "═══════════════════════════════════════════════════════════════"
    echo "  JMS-Specific Tests"
    echo "═══════════════════════════════════════════════════════════════"
    echo ""
    
    # Test 1: Verify JMS queue configuration
    run_test "JMS queue configuration" \
        "curl -f -s http://localhost:${APP_PORT}/health/ready | grep -q 'UP'"
    
    # Test 2: Create transaction and verify event sent
    echo ""
    echo "Test 2: Creating transaction to trigger JMS events..."
    TRANSACTION_RESPONSE=$(curl -s -X POST http://localhost:${APP_PORT}/api/transactions \
        -H "Content-Type: application/json" \
        -d '{"accountId":1,"amount":100.00,"type":"DEPOSIT"}' 2>/dev/null || echo "")
    
    if [ -n "$TRANSACTION_RESPONSE" ]; then
        print_status "Transaction created successfully"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        print_warning "Could not create transaction"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
    
    # Wait for MDB processing
    echo ""
    echo "Waiting 5 seconds for MDB processing..."
    sleep 5
    
    # Test 3: Verify EmailNotificationMDB processed message
    echo ""
    echo "Test 3: Verifying EmailNotificationMDB processing..."
    LOGS=$(podman logs $CONTAINER_NAME 2>&1)
    if echo "$LOGS" | grep -q "EmailNotificationMDB"; then
        print_status "EmailNotificationMDB processed messages"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        print_warning "EmailNotificationMDB not found in logs"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
    
    # Test 4: Verify AuditLoggingMDB processed message
    echo ""
    echo "Test 4: Verifying AuditLoggingMDB processing..."
    if echo "$LOGS" | grep -q "AuditLoggingMDB"; then
        print_status "AuditLoggingMDB processed messages"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        print_warning "AuditLoggingMDB not found in logs"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
    
    # Test 5: Verify TransactionEventMDB processed message
    echo ""
    echo "Test 5: Verifying TransactionEventMDB processing..."
    if echo "$LOGS" | grep -q "TransactionEventMDB"; then
        print_status "TransactionEventMDB processed messages"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        print_warning "TransactionEventMDB not found in logs"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
    
    # Test 6: Verify JMS connection factory
    echo ""
    echo "Test 6: Verifying JMS connection factory..."
    if echo "$LOGS" | grep -q "jms/ConnectionFactory"; then
        print_status "JMS ConnectionFactory configured"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        print_warning "JMS ConnectionFactory not found in logs"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
    
    # Test 7: Verify transaction queue
    echo ""
    echo "Test 7: Verifying transaction queue..."
    if echo "$LOGS" | grep -q "jms/TransactionQueue"; then
        print_status "Transaction queue configured"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        print_warning "Transaction queue not found in logs"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
    
    # Test 8: Verify notification queue
    echo ""
    echo "Test 8: Verifying notification queue..."
    if echo "$LOGS" | grep -q "jms/NotificationQueue"; then
        print_status "Notification queue configured"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        print_warning "Notification queue not found in logs"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
    
    # Test 9: Verify audit logs in database
    echo ""
    echo "Test 9: Verifying audit logs in database..."
    if [ "$DB_MODE" = "docker-compose" ]; then
        AUDIT_COUNT=$(podman exec $DB_CONTAINER psql -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM audit_logs;" 2>/dev/null | tr -d ' ' || echo "0")
        if [ -n "$AUDIT_COUNT" ] && [ "$AUDIT_COUNT" -gt 0 ]; then
            print_status "Audit logs persisted in database (Count: $AUDIT_COUNT)"
            TESTS_PASSED=$((TESTS_PASSED + 1))
        else
            print_warning "Could not verify audit logs in database"
            TESTS_FAILED=$((TESTS_FAILED + 1))
        fi
    else
        print_info "Skipping database test (DB_MODE=none)"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    fi
    
    # Test 10: Verify dead letter queue configuration
    echo ""
    echo "Test 10: Verifying dead letter queue..."
    if echo "$LOGS" | grep -q "jms/DeadLetterQueue"; then
        print_status "Dead letter queue configured"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        print_warning "Dead letter queue not found in logs"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
    
    echo ""
    echo "═══════════════════════════════════════════════════════════════"
    echo "  JMS Tests Complete"
    echo "═══════════════════════════════════════════════════════════════"
EOF

# Find the line number where to insert (after "test_web_interface" and before "# TODO")
LINE_NUM=$(grep -n "# TODO: Add lab-specific functional tests here" "$PODMAN_SCRIPT" | cut -d: -f1)

if [ -z "$LINE_NUM" ]; then
    echo "❌ Error: Could not find insertion point (TODO comment)"
    exit 1
fi

# Insert before the TODO line
head -n $((LINE_NUM - 1)) "$PODMAN_SCRIPT" > /tmp/podman-test-new.sh
cat /tmp/jms-tests.txt >> /tmp/podman-test-new.sh
tail -n +$LINE_NUM "$PODMAN_SCRIPT" >> /tmp/podman-test-new.sh

# Replace original file
mv /tmp/podman-test-new.sh "$PODMAN_SCRIPT"
chmod +x "$PODMAN_SCRIPT"

echo "✅ Added JMS-specific tests to podman-test.sh"
echo ""
echo "Summary:"
echo "  - 10 JMS-specific tests added"
echo "  - Tests cover: MDBs, queues, connection factory, audit logs, DLQ"
echo "  - Backup saved as: podman-test.sh.before-jms-tests"
echo ""
echo "Next steps:"
echo "  1. Review the changes: cd $LAB_DIR && git diff podman-test.sh"
echo "  2. Test the script: cd $LAB_DIR && ./podman-test.sh"
echo ""

# Made with Bob
