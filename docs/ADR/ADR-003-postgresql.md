# ADR-003: PostgreSQL Database Selection

## Status
Accepted

## Date
2026-07-30

## Context
We need to select a primary database for RateMyRental that:
- Provides ACID compliance for data integrity
- Supports complex queries and relationships
- Scales to handle millions of records
- Supports encryption and security features
- Has a strong ecosystem and community
- Meets compliance requirements (GDPR, CCPA)
- Provides good performance for typical web application workloads
- Supports geospatial queries (for property location searches)

## Decision
We will use **PostgreSQL 14+** as our primary relational database.

## Rationale

### Advantages

1. **ACID Compliance**: Guarantees data consistency and integrity
   - Atomicity: Transactions are all-or-nothing
   - Consistency: Database rules are always enforced
   - Isolation: Concurrent transactions don't interfere
   - Durability: Committed data survives failures

2. **Advanced Features**:
   - JSON/JSONB support for semi-structured data
   - Full-text search capabilities
   - Range types for date/time queries
   - Arrays and composite types
   - Window functions for analytics
   - Common Table Expressions (CTEs)

3. **Geospatial Support**:
   - PostGIS extension for location queries
   - Efficient proximity searches for rental properties
   - Geographic indexing

4. **Scalability**:
   - Table partitioning for large datasets
   - Sharding support via Citus
   - Streaming replication
   - Horizontal scaling ready

5. **Security**:
   - Row-level security (RLS)
   - Column-level security with views
   - Encrypted connections (SSL/TLS)
   - Fine-grained user permissions
   - Password hashing integration

6. **Compliance**:
   - Audit trails and triggers
   - Point-in-time recovery
   - Data masking capabilities
   - GDPR-friendly (Right to be forgotten support)

7. **Performance**:
   - Efficient query planner
   - Sophisticated indexing (B-tree, Hash, GiST, GIN)
   - Query optimization tools (EXPLAIN ANALYZE)
   - Connection pooling support

8. **Developer Experience**:
   - Clear SQL syntax
   - Excellent documentation
   - Large community
   - Many tools and extensions
   - ORM support (Hibernate, JPA)

9. **Open Source**:
   - No licensing costs
   - Full source code available
   - Community-driven development
   - Vendor independence

### Disadvantages

1. **Memory Usage**: Higher memory consumption than SQLite
2. **Operational Complexity**: Requires more configuration and maintenance
3. **Learning Curve**: More complex than simple key-value stores
4. **Scaling Complexity**: Vertical scaling is easier than horizontal

## Alternatives Considered

### 1. MySQL/MariaDB
- **Why Not**: Less advanced features, weaker JSON support, different licensing
- **Suitable For**: Simple applications with basic SQL needs

### 2. MongoDB
- **Why Not**: Lacks ACID compliance, schema flexibility can cause issues, not ideal for relational data
- **Suitable For**: Document-heavy applications with flexible schemas

### 3. SQLite
- **Why Not**: No multi-user support, limited concurrency, not suitable for production scale
- **Suitable For**: Embedded databases, mobile apps, local development

### 4. Oracle Database
- **Why Not**: High cost, complex licensing, over-engineered for our needs
- **Suitable For**: Enterprise applications with complex requirements

## Implementation Details

### Version
- **Minimum**: PostgreSQL 14
- **Recommended**: PostgreSQL 15 LTS (when available)
- **Cloud Hosting**: AWS RDS, Azure Database for PostgreSQL, or DigitalOcean

### Configuration

#### Connection Pool
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

#### Extensions Required
- `uuid-ossp`: UUID generation
- `pgcrypto`: Cryptographic functions
- `postgis`: Geospatial support (future)

#### Performance Tuning
```sql
-- Connection limits
max_connections = 200

-- Memory
shared_buffers = 256MB
effective_cache_size = 1GB
work_mem = 64MB

-- WAL (Write-Ahead Logging)
wal_level = replica
max_wal_senders = 10

-- Autovacuum
autovacuum = on
autovacuum_max_workers = 4
```

### Data Type Choices

| Need | PostgreSQL Type | Rationale |
|------|-----------------|-----------|
| Unique IDs | UUID | Better than sequential, resistant to timing attacks |
| Email | VARCHAR(255) | Adequate for RFC standards |
| Passwords | VARCHAR(255) | For bcrypt hashes |
| Decimals | DECIMAL(10,2) | For currency, no floating-point errors |
| Timestamps | TIMESTAMP WITH TIME ZONE | Handles timezones correctly |
| JSON | JSONB | Supports querying and indexing |
| Large Text | TEXT | No character limit |
| Booleans | BOOLEAN | Native support |
| Ratings | SMALLINT | 1-5 range efficiently stored |

## Backup & Recovery

### Backup Strategy
- **Frequency**: Daily automated backups
- **Retention**: 30 days point-in-time recovery
- **Method**: `pg_dump` with compression
- **Offsite**: Encrypted copies to S3/Cloud Storage

### Recovery
- **RTO**: < 1 hour
- **RPO**: < 1 hour
- **Testing**: Quarterly restore drills

## Migration Strategy

### From Development to Production
1. Same schema versioning (Flyway)
2. Same configuration management
3. Same backup strategy
4. Load testing before cutover

## Monitoring & Maintenance

### Key Metrics
- Query execution time (p50, p95, p99)
- Active connections
- Cache hit ratio
- Transaction rollback rate
- Disk I/O and space usage
- Replication lag (if replicated)

### Regular Maintenance
- Weekly: ANALYZE and VACUUM
- Monthly: REINDEX large tables
- Quarterly: Full database ANALYZE
- Annually: Performance tuning review

## Compliance & Security

### Data Protection
- Encrypted connections (SSL/TLS)
- Encrypted backups
- Row-level security for sensitive data
- Regular security updates

### Compliance
- GDPR: Support for data deletion and portability
- CCPA: Audit logging for data access
- SOC 2: Backup and recovery procedures
- PCI DSS: When handling payment data

## Cost Considerations

### Self-Hosted
- Hardware: $100-500/month (server)
- Maintenance: Internal team time
- Backup storage: $50-200/month

### Cloud (AWS RDS, Azure, etc.)
- db.t3.micro: ~$20-30/month
- db.t3.small: ~$50-70/month
- db.t3.medium: ~$100-150/month
- Backup storage: ~$1 per GB/month

## Timeline

- **Phase 1**: Set up PostgreSQL locally (Week 1)
- **Phase 2**: Create schema and migrations (Week 1-2)
- **Phase 3**: Connect application (Week 2)
- **Phase 4**: Staging environment (Week 3)
- **Phase 5**: Production deployment (Week 4)

## Related ADRs
- ADR-004: Flyway Database Migrations
- ADR-005: Security Principles
