# Security Guidelines

## Overview
Security is a first-class citizen in RateMyRental. All systems must follow these guidelines to protect user data and maintain system integrity.

## Authentication & Authorization

### Authentication
- JWT tokens with HS256 or RS256 signing
- Token expiration: 1 hour access token, 7 days refresh token
- Secure token storage (HTTP-only cookies or secure storage)
- Multi-factor authentication (MFA) support - future enhancement

### Authorization
- Role-based access control (RBAC)
- Roles: Admin, Landlord, Tenant, Reviewer
- Fine-grained permissions per endpoint
- Attribute-based access control (ABAC) - future enhancement

See [ADR-002](./ADR/ADR-002-jwt-authentication.md) for JWT implementation details.

## Data Protection

### Encryption
- **In Transit**: TLS 1.2+ for all HTTP/HTTPS
- **At Rest**: AES-256 for sensitive data in database
- Encryption keys managed via secure vaults (AWS Secrets Manager, HashiCorp Vault)
- Never log sensitive data (passwords, tokens, PII)

### Password Security
- Minimum 12 characters, complexity requirements
- Bcrypt with salt for password hashing (cost factor ≥ 12)
- Never store plaintext passwords
- Implement password reset flow securely

### Data Privacy
- GDPR compliance (Right to be forgotten, Data portability)
- CCPA compliance (Opt-out, Data disclosure)
- Personally Identifiable Information (PII) handling
- Data retention policies per data type
- Regular privacy impact assessments

## API Security

### Input Validation
- Whitelist validation for all inputs
- Type checking and format validation
- Length constraints on strings
- Reject unexpected parameters
- Array size limits (prevent billion laughs attack)

### Output Encoding
- HTML encode for web contexts
- URL encode for URLs
- JSON encode for JSON responses
- Prevent XSS vulnerabilities

### SQL Injection Prevention
- Always use parameterized queries
- No string concatenation for SQL
- ORM frameworks preferred (JPA/Hibernate)
- Regular SQL injection security testing

## Network Security

### HTTPS/TLS
- Enforce HTTPS for all endpoints
- TLS 1.2 or higher
- Strong cipher suites only
- HSTS headers enabled

### CORS
- Restrict to trusted origins only
- Whitelist specific domains
- No wildcard (*) in production
- Validate preflight requests

### Rate Limiting & DDoS Protection
- Per-IP rate limiting (100 req/min default)
- Per-user rate limiting for authenticated endpoints
- Exponential backoff for failed auth attempts
- WAF (Web Application Firewall) for DDoS

## Secrets Management

### Credential Storage
- Use environment variables for secrets
- Never commit secrets to Git
- Use `.env.example` for documentation only
- Regular rotation of API keys & tokens
- Separate credentials per environment (dev, staging, prod)

### External Services
- API keys stored in secure vaults
- Credentials not hardcoded in source
- Automatic secret scanning in CI/CD

## Auditing & Logging

### Security Logging
- Log all authentication attempts
- Log authorization failures
- Log sensitive operations (user deletion, role changes)
- Never log passwords or PII
- Structured logging in JSON format

### Monitoring
- Real-time alerting for suspicious activity
- Failed login threshold alerts
- Unusual access pattern detection
- Regular review of audit logs

## Vulnerability Management

### Security Testing
- Static Application Security Testing (SAST)
- Dynamic Application Security Testing (DAST)
- Dependency scanning for known vulnerabilities
- Regular penetration testing
- Code security reviews before production

### Patch Management
- Monitor for framework & library updates
- Rapid patching for critical vulnerabilities
- Security advisory subscriptions
- Regular dependency updates (monthly minimum)

## Compliance

### Standards & Frameworks
- OWASP Top 10 compliance
- CWE Top 25 mitigation
- SOC 2 Type II (future)
- PCI DSS for payment data (if applicable)
- Industry-specific regulations (GDPR, CCPA)

### Regular Audits
- Quarterly security reviews
- Annual penetration testing
- Compliance audits
- Vulnerability assessments

## Incident Response

### Incident Plan
- Designated incident response team
- Clear escalation procedures
- Communication protocol for breaches
- 72-hour notification requirement (GDPR)
- Post-incident reviews & improvements

## Developer Security Practices

- Secure coding training for all developers
- Code review with security focus
- Pre-commit hooks for secret scanning
- Secure development lifecycle (SDLC)
- Principle of least privilege
- Defense in depth strategy

See [ADR-005](./ADR/ADR-005-security-principles.md) for detailed security architecture decisions.
