# Database Documentation

## Overview
RateMyRental uses PostgreSQL as its primary database with Flyway for schema versioning and migrations.

## Database Technology Stack

- **Database**: PostgreSQL 14+
- **Migration Tool**: Flyway
- **ORM**: JPA/Hibernate
- **Connection Pooling**: HikariCP
- **Backup Strategy**: Daily snapshots, point-in-time recovery

See [ADR-003](./ADR/ADR-003-postgresql.md) for technology selection rationale.

## Database Design Principles

1. **ACID Compliance**: All transactions maintain integrity
2. **Normalization**: 3NF minimum for data consistency
3. **Scalability**: Partition strategy for large tables
4. **Security**: Encrypted sensitive data, role-based access
5. **Performance**: Proper indexing, query optimization
6. **Maintainability**: Clear naming conventions, documentation

## Schema Overview

### Core Tables

#### users
```sql
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  role VARCHAR(20) NOT NULL DEFAULT 'TENANT', -- ADMIN, LANDLORD, TENANT
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, SUSPENDED, DELETED
  email_verified BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP NULL,
  INDEX idx_email (email),
  INDEX idx_username (username)
);
```

#### properties
```sql
CREATE TABLE properties (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  owner_id UUID NOT NULL REFERENCES users(id),
  name VARCHAR(255) NOT NULL,
  address VARCHAR(255) NOT NULL,
  city VARCHAR(100) NOT NULL,
  state VARCHAR(50) NOT NULL,
  zip_code VARCHAR(10),
  country VARCHAR(100),
  latitude DECIMAL(10, 8),
  longitude DECIMAL(11, 8),
  type VARCHAR(50) NOT NULL, -- apartment, house, condo, etc.
  bedrooms INT,
  bathrooms INT,
  square_feet INT,
  price_per_month DECIMAL(10, 2),
  description TEXT,
  amenities TEXT[],
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_owner_id (owner_id),
  INDEX idx_city (city),
  INDEX idx_coordinates (latitude, longitude)
);
```

#### reviews
```sql
CREATE TABLE reviews (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  property_id UUID NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
  reviewer_id UUID NOT NULL REFERENCES users(id),
  rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
  title VARCHAR(200) NOT NULL,
  content TEXT NOT NULL,
  cleanliness_rating INT CHECK (cleanliness_rating >= 1 AND cleanliness_rating <= 5),
  safety_rating INT CHECK (safety_rating >= 1 AND safety_rating <= 5),
  maintenance_rating INT CHECK (maintenance_rating >= 1 AND maintenance_rating <= 5),
  communication_rating INT CHECK (communication_rating >= 1 AND communication_rating <= 5),
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
  helpful_count INT DEFAULT 0,
  unhelpful_count INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(property_id, reviewer_id),
  INDEX idx_property_id (property_id),
  INDEX idx_reviewer_id (reviewer_id),
  INDEX idx_status (status)
);
```

#### audit_log
```sql
CREATE TABLE audit_log (
  id BIGSERIAL PRIMARY KEY,
  user_id UUID REFERENCES users(id),
  entity_type VARCHAR(100) NOT NULL,
  entity_id UUID NOT NULL,
  action VARCHAR(50) NOT NULL, -- CREATE, UPDATE, DELETE
  changes JSONB,
  ip_address VARCHAR(45),
  user_agent TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user_id (user_id),
  INDEX idx_entity (entity_type, entity_id),
  INDEX idx_created_at (created_at)
);
```

## Migration Strategy

See [ADR-004](./ADR/ADR-004-flyway.md) for detailed migration approach.

### Migration Naming
```
V<version>__<description>.sql
V1__Initial_schema.sql
V2__Add_users_table.sql
V3__Add_reviews_table.sql
```

### Migration Location
```
src/main/resources/db/migration/
```

## Indexing Strategy

### Primary Indexes
- UUID primary keys on all tables
- Foreign key indexes for referential integrity
- Unique constraints where applicable

### Performance Indexes
- B-tree indexes on frequently filtered columns (city, status)
- Composite indexes for common query patterns
- GIST indexes for geographic data (latitude, longitude)
- Regular index analysis and maintenance

## Backup & Recovery

### Backup Strategy
- Daily automated snapshots
- Point-in-time recovery (last 30 days)
- Weekly encrypted backup archives
- Backup testing quarterly

### Recovery Procedures
- RTO (Recovery Time Objective): 1 hour
- RPO (Recovery Point Objective): 1 hour
- Documented recovery procedures
- Regular disaster recovery drills

## Performance Optimization

### Query Optimization
- Use EXPLAIN ANALYZE for complex queries
- Avoid N+1 query problems (use JOINs)
- Connection pooling (HikariCP, pool size: 10-20)
- Query result caching (Redis, future)

### Monitoring
- Query performance metrics
- Slow query log monitoring
- Index usage analysis
- Connection pool statistics

## Data Retention

| Data Type | Retention Period |
|-----------|-----------------|
| Active User Data | Indefinite (until deletion) |
| Deleted User Data | 90 days |
| Audit Logs | 2 years |
| Session Logs | 30 days |
| Error Logs | 90 days |

## Security

### Database Access
- Principle of least privilege for DB users
- Separate users for read/write operations
- Encrypted connections (SSL/TLS)
- Network isolation (VPC, firewall rules)

### Data Protection
- Encrypted password storage (bcrypt)
- Encrypted sensitive fields at rest
- No PII in application logs
- Regular security audits

### Compliance
- GDPR right to erasure support
- CCPA data portability
- SOC 2 compliance (future)
- Regular access reviews

## Connection Management

### Connection Pool Configuration
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
```

## Monitoring & Maintenance

### Regular Maintenance Tasks
- Weekly: Analyze tables, vacuum
- Monthly: Reindex large tables
- Quarterly: Database health audit
- Annually: Capacity planning review

### Monitoring Metrics
- Query execution time (p50, p95, p99)
- Connection pool utilization
- Disk space usage
- Cache hit ratio (future)
- Transaction rollback rate
