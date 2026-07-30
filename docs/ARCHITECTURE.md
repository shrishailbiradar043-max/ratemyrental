# Architecture Overview

## System Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Client Layer                          │
│          (Web App / Mobile App / CLI)                    │
└────────────────────┬────────────────────────────────────┘
                     │ HTTPS/REST
┌────────────────────▼────────────────────────────────────┐
│              API Gateway & Load Balancer                 │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│            Microservices / Feature Modules               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Auth Svc   │  │ Property Svc │  │  Review Svc  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│          Data Access & Business Logic Layer              │
│                 (Repository Pattern)                     │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│          Data Layer (PostgreSQL Database)                │
│        With Flyway Migration Management                  │
└─────────────────────────────────────────────────────────┘
```

## Technology Stack

| Layer | Technology | Rationale |
|-------|-----------|-----------|
| **Language** | Java | Enterprise-grade, robust ecosystem |
| **Framework** | Spring Boot | Production-ready, excellent community |
| **Database** | PostgreSQL | ACID compliance, rich features, scalability |
| **Migration** | Flyway | Version control for schema, rollback support |
| **Authentication** | JWT | Stateless, scalable, standard |
| **API Style** | REST | Widely adopted, simple, cacheable |
| **Build Tool** | Maven/Gradle | Dependency management, build automation |
| **Testing** | JUnit 5, Mockito | Industry standard, excellent coverage |
| **Logging** | SLF4J, Logback | Structured logging, performance |
| **Security** | Spring Security | Comprehensive authentication & authorization |

## Module Structure

```
ratemyrental/
├── auth/              # Authentication & Authorization
├── property/          # Property Management
├── review/            # Reviews & Ratings
├── user/              # User Management
├── common/            # Shared utilities & exceptions
├── config/            # Global configuration
└── db/
    └── migration/     # Flyway SQL migrations
```

## Design Patterns

- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic encapsulation
- **Dependency Injection**: Loose coupling, testability
- **DTO (Data Transfer Object)**: API contract definition
- **Exception Handling**: Centralized error management

## API Design

- **Versioning**: URL-based (e.g., `/v1/properties`)
- **Error Responses**: Standard HTTP status codes + error details
- **Pagination**: Offset/limit for list endpoints
- **Rate Limiting**: Token bucket algorithm
- **CORS**: Whitelist-based configuration

## Security Architecture

- HTTPS enforcement
- CORS with restricted origins
- CSRF protection for state-changing operations
- SQL injection prevention (parameterized queries)
- XSS protection via output encoding
- Authentication via JWT tokens
- Role-based access control (RBAC)

## Scalability Considerations

- Stateless API design (no server sessions)
- Database connection pooling
- Horizontal scaling ready
- Caching layer (Redis - future)
- Message queue for async operations (Kafka/RabbitMQ - future)

See [ADR-001](./ADR/ADR-001-feature-based-architecture.md) for detailed architecture decisions.
