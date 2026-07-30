# ADR-005: Security Principles

## Status
Accepted

## Date
2026-07-30

## Context
We need to establish fundamental security principles and practices that guide all development decisions at RateMyRental. These principles should:
- Protect user data and privacy
- Prevent common vulnerabilities (OWASP Top 10)
- Comply with regulations (GDPR, CCPA)
- Be practical and implementable
- Guide architectural decisions
- Create a security culture
- Enable secure development practices
- Facilitate threat modeling and risk assessment

## Decision
We adopt the following **Security Principles** as foundational to all development at RateMyRental:

### 1. Defense in Depth
- Multiple layers of security controls
- No single point of failure
- Security at application, network, and infrastructure levels
- Example: Input validation + output encoding + CSP headers + WAF

### 2. Principle of Least Privilege
- Users, processes, and systems have minimum required access
- Granular role-based access control (RBAC)
- Default deny, explicit allow
- Regular access reviews and cleanup
- Example: Admin users only for critical operations

### 3. Secure by Default
- Security features enabled by default
- Safe defaults for all configurations
- Developers opt-in to less secure options (with warnings)
- Secure error messages
- Example: HTTPS required, CSRF protection on

### 4. Fail Securely
- Systems fail in a safe state
- No data exposure on errors
- Graceful degradation
- Clear error handling
- Example: Authentication fails closed, not open

### 5. Never Trust User Input
- All input is potentially malicious
- Validate at boundaries (API, database)
- Whitelist validation preferred over blacklist
- Encode output for context
- Example: SQL parameterized queries, HTML encoding

### 6. Separation of Concerns
- Security logic separate from business logic
- Clear responsibility boundaries
- Consistent security checks across application
- Reusable security components
- Example: Centralized authentication, authorization layers

### 7. Keep Security Simple
- Avoid complex custom security
- Use proven, standard libraries
- Well-understood patterns
- Easier to audit and maintain
- Example: Use Spring Security, JJWT, bcrypt (not custom crypto)

### 8. Transparency & Visibility
- Comprehensive logging of security events
- Audit trails for sensitive operations
- Monitoring and alerting for anomalies
- Regular security reviews
- Example: Log failed auth attempts, data access

### 9. Defense Against Common Threats
- Active mitigation of OWASP Top 10
- Regular vulnerability scanning
- Secure coding practices
- Dependency management
- Example: SQL injection prevention, XSS protection

### 10. Security is Everyone's Responsibility
- Security training for all developers
- Security reviews in code review process
- Threat modeling for new features
- Incident response procedures
- Example: Security checklist before deployment

## Detailed Implementations

### Authentication & Authorization
- JWT tokens with short expiration (1 hour)
- Refresh tokens for extended sessions (7 days)
- Bcrypt password hashing (cost factor ≥ 12)
- Role-based access control (RBAC)
- See [ADR-002](./ADR-002-jwt-authentication.md) for details

### Data Protection
- HTTPS/TLS 1.2+ for all communications
- Encrypted storage for sensitive data (AES-256)
- Secure key management (vault/secrets manager)
- No plaintext passwords or tokens in logs
- Data classification and handling

### Input Validation & Output Encoding
```java
// Input: Whitelist validation
String username = request.getParameter("username");
if (!username.matches("^[a-zA-Z0-9_]{3,20}$")) {
    throw new ValidationException("Invalid username");
}

// Output: Context-aware encoding
String displayName = StringEscapeUtils.escapeHtml4(user.getName());
```

### SQL Injection Prevention
```java
// Good: Parameterized queries
String query = "SELECT * FROM users WHERE email = ?";
PreparedStatement stmt = conn.prepareStatement(query);
stmt.setString(1, email);

// Bad: String concatenation
String query = "SELECT * FROM users WHERE email = '" + email + "'";
```

### Cross-Site Scripting (XSS) Prevention
```java
// Encode user input in HTML context
String userContent = encoder.encodeForHTML(input);

// Use Content Security Policy headers
response.setHeader("Content-Security-Policy", "default-src 'self'");
```

### Cross-Site Request Forgery (CSRF) Protection
```java
// Spring Security provides automatic CSRF protection
// Ensure state-changing operations require CSRF token
POST /v1/properties
X-CSRF-Token: <token>
```

### Error Handling
```java
try {
    // Process request
} catch (DatabaseException e) {
    // Log full exception details for debugging
    logger.error("Database error details: ", e);
    
    // Return generic error to user
    return ApiResponse.error("An error occurred. Please try again.");
}
```

### Rate Limiting & DDoS Protection
```properties
# Prevent brute force attacks
auth.max-attempts=5
auth.lockout-duration=15m

# API rate limiting
rate-limit.public=60/min per IP
rate-limit.authenticated=300/min per user
```

### Dependency Management
```xml
<!-- Keep dependencies updated -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot</artifactId>
    <version>3.1.0</version>
</dependency>
```

### Secrets Management
```bash
# Never commit secrets
echo ".env" >> .gitignore

# Use environment variables or vault
export DATABASE_PASSWORD=$(aws secretsmanager get-secret-value ...)

# Rotate secrets regularly (quarterly minimum)
```

### Security Testing
- Static Application Security Testing (SAST)
- Dynamic Application Security Testing (DAST)
- Dependency scanning
- Penetration testing
- Threat modeling

### Logging & Monitoring
```java
// Log security events
logger.warn("Failed login attempt for user: {}", username);
logger.info("User role changed: {} -> {}", oldRole, newRole);
logger.error("Unauthorized access attempt: {} tried to access {}", userId, resource);

// Never log sensitive data
// BAD: logger.info("User login: {} with password: {}", email, password);
```

## Threat Modeling

For each new feature:
1. Identify assets (user data, system resources)
2. Identify threats (unauthorized access, data leaks)
3. Identify vulnerabilities (missing validation, weak crypto)
4. Implement mitigations (strong controls)
5. Test and validate

## Security Checklist

### Before Deployment
- [ ] Input validation implemented
- [ ] Output encoding implemented
- [ ] Authentication required where needed
- [ ] Authorization checks in place
- [ ] No hardcoded secrets
- [ ] Sensitive data encrypted
- [ ] Error messages generic
- [ ] Logging comprehensive
- [ ] Rate limiting enabled
- [ ] Security tests passed
- [ ] Dependency vulnerabilities scanned
- [ ] Code review with security focus
- [ ] Penetration test passed (for critical features)

### Before Code Commit
- [ ] No credentials in code
- [ ] No passwords or keys in comments
- [ ] Security tests passing
- [ ] Dependency versions current
- [ ] No known vulnerabilities

## Compliance & Standards

### Standards Followed
- OWASP Top 10 mitigation
- CWE Top 25 prevention
- NIST Cybersecurity Framework
- GDPR data protection
- CCPA privacy rights

### Regular Activities
- Monthly: Dependency scanning
- Quarterly: Security audit
- Semi-annually: Penetration testing
- Annually: Compliance review

## Incident Response

### Upon Security Incident
1. **Identify**: Determine scope and severity
2. **Contain**: Prevent further damage
3. **Eradicate**: Remove the threat
4. **Recover**: Restore systems
5. **Communicate**: Notify stakeholders (72-hour GDPR requirement)
6. **Learn**: Post-incident review

### Emergency Contacts
- Security Lead: [Contact]
- Incident Response Team: [Contact]
- Legal/Compliance: [Contact]

## Training & Awareness

- Quarterly security training for all developers
- OWASP Top 10 understanding required
- Secure coding practices review
- Security incident case studies

## Consequences

- Stronger security posture
- Better compliance with regulations
- Reduced vulnerability risk
- Faster incident response
- More confident users

## Alternatives Considered

1. **No explicit security framework**: Risk of inconsistent practices
2. **Complex proprietary framework**: Difficult to maintain and teach
3. **Overly restrictive framework**: May impede development

## Related ADRs
- ADR-002: JWT Authentication
- ADR-003: PostgreSQL Database Selection
- ADR-001: Feature-Based Architecture

## References
- OWASP Top 10: https://owasp.org/www-project-top-ten/
- NIST Cybersecurity Framework: https://www.nist.gov/cyberframework/
- CWE Top 25: https://cwe.mitre.org/top25/
