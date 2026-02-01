<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# JMS ObjectMessage Deserialization Vulnerability - Security Fix

## Executive Summary

**Vulnerability:** Java Deserialization Attack via JMS ObjectMessage  
**Severity:** CRITICAL (CVSS 9.8)  
**Status:** ✅ FIXED  
**Fix Date:** 2026-02-01  

## Vulnerability Details

### Original Issue

The application used JMS `ObjectMessage` with Java serialization to transmit `TransactionEvent` objects between message producers and consumers. This created a critical security vulnerability:

**Location:** `DeadLetterQueueMDB.java:122-124`

```java
// VULNERABLE CODE (REMOVED)
} else if (message instanceof ObjectMessage) {
    Object obj = ((ObjectMessage) message).getObject();  // ⚠️ DANGEROUS!
    return obj != null ? obj.toString() : "null";
```

### Attack Vector

1. **Deserialization Gadget Chains:** Attacker crafts malicious serialized objects using libraries like Apache Commons Collections, Spring Framework, etc.
2. **Remote Code Execution:** When `getObject()` is called, the JVM deserializes the payload, triggering method invocations that execute arbitrary code
3. **Privilege Escalation:** Code runs with application server privileges, potentially accessing databases, file systems, and network resources

### Real-World Impact

- **CVE-2015-7501** (JBoss): RCE via Java deserialization
- **CVE-2015-4852** (WebLogic): RCE via T3 protocol deserialization
- **CVE-2016-0638** (Oracle): Multiple deserialization vulnerabilities

## Solution Implemented

### Approach: JSON Serialization with TextMessage

We replaced Java serialization with JSON serialization using Jakarta JSON-B (JSON Binding), transmitting messages as `TextMessage` instead of `ObjectMessage`.

### Changes Made

#### 1. Created JSON Utility Class

**File:** `src/main/java/com/bank/util/JsonMessageUtil.java`

```java
public class JsonMessageUtil {
    private static final Jsonb jsonb = JsonbBuilder.create();
    
    public static String toJson(Object object) {
        return jsonb.toJson(object);
    }
    
    public static <T> T fromJson(String json, Class<T> type) {
        return jsonb.fromJson(json, type);
    }
}
```

**Benefits:**
- ✅ No deserialization vulnerabilities
- ✅ Type-safe with explicit class specification
- ✅ Human-readable for debugging
- ✅ Language-agnostic (interoperable with non-Java services)

#### 2. Updated TransactionEvent

**File:** `src/main/java/com/bank/event/TransactionEvent.java`

**Changes:**
- ❌ Removed `implements Serializable`
- ❌ Removed `serialVersionUID`
- ✅ Added security documentation

#### 3. Updated Message Producer

**File:** `src/main/java/com/bank/producer/TransactionEventProducer.java`

**Before:**
```java
ObjectMessage message = context.createObjectMessage(event);
```

**After:**
```java
String jsonPayload = JsonMessageUtil.toJson(event);
TextMessage message = context.createTextMessage(jsonPayload);
message.setStringProperty("messageFormat", "JSON");
```

#### 4. Updated Message Consumers (MDBs)

**Files Updated:**
- `TransactionEventMDB.java`
- `AuditLoggingMDB.java`
- `EmailNotificationMDB.java`
- `DeadLetterQueueMDB.java` (Critical fix)

**Before:**
```java
ObjectMessage objectMessage = (ObjectMessage) message;
Object payload = objectMessage.getObject();  // ⚠️ VULNERABLE
TransactionEvent event = (TransactionEvent) payload;
```

**After:**
```java
TextMessage textMessage = (TextMessage) message;
String jsonPayload = textMessage.getText();
TransactionEvent event = JsonMessageUtil.fromJson(jsonPayload, TransactionEvent.class);
```

#### 5. Special Handling in DeadLetterQueueMDB

The Dead Letter Queue handler now safely handles legacy ObjectMessages without deserializing them:

```java
} else if (message instanceof ObjectMessage) {
    // SECURITY: Do NOT call getObject() - it triggers deserialization!
    logger.warning("SECURITY: ObjectMessage detected in DLQ. " +
                 "This message type is deprecated due to deserialization vulnerabilities.");
    return "[ObjectMessage - Content not extracted for security reasons]";
```

## Security Benefits

### Before (Vulnerable)

| Aspect | Status |
|--------|--------|
| Deserialization Attacks | ❌ Vulnerable |
| Remote Code Execution | ❌ Possible |
| Gadget Chain Exploits | ❌ Exploitable |
| Message Inspection | ❌ Binary format |
| Cross-Language Support | ❌ Java only |

### After (Secure)

| Aspect | Status |
|--------|--------|
| Deserialization Attacks | ✅ Eliminated |
| Remote Code Execution | ✅ Prevented |
| Gadget Chain Exploits | ✅ Not applicable |
| Message Inspection | ✅ Human-readable JSON |
| Cross-Language Support | ✅ Any language |

## Testing & Validation

### Unit Tests Required

1. **JSON Serialization Tests**
   - Verify TransactionEvent serializes to valid JSON
   - Verify deserialization produces correct objects
   - Test null handling and edge cases

2. **Message Producer Tests**
   - Verify TextMessage creation with JSON payload
   - Verify message properties are set correctly
   - Test error handling

3. **Message Consumer Tests**
   - Verify JSON deserialization in MDBs
   - Test invalid JSON handling
   - Verify legacy ObjectMessage rejection

### Integration Tests

1. **End-to-End Message Flow**
   - Send transaction events through all queues/topics
   - Verify correct processing in all MDBs
   - Confirm audit logs and email notifications work

2. **Dead Letter Queue Testing**
   - Simulate message failures
   - Verify DLQ handler processes TextMessages safely
   - Confirm ObjectMessages are logged but not deserialized

### Security Tests

1. **Penetration Testing**
   - Attempt to send malicious ObjectMessages
   - Verify they are rejected or safely handled
   - Confirm no deserialization occurs

2. **Code Review**
   - Search codebase for `ObjectMessage.getObject()` calls
   - Verify no remaining deserialization vulnerabilities
   - Review all JMS message handling code

## Migration Guide

### For Existing Deployments

1. **Deploy Updated Code**
   - Deploy new version with JSON serialization
   - Both TextMessage and ObjectMessage are handled during transition

2. **Monitor Logs**
   - Watch for ObjectMessage warnings in logs
   - Identify any legacy message producers

3. **Update All Producers**
   - Ensure all message producers use new JSON format
   - Update any external systems sending messages

4. **Remove Legacy Support**
   - After transition period, remove ObjectMessage handling
   - Update message selectors to require `messageFormat='JSON'`

## Compliance & Standards

This fix addresses requirements from:

- **OWASP Top 10 2021:** A08:2021 – Software and Data Integrity Failures
- **CWE-502:** Deserialization of Untrusted Data
- **NIST SP 800-53:** SI-10 (Information Input Validation)
- **PCI DSS 4.0:** Requirement 6.2.4 (Secure Coding)

## References

- [OWASP Deserialization Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Deserialization_Cheat_Sheet.html)
- [Jakarta JSON Binding Specification](https://jakarta.ee/specifications/jsonb/)
- [JMS 3.1 Specification](https://jakarta.ee/specifications/messaging/)
- [CVE-2015-7501 Details](https://nvd.nist.gov/vuln/detail/CVE-2015-7501)

## Contact

For security concerns or questions about this fix:
- Security Team: security@bank.com
- Development Lead: Olivier Planson

---

**Document Version:** 1.0  
**Last Updated:** 2026-02-01  
**Classification:** Internal Use Only