package com.ratemyrental.common.entity;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing Configuration.
 *
 * Purpose:
 * Enables Spring Data JPA auditing globally for all entities.
 * This configuration automatically populates @CreatedDate and @LastModifiedDate fields.
 *
 * How It Works:
 * 1. @EnableJpaAuditing activates the AuditingEntityListener.
 * 2. When an entity is saved/merged, the listener intercepts the persistence event.
 * 3. If the entity has @CreatedDate field, it's set to current Instant.
 * 4. If the entity has @LastModifiedDate field, it's updated to current Instant.
 * 5. This happens automatically, transparently, in all repositories.
 *
 * Requirement:
 * - Entity must have @EntityListeners(AuditingEntityListener.class) OR
 * - Globally enabled via auditorAwareRef (optional, for tracking who modified what)
 *
 * Since we use @MappedSuperclass with @CreatedDate/@LastModifiedDate in BaseEntity,
 * all child entities automatically inherit this behavior.
 *
 * Benefits:
 * - No manual timestamp management
 * - Testable and mockable
 * - Centralized configuration
 * - Works with repository save(), saveAll(), merge(), etc.
 * - Does NOT work with direct SQL updates (use database triggers for that)
 *
 * @see org.springframework.data.jpa.repository.config.EnableJpaAuditing
 * @see org.springframework.data.jpa.domain.support.AuditingEntityListener
 * @see org.springframework.data.annotation.CreatedDate
 * @see org.springframework.data.annotation.LastModifiedDate
 */
@Configuration
@EnableJpaAuditing
public class AuditingConfiguration {
    // No beans needed here; @EnableJpaAuditing does all the work
}
