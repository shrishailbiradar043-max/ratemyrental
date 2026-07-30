# ADR-002: JWT Authentication

## Status
Accepted

## Date
2026-07-30

## Context
We need a stateless authentication mechanism that:
- Works with distributed systems and horizontal scaling
- Doesn't require server-side session storage
- Is widely adopted and industry-standard
- Supports role-based access control
- Can be extended for future multi-tenant scenarios
- Works with mobile and web applications

## Decision
We will use **JWT (JSON Web Tokens)** for authentication and authorization with the following specifications:

### Token Structure
```
Authorization: Bearer <access_token>
```

### Access Token
- **Algorithm**: HS256 (HMAC SHA-256) or RS256 (RSA SHA-256)
- **Expiration**: 1 hour
- **Claims**:
  - `sub`: User ID (subject)
  - `email`: User email
  - `roles`: Array of user roles
  - `iat`: Issued at time
  - `exp`: Expiration time
  - `nbf`: Not before time
  - `jti`: JWT ID (unique identifier)

### Refresh Token
- **Algorithm**: HS256
- **Expiration**: 7 days
- **Stored**: Secure HTTP-only cookie or secure storage
- **Usage**: Exchange for new access token

### Example JWT Payload
```json
{
  "sub": "123e4567-e89b-12d3-a456-426614174000",
  "email": "user@example.com",
  "roles": ["TENANT"],
  "iat": 1626874418,
  "exp": 1626878018,
  "nbf": 1626874418,
  "jti": "jti_unique_id"
}
```

## Implementation Strategy

### 1. Token Generation (Login)
- Validate credentials against database
- Hash password using bcrypt and compare
- Generate access token (1-hour expiration)
- Generate refresh token (7-day expiration)
- Return both tokens

### 2. Token Validation (Request Processing)
- Extract token from Authorization header
- Verify signature with secret key
- Check expiration time
- Validate token claims
- Load user roles and permissions

### 3. Token Refresh
- Accept refresh token
- Validate refresh token
- Generate new access token
- Optionally rotate refresh token

### 4. Token Revocation
- Maintain token blacklist for logout
- Store revoked token JTI in Redis or database
- Check blacklist during token validation
- Auto-cleanup after expiration

### 5. Multi-Factor Authentication (MFA) - Future
- Generate TOTP tokens
- Verify MFA before issuing JWT
- Store MFA status in token

## Rationale

### Advantages
1. **Stateless**: No server-side session storage required
2. **Scalable**: Works across multiple servers and instances
3. **Mobile-Friendly**: Works with native and web apps
4. **Standard**: Industry-standard format and widely supported
5. **Flexible**: Easy to add custom claims
6. **Performance**: No database lookup on every request (after signature validation)
7. **Self-Contained**: All necessary info in the token

### Disadvantages
1. **Token Size**: Larger than session ID
2. **Revocation Complexity**: Requires blacklist for logout
3. **Clock Sync**: Requires synchronized clocks across servers
4. **Token Leakage**: Stolen token is valid until expiration

## Security Measures

### Secret Key Management
- Store secret key in secure vault (AWS Secrets Manager, HashiCorp Vault)
- Rotate keys periodically (quarterly minimum)
- Different keys for development, staging, production
- Never commit secrets to Git

### Token Storage (Client-Side)
- **Web**: HTTP-only cookies (resistant to XSS)
- **Mobile**: Secure storage (Keychain/Keystore)
- **CLI**: Encrypted local file with restricted permissions

### HTTPS Enforcement
- All endpoints require HTTPS/TLS
- Certificate validation on client-side
- HSTS headers enabled

### Token Claims Validation
- Always validate expiration time
- Always validate signature
- Check token hasn't been tampered with
- Validate required claims exist

## Implementation in Spring Boot

### Dependencies
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

### Configuration
```properties
jwt.secret=${JWT_SECRET_KEY}
jwt.access-token-expiration=3600000  # 1 hour in milliseconds
jwt.refresh-token-expiration=604800000  # 7 days in milliseconds
```

## API Endpoints

### Login
```
POST /v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "secure_password"
}

Response:
{
  "status": "success",
  "data": {
    "accessToken": "eyJ0...",
    "refreshToken": "eyJ1...",
    "expiresIn": 3600,
    "user": {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "email": "user@example.com",
      "roles": ["TENANT"]
    }
  }
}
```

### Refresh Token
```
POST /v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJ1..."
}

Response:
{
  "status": "success",
  "data": {
    "accessToken": "eyJ0...",
    "expiresIn": 3600
  }
}
```

### Logout
```
POST /v1/auth/logout
Authorization: Bearer eyJ0...

Response:
{
  "status": "success",
  "message": "Logged out successfully"
}
```

## Testing Strategy

- Unit tests for token generation
- Unit tests for token validation
- Integration tests for login flow
- Integration tests for refresh flow
- Security tests for token tampering
- Performance tests for token validation

## Monitoring

- Track failed authentication attempts
- Alert on unusual login patterns
- Monitor token generation/validation performance
- Track blacklisted tokens size

## Migration Path

If replacing session-based auth:
1. Implement JWT alongside existing sessions
2. Update clients to use JWT
3. Remove session-based code
4. Update session handling libraries

## Related ADRs
- ADR-005: Security Principles
