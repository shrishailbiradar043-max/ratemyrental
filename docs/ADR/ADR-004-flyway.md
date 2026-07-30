# ADR-004: Flyway Database Migrations

## Status
Accepted

## Date
2026-07-30

## Context
We need a way to manage database schema changes across development, staging, and production environments that:
- Maintains version control for schema
- Ensures consistency across environments
- Allows rollback capabilities where possible
- Integrates with CI/CD pipelines
- Tracks which migrations have been applied
- Prevents concurrent migration conflicts
- Provides clear migration history
- Works with PostgreSQL

## Decision
We will use **Flyway** for database schema versioning and migrations.

## Rationale

### Advantages

1. **Version Control**:
   - Schema changes tracked in Git
   - Clear history of all modifications
   - Ability to review schema evolution

2. **Consistency**:
   - Same migrations run across all environments
   - Guaranteed order of execution
   - Idempotent operations

3. **CI/CD Integration**:
   - Automatic migration on application startup
   - Can be run separately in deployment pipeline
   - Supports build server integration

4. **Simplicity**:
   - Plain SQL migrations (easy to understand)
   - Minimal configuration required
   - No complex DSL to learn

5. **Spring Boot Integration**:
   - Auto-configuration in Spring Boot
   - Zero additional configuration in many cases
   - Bean-based migration callbacks

6. **Safety**:
   - Checksum validation (detects tampered migrations)
   - Transaction per migration (ACID)
   - Automatic baseline for existing databases

7. **Flexibility**:
   - Supports both SQL and Java migrations
   - Custom naming schemes
   - Callbacks for pre/post migration hooks

8. **Community**:
   - Large user base
   - Good documentation
   - Active maintenance

### Disadvantages

1. **Limited Rollback**: No automatic rollback (intentional design decision)
2. **Java Migrations**: Can add complexity for complex logic
3. **Learning Curve**: Team needs to learn Flyway conventions
4. **Migration Dependencies**: Can't skip migrations

## Alternatives Considered

### 1. Liquibase
- **Why Not**: More complex, XML/YAML overhead, steeper learning curve
- **Suitable For**: Complex enterprise scenarios with custom rollback needs

### 2. Manual SQL Scripts
- **Why Not**: Error-prone, difficult to track, no consistency guarantees
- **Suitable For**: Very small projects only

### 3. Hibernate Auto DDL
- **Why Not**: Not for production use, can cause data loss, hard to track
- **Suitable For**: Development only, not recommended for production

## Implementation Details

### Maven Dependency

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>9.22.3</version>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
    <version>9.22.3</version>
</dependency>
```

### Application Configuration

```properties
# application.properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baselineOnMigrate=true
spring.flyway.validateOnMigrate=true
spring.flyway.cleanDisabled=true
spring.flyway.outOfOrder=false
spring.flyway.placeholderReplacement=true
spring.flyway.placeholders.placeholderName=placeholderValue
```

### Directory Structure

```
src/main/resources/db/
├── migration/
│   ├── V1__Initial_schema.sql
│   ├── V2__Create_users_table.sql
│   ├── V3__Create_properties_table.sql
│   ├── V4__Create_reviews_table.sql
│   └── U4__Undo_reviews_table.sql  (undo migration)
```

### Naming Convention

**Versioned Migrations**
```
V<version>__<description>.sql
V1__Initial_schema.sql
V2__Add_users_table.sql
V3__Add_properties_table.sql
V4__Add_review_ratings.sql
```

**Undo Migrations** (Optional, for rollback)
```
U<version>__<description>.sql
U2__Undo_add_users_table.sql
```

**Repeatable Migrations** (Run every time if checksum changes)
```
R__<description>.sql
R__Create_views.sql
R__Grant_permissions.sql
```

### Version Numbering

- **Format**: Numeric (1, 2, 3) or Decimal (1.1, 1.2, 2.0)
- **Increment**: Sequential, no gaps
- **Recommendations**:
  - Start at 1
  - Increment by 1
  - Don't skip versions
  - One feature per migration usually

## Migration Guidelines

### Best Practices

1. **Atomic Changes**:
   - One logical change per migration
   - Small, focused migrations
   - Easier to debug if issues occur

2. **Naming**:
   - Descriptive names (use underscores for spaces)
   - Reflect the change being made
   - Example: `V3__Add_phone_to_users.sql`

3. **SQL Style**:
   - Use standard SQL where possible
   - Include comments for complex logic
   - Use consistent formatting
   - Add CASCADE only when intentional

4. **Backward Compatibility**:
   - Don't break existing code
   - Add columns as NULL initially
   - Provide default values
   - Test with current code before migration

5. **Performance**:
   - Be cautious with large table alterations
   - Consider downtime implications
   - Test migrations on production-like data
   - Profile slow migrations

6. **Testing**:
   - Run against test database first
   - Verify data integrity after migration
   - Test rollback scenarios
   - Document manual steps if needed

### Example Migrations

**V1__Initial_schema.sql**
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_email ON users(email);
```

**V2__Add_properties_table.sql**
```sql
CREATE TABLE properties (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_owner_id ON properties(owner_id);
```

**V3__Add_phone_to_users.sql**
```sql
ALTER TABLE users ADD COLUMN phone VARCHAR(20);

-- Migration complete
```

**R__Create_search_view.sql**
```sql
DROP VIEW IF EXISTS property_search_view CASCADE;

CREATE VIEW property_search_view AS
SELECT p.id, p.name, u.email as owner_email
FROM properties p
JOIN users u ON p.owner_id = u.id
WHERE p.status = 'ACTIVE';
```

## Running Migrations

### Automatic (Application Startup)
Migrations run automatically when Spring Boot application starts.

```properties
spring.flyway.enabled=true
```

### Manual via CLI
```bash
mvn flyway:migrate
mvn flyway:validate
mvn flyway:info
mvn flyway:clean  # DANGER: Drops all database objects
```

### Gradle
```bash
./gradlew flywayMigrate
./gradlew flywayValidate
./gradlew flywayInfo
```

## Monitoring & Maintenance

### Flyway Metadata Table
Flyway creates `flyway_schema_history` table to track applied migrations:

```sql
SELECT * FROM flyway_schema_history;
```

Columns:
- `installed_rank`: Execution order
- `version`: Migration version
- `description`: Migration description
- `type`: SQL, JDBC
- `script`: SQL file name
- `checksum`: File integrity check
- `installed_by`: User who executed
- `installed_on`: Execution timestamp
- `execution_time`: Execution duration (ms)
- `success`: Whether migration succeeded

### Common Issues

1. **Checksum Mismatch**:
   - Migration file was modified after execution
   - Solution: Don't modify applied migrations
   - If necessary: Delete from metadata and re-apply

2. **Migration Failure**:
   - Partial migration execution
   - Solution: Fix issue and run `flyway:repair`
   - Then retry with corrected migration

3. **Out of Order**:
   - Migration inserted between applied migrations
   - Solution: Use `spring.flyway.outOfOrder=true` (use cautiously)

## Deployment Strategy

### Development
- Auto-migrate on startup
- Migrations tracked in Git
- Test all migrations

### Staging
- Run migrations before deployment
- Validate against production-like data
- Monitor migration execution time
- Plan for actual production

### Production
- Run migrations in maintenance window
- Have rollback plan (manual SQL prepared)
- Monitor database performance after
- Keep detailed execution logs
- Team notified of migration start/completion

## Rollback Strategy

### Undo Migrations
```sql
-- U2__Undo_add_users_table.sql
DROP TABLE users;
```

### Manual Rollback
Since Flyway doesn't auto-rollback:
1. Keep undo migrations prepared
2. Have manual SQL rollback scripts
3. Notify team of rollback
4. Update flyway_schema_history if using manual rollback

## Versioning Across Branches

### Development Workflow
```
main branch: V1, V2, V3
feature-branch: V4 (created locally)
  - After code review, merged to main
  - Applied in order with other V4+ migrations
```

### Conflict Resolution
- Use sequential versioning
- Don't skip versions
- Coordinate between team members
- Consider PR workflow for migrations

## Monitoring

### Key Metrics
- Migration execution time
- Number of migrations per deployment
- Migration success rate
- Rollback frequency (rare!)

## Related ADRs
- ADR-003: PostgreSQL Database Selection
- ADR-005: Security Principles
