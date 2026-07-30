# ADR-001: Feature-Based Architecture

## Status
Accepted

## Date
2026-07-30

## Context
We need an architectural approach that supports scalability, maintainability, and clear separation of concerns. The team requires an architecture that:
- Enables parallel development across features
- Allows for independent testing of features
- Supports future microservices migration
- Maintains code clarity and organization
- Facilitates onboarding of new team members

## Decision
We will use a **Feature-Based Module Architecture** with the following structure:

```
ratemyrental/
├── auth/               # Authentication & Authorization feature
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── dto/
│   ├── entity/
│   └── exception/
├── property/           # Property Management feature
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── dto/
│   ├── entity/
│   └── exception/
├── review/             # Review & Rating feature
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── dto/
│   ├── entity/
│   └── exception/
├── user/               # User Management feature
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── dto/
│   ├── entity/
│   └── exception/
├── common/             # Shared utilities
│   ├── exception/      # Global exceptions
│   ├── util/           # Utility classes
│   ├── config/         # Shared configuration
│   └── security/       # Security utilities
├── config/             # Application configuration
└── db/                 # Database migrations
    └── migration/      # Flyway migrations
```

## Rationale

### Advantages
1. **Feature Independence**: Each feature is self-contained with its own layers
2. **Scalability**: Easy to add new features without affecting existing code
3. **Team Parallelization**: Multiple teams can work on different features simultaneously
4. **Code Organization**: Clear structure makes navigation and maintenance easier
5. **Testability**: Features can be tested independently
6. **Microservices Ready**: Features can be extracted into microservices later
7. **Reduced Coupling**: Features communicate through well-defined interfaces

### Disadvantages
1. **Initial Complexity**: Requires more files and structure setup
2. **Learning Curve**: Team needs to understand the pattern
3. **Potential Duplication**: Some code might be duplicated across features

## Implementation Details

### Layer Responsibilities

1. **Controller**: HTTP request handling, parameter validation, routing
2. **Service**: Business logic, orchestration, transaction management
3. **Repository**: Data access, query execution, ORM operations
4. **DTO**: Data Transfer Objects for API contracts
5. **Entity**: JPA entities, database models
6. **Exception**: Feature-specific custom exceptions

### Common Patterns

- Use Spring Boot annotations (@RestController, @Service, @Repository)
- Dependency injection through constructor
- Clear interface definitions between layers
- Unit tests for each layer
- Integration tests for feature workflows

### Communication Between Features

- Services use dependency injection to call other services
- Use DTOs to decouple feature models
- Event-driven communication for loose coupling (future)
- Avoid circular dependencies

## Consequences

- More consistent and maintainable codebase
- Easier for new developers to understand code structure
- Better code reusability within features
- Simpler testing and debugging
- Clear responsibility boundaries
- Reduced cognitive load during development

## Alternatives Considered

1. **Layered Architecture**: Simpler but harder to scale and parallelize
2. **Microservices from Start**: Over-engineered for current scale
3. **Domain-Driven Design**: Too complex for current project phase

## Related ADRs
- ADR-005: Security Principles
