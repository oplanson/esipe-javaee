
<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->





---
marp: true
theme: default
paginate: true
backgroundColor: #fff
header: 'Jakarta EE & MicroProfile Course'
footer: 'Lecture 9: Jakarta EE Security | © 2026 Olivier Planson - All rights reserved. Reproduction prohibited.'
style: |
  section {
    font-size: 22px;
    padding: 40px 60px;
  }
  img {
    max-width: 85%;
    max-height: 380px;
    display: block;
    margin: 10px auto;
  }
  pre {
    font-size: 0.65em;
    margin: 10px 0;
    padding: 10px;
  }
  code {
    font-size: 0.7em;
  }
  ul, ol {
    font-size: 0.85em;
    line-height: 1.8;
    margin: 8px 0;
  }
  li {
    margin: 6px 0;
    line-height: 1.8;
  }
  li::marker {
    flex-shrink: 0;
  }
  h1 {
    font-size: 1.8em;
    margin-bottom: 20px;
    line-height: 1.3;
    display: flex;
    align-items: center;
    gap: 10px;
    flex-wrap: nowrap;
    white-space: nowrap;
  }
  h2 {
    font-size: 1.3em;
    margin-bottom: 15px;
    line-height: 1.3;
  }
  h3 {
    font-size: 1.1em;
    margin-bottom: 10px;
  }
  table {
    font-size: 0.75em;
    margin: 10px auto;
  }
  .columns {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
  .columns-3 {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 15px;
  }
  .small-text {
    font-size: 0.75em;
  }
  .highlight {
    background-color: #fff3cd;
    padding: 2px 6px;
    border-radius: 3px;
  }
  .success {
    color: #28a745;
    font-weight: bold;
  }
  .warning {
    color: #ffc107;
    font-weight: bold;
  }
  .danger {
    color: #dc3545;
    font-weight: bold;
  }
  .info {
    color: #17a2b8;
    font-weight: bold;
  }
  blockquote {
    border-left: 4px solid #007bff;
    padding-left: 15px;
    margin: 15px 0;
    font-style: italic;
    background-color: #f8f9fa;
    padding: 10px 15px;
  }
  .center {
    text-align: center;
  }
  .emoji {
    font-size: 1.5em;
  }
  footer {
    font-size: 0.7em;
  }
  .columns-2-1 {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 20px;
  }
  .columns-1-2 {
    display: grid;
    grid-template-columns: 1fr 2fr;
    gap: 20px;
  }

---

# 🔐 Lecture 9: Jakarta EE Security

**Enterprise Application Security**

---

## 📋 Lecture Overview

**Duration:** 3 hours 20 minutes (200 minutes)

**Topics Covered:**
- Introduction to Security (15 min)
- Authentication vs Authorization (20 min)
- Jakarta Security API (30 min)
- Declarative Security (25 min)
- Programmatic Security (20 min)
- JWT and Token-Based Auth (30 min)
- OAuth2 and OpenID Connect (25 min)
- Security Best Practices (20 min)
- Microservices Security (15 min)

---

## 🎯 Learning Objectives

By the end of this lecture, you will be able to:

1. **Understand** security fundamentals (authentication, authorization, encryption)
2. **Implement** Jakarta Security API in enterprise applications
3. **Apply** declarative and programmatic security
4. **Secure** REST APIs with JWT tokens
5. **Integrate** OAuth2 and OpenID Connect
6. **Follow** security best practices
7. **Secure** microservices architectures

---

# Part 1: Introduction to Security
## 15 minutes

---

## Why Security Matters

**Security is NOT optional** in enterprise applications:

- **Data Protection**: Customer data, financial information, PII
- **Compliance**: GDPR, HIPAA, PCI-DSS, SOX
- **Business Continuity**: Prevent data breaches and downtime
- **Trust**: Customer confidence and brand reputation
- **Legal**: Avoid lawsuits and penalties

> "Security should be built in, not bolted on"

---

## Security Principles (CIA Triad)

<div class="columns-3">
<div>

### 🔒 Confidentiality
- Data accessible only to authorized users
- Encryption at rest and in transit
- Access controls

</div>
<div>

### ✅ Integrity
- Data accuracy and consistency
- Prevent unauthorized modifications
- Digital signatures

</div>
<div>

### 🌐 Availability
- Systems accessible when needed
- DDoS protection
- Redundancy and failover

</div>
</div>

---

## Security Layers

<details>
<summary>📝 Original Mermaid Code (click to expand)</summary>

```mermaid
graph TB
    A["Application Security<br/>- Authentication<br/>- Authorization<br/>- Input Validation"]
    B["Transport Security<br/>- HTTPS/TLS<br/>- Certificate Management"]
    C["Infrastructure Security<br/>- Firewalls<br/>- Network Segmentation"]
    A --> B
    B --> C
    style A fill:#667eea
    style B fill:#4facfe
    style C fill:#43e97b
```

</details>

![width:70%](images/09-jakarta-ee-security-diagram-1.png)


---

## Common Security Threats

| Threat | Description | Mitigation |
|--------|-------------|------------|
| **SQL Injection** | Malicious SQL in user input | Prepared statements, ORM |
| **XSS** | Inject malicious scripts | Input sanitization, CSP |
| **CSRF** | Unauthorized commands | CSRF tokens, SameSite cookies |
| **Session Hijacking** | Steal session tokens | Secure cookies, HTTPS |
| **Brute Force** | Password guessing | Rate limiting, account lockout |
| **Man-in-the-Middle** | Intercept communications | TLS/SSL, certificate pinning |

---

## OWASP Top 10 (2021)

1. **Broken Access Control**
2. **Cryptographic Failures**
3. **Injection**
4. **Insecure Design**
5. **Security Misconfiguration**
6. **Vulnerable Components**
7. **Authentication Failures**
8. **Software and Data Integrity Failures**
9. **Security Logging Failures**
10. **Server-Side Request Forgery (SSRF)**

---

# Part 2: Authentication vs Authorization
## 20 minutes

---

## Authentication vs Authorization

<div class="columns">
<div>

### 🔑 Authentication
**"Who are you?"**

- Verify user identity
- Credentials validation
- Multi-factor authentication
- Session management

**Example:**
Login with username/password

</div>
<div>

### 🛡️ Authorization
**"What can you do?"**

- Access control
- Role-based permissions
- Resource protection
- Policy enforcement

**Example:**
Admin can delete users

</div>
</div>

---

## Authentication Flow

<details>
<summary>📝 Original Mermaid Code (click to expand)</summary>

```mermaid
sequenceDiagram
    participant Client
    participant Server
    Client->>Server: 1. Login Request (username, password)
    Note over Server: 2. Validate Credentials
    Server-->>Client: 3. Session Token/JWT
    Client->>Server: 4. Authenticated Request (with token)
    Server-->>Client: 5. Response
```

</details>

![width:70%](images/09-jakarta-ee-security-diagram-2.png)


---

## Authentication Methods

### 1. **Basic Authentication**
```http
Authorization: Basic dXNlcjpwYXNzd29yZA==
```
- Simple but insecure (Base64 encoding)
- Must use HTTPS
- No logout mechanism

### 2. **Form-Based Authentication**
- HTML form with username/password
- Server-side session management
- Cookie-based sessions

---

## Authentication Methods (continued)

### 3. **Token-Based Authentication**
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```
- Stateless (no server-side sessions)
- JWT (JSON Web Tokens)
- Scalable for microservices

### 4. **Certificate-Based Authentication**
- Client SSL/TLS certificates
- Mutual TLS (mTLS)
- Strong authentication

---

## Multi-Factor Authentication (MFA)

**Something you know** + **Something you have** + **Something you are**

<div class="columns-3">
<div>

### Knowledge
- Password
- PIN
- Security questions

</div>
<div>

### Possession
- SMS code
- Authenticator app
- Hardware token

</div>
<div>

### Inherence
- Fingerprint
- Face recognition
- Iris scan

</div>
</div>

---

## Authorization Models

### 1. **Role-Based Access Control (RBAC)**
```java
@RolesAllowed({"ADMIN", "MANAGER"})
public void deleteUser(Long userId) { }
```

### 2. **Attribute-Based Access Control (ABAC)**
```java
if (user.getDepartment().equals("HR") && 
    user.getLevel() >= 3) {
    // Allow access
}
```

---

## Authorization Models (continued)

### 3. **Permission-Based Access Control**
```java
@RequiresPermissions("user:delete")
public void deleteUser(Long userId) { }
```

### 4. **Policy-Based Access Control**
```java
@PreAuthorize("hasRole('ADMIN') or 
              (hasRole('MANAGER') and #userId == principal.id)")
public void updateUser(Long userId) { }
```

---

## Summary

### Key Takeaways

1. **Authentication** verifies identity, **Authorization** controls access
2. Use **Jakarta Security API** for modern, portable security
3. **Never store plain-text passwords** - use strong hashing
4. **IdentityStore** validates credentials and retrieves roles
5. **JWT** provides stateless authentication for APIs
6. **Always validate input** and encode output
7. **Use HTTPS** for all production traffic
8. **Implement audit logging** for security events
9. **Follow OWASP guidelines** to prevent vulnerabilities
10. **Regular security testing** is essential

---

## Resources

### Official Documentation

- **Jakarta Security Specification**
  https://jakarta.ee/specifications/security/

- **Jakarta EE Tutorial - Security**
  https://eclipse-ee4j.github.io/jakartaee-tutorial/

- **Open Liberty Security**
  https://openliberty.io/docs/latest/security.html

### Security Resources

- **OWASP Top 10**
  https://owasp.org/www-project-top-ten/

- **OWASP Cheat Sheet Series**
  https://cheatsheetseries.owasp.org/

---

## Next Steps

### Lab 9: Secure Banking Application

In the next lab, you will:

1. Implement database-backed authentication
2. Create custom IdentityStore
3. Configure role-based authorization
4. Implement JWT authentication
5. Secure REST endpoints
6. Add security audit logging
7. Test security features

**Get ready to build a production-ready secure application!**

---

## Questions?

**Thank you for your attention!**

Contact: olivier.planson@univ-eiffel.fr

---

<!-- End of Lecture 9 -->
<!-- © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
