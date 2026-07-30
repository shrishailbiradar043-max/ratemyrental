# API Guidelines

## Overview
This document defines standards for all REST APIs in RateMyRental to ensure consistency, maintainability, and developer experience.

## API Standards

### Base URL
```
https://api.ratemyrental.com/v1
```

### Versioning
- Version in URL path: `/v1`, `/v2`, etc.
- Backward compatibility for at least 1 major version
- Deprecation warnings in response headers
- Migration guide provided for breaking changes

## Request Format

### Headers
```http
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>
Accept: application/json
X-Request-ID: <UUID>  # For tracing
```

### Method Conventions
| Method | Use Case | Idempotent |
|--------|----------|-----------|
| GET | Retrieve resource(s) | Yes |
| POST | Create new resource | No |
| PUT | Full resource update | Yes |
| PATCH | Partial resource update | No |
| DELETE | Remove resource | Yes |

### Body Examples

**Create Request (POST)**
```json
{
  "name": "Apartment 42",
  "address": "123 Main St, City",
  "bedrooms": 2,
  "bathrooms": 1,
  "type": "apartment"
}
```

**Update Request (PATCH)**
```json
{
  "name": "Updated Name"
}
```

## Response Format

### Success Response (200, 201, 204)
```json
{
  "status": "success",
  "code": 200,
  "data": {
    "id": "prop_123abc",
    "name": "Apartment 42",
    "createdAt": "2026-07-30T10:35:18Z",
    "updatedAt": "2026-07-30T10:35:18Z"
  },
  "meta": {
    "requestId": "req_abc123",
    "timestamp": "2026-07-30T10:35:18Z"
  }
}
```

### Error Response
```json
{
  "status": "error",
  "code": 400,
  "error": {
    "type": "VALIDATION_ERROR",
    "message": "Invalid input parameters",
    "details": [
      {
        "field": "bedrooms",
        "message": "Must be a positive integer",
        "code": "INVALID_TYPE"
      }
    ]
  },
  "meta": {
    "requestId": "req_abc123",
    "timestamp": "2026-07-30T10:35:18Z"
  }
}
```

### Paginated Response
```json
{
  "status": "success",
  "code": 200,
  "data": [
    { "id": "prop_1", "name": "Apartment A" },
    { "id": "prop_2", "name": "Apartment B" }
  ],
  "pagination": {
    "page": 1,
    "pageSize": 20,
    "total": 150,
    "hasMore": true,
    "nextPage": 2
  },
  "meta": {
    "requestId": "req_abc123",
    "timestamp": "2026-07-30T10:35:18Z"
  }
}
```

## HTTP Status Codes

| Code | Meaning | Use Case |
|------|---------|----------|
| 200 | OK | Successful GET, PUT, PATCH |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE or empty response |
| 400 | Bad Request | Invalid input, validation errors |
| 401 | Unauthorized | Missing/invalid authentication |
| 403 | Forbidden | Authenticated but lacks permission |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Resource already exists (duplicate) |
| 429 | Too Many Requests | Rate limit exceeded |
| 500 | Internal Server Error | Unexpected server error |
| 503 | Service Unavailable | Server maintenance/overload |

## Pagination

### Query Parameters
```
GET /properties?page=1&pageSize=20&sort=createdAt:desc
```

- `page`: 1-based page number (default: 1)
- `pageSize`: Items per page (default: 20, max: 100)
- `sort`: Sort field and direction (field:asc or field:desc)

### Offset-Limit Alternative
```
GET /properties?offset=0&limit=20
```

## Filtering & Searching

### Filter Operators
```
GET /properties?bedrooms=2&type=apartment&minPrice=500&maxPrice=2000
GET /properties?search=downtown%20apartment
GET /properties?filters[location]=downtown&filters[rating][min]=4
```

### Date Range Filtering
```
GET /reviews?createdFrom=2026-01-01&createdTo=2026-12-31
GET /reviews?updatedSince=2026-07-01T00:00:00Z
```

## Authentication

### JWT Token
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Refresh
```
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "..."
}
```

## Rate Limiting

### Headers
```http
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 45
X-RateLimit-Reset: 1627478400
```

### Limits
- Public endpoints: 60 req/min per IP
- Authenticated endpoints: 300 req/min per user
- Search endpoints: 30 req/min per user

## Error Codes

### Authentication Errors
- `AUTH_INVALID_TOKEN`: Token malformed or expired
- `AUTH_MISSING_CREDENTIALS`: No authentication provided
- `AUTH_INSUFFICIENT_PERMISSIONS`: User lacks required role

### Validation Errors
- `VALIDATION_ERROR`: Invalid input format
- `VALIDATION_REQUIRED_FIELD`: Required field missing
- `VALIDATION_CONSTRAINT_VIOLATION`: Business logic violation

### Resource Errors
- `NOT_FOUND`: Resource doesn't exist
- `ALREADY_EXISTS`: Resource already exists
- `CONFLICT`: State conflict

## API Documentation

- OpenAPI/Swagger specifications for all endpoints
- Example requests and responses
- Authentication requirements
- Rate limiting information
- Error scenarios

## Backward Compatibility

- Never remove fields from responses
- Only add optional fields to requests
- Never change existing field meanings
- Provide deprecation warnings 6 months in advance
- Maintain previous API versions for 12 months

## Testing Requirements

- Unit tests for all endpoints (100% coverage)
- Integration tests for workflows
- API contract tests
- Load testing for performance baselines
- Security testing (injection, XSS, CSRF)

## Documentation Requirements

- Update API documentation with changes
- Document all breaking changes
- Provide migration guides
- Include example cURL commands
- Document authentication requirements
