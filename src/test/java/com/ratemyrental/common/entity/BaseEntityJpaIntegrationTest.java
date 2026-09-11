package com.ratemyrental.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for BaseEntity with Spring Data JPA auditing.
 *
 * Test Coverage:
 * 1. Spring Data JPA auditing automatically sets @CreatedDate and @LastModifiedDate
 * 2. Timestamps are in UTC (Instant type)
 * 3. Auditing works across save and update operations
 * 4. UUID is generated before persistence
 *
 * This test requires:
 * - Spring Boot test context
 * - In-memory database (H2 configured in test resources)
 * - AuditingConfiguration to be loaded
 *
 * Notes:
 * - Uses @DataJpaTest for fast, isolated JPA tests
 * - Does NOT load full ApplicationContext (faster)
 * - Creates a temporary test entity for this test only
 */
@DataJpaTest
@Import(AuditingConfiguration.class)
@DisplayName("BaseEntity JPA Integration Tests")
class BaseEntityJpaIntegrationTest {

    /**
     * Test entity for JPA auditing verification.
     * Only exists in tests; not part of production model.
     */
    @Entity
    @Table(name = "test_entities")
    static class TestJpaEntity extends BaseEntity {
        private String name;

        public TestJpaEntity() {
            super();
        }

        public TestJpaEntity(String name) {
            super();
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * Repository for test entity.
     * Allows us to verify JPA auditing during save/update operations.
     */
    @Repository
    interface TestJpaEntityRepository extends JpaRepository<TestJpaEntity, Long> {
    }

    @Autowired
    private TestJpaEntityRepository repository;

    // ==================== UUID Generation Tests ====================

    @Test
    @DisplayName("UUID is generated before persistence")
    void testUuidIsGeneratedBeforePersistence() {
        TestJpaEntity entity = new TestJpaEntity("Test Entity");
        var uuidBeforeSave = entity.getUuid();

        assertThat(uuidBeforeSave).isNotNull();

        TestJpaEntity savedEntity = repository.save(entity);

        assertThat(savedEntity.getUuid()).isEqualTo(uuidBeforeSave);
    }

    // ==================== Created Timestamp Tests ====================

    @Test
    @DisplayName("@CreatedDate is populated on first save")
    void testCreatedDateIsPopulatedOnSave() {
        TestJpaEntity entity = new TestJpaEntity("Test Entity");
        Instant beforeSave = Instant.now();

        TestJpaEntity savedEntity = repository.save(entity);

        Instant afterSave = Instant.now();

        assertThat(savedEntity.getCreatedAt()).isNotNull();
        assertThat(savedEntity.getCreatedAt()).isAfterOrEqualTo(beforeSave);
        assertThat(savedEntity.getCreatedAt()).isBeforeOrEqualTo(afterSave);
    }

    @Test
    @DisplayName("@CreatedDate remains immutable after update")
    void testCreatedDateIsImmutableAfterUpdate() {
        TestJpaEntity entity = new TestJpaEntity("Original Name");
        TestJpaEntity savedEntity = repository.save(entity);
        Instant originalCreatedAt = savedEntity.getCreatedAt();

        sleep(100); // Small delay to ensure timestamps differ
        savedEntity.setName("Updated Name");
        TestJpaEntity updatedEntity = repository.save(savedEntity);

        assertThat(updatedEntity.getCreatedAt()).isEqualTo(originalCreatedAt);
    }

    // ==================== Updated Timestamp Tests ====================

    @Test
    @DisplayName("@LastModifiedDate is populated on first save")
    void testLastModifiedDateIsPopulatedOnSave() {
        TestJpaEntity entity = new TestJpaEntity("Test Entity");
        Instant beforeSave = Instant.now();

        TestJpaEntity savedEntity = repository.save(entity);

        Instant afterSave = Instant.now();

        assertThat(savedEntity.getUpdatedAt()).isNotNull();
        assertThat(savedEntity.getUpdatedAt()).isAfterOrEqualTo(beforeSave);
        assertThat(savedEntity.getUpdatedAt()).isBeforeOrEqualTo(afterSave);
    }

    @Test
    @DisplayName("@LastModifiedDate equals @CreatedDate on initial save")
    void testLastModifiedDateEqualsCreatedDateOnInitialSave() {
        TestJpaEntity entity = new TestJpaEntity("Test Entity");
        TestJpaEntity savedEntity = repository.save(entity);

        assertThat(savedEntity.getUpdatedAt()).isEqualTo(savedEntity.getCreatedAt());
    }

    @Test
    @DisplayName("@LastModifiedDate is updated on entity update")
    void testLastModifiedDateIsUpdatedOnEntityUpdate() {
        TestJpaEntity entity = new TestJpaEntity("Original Name");
        TestJpaEntity savedEntity = repository.save(entity);
        Instant originalUpdatedAt = savedEntity.getUpdatedAt();

        sleep(100); // Ensure time passes

        savedEntity.setName("Updated Name");
        Instant beforeUpdate = Instant.now();
        TestJpaEntity updatedEntity = repository.save(savedEntity);
        Instant afterUpdate = Instant.now();

        assertThat(updatedEntity.getUpdatedAt()).isAfter(originalUpdatedAt);
        assertThat(updatedEntity.getUpdatedAt()).isAfterOrEqualTo(beforeUpdate);
        assertThat(updatedEntity.getUpdatedAt()).isBeforeOrEqualTo(afterUpdate);
    }

    // ==================== ID Generation Tests ====================

    @Test
    @DisplayName("ID is generated by PostgreSQL on persistence")
    void testIdIsGeneratedOnPersistence() {
        TestJpaEntity entity = new TestJpaEntity("Test Entity");
        assertThat(entity.getId()).isNull();

        TestJpaEntity savedEntity = repository.save(entity);

        assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getId()).isGreaterThan(0L);
    }

    @Test
    @DisplayName("ID is retrieved from database after save")
    void testIdIsRetrievedFromDatabase() {
        TestJpaEntity entity = new TestJpaEntity("Test Entity");
        TestJpaEntity savedEntity = repository.save(entity);
        Long savedId = savedEntity.getId();

        TestJpaEntity retrievedEntity = repository.findById(savedId).orElse(null);

        assertThat(retrievedEntity).isNotNull();
        assertThat(retrievedEntity.getId()).isEqualTo(savedId);
        assertThat(retrievedEntity.getUuid()).isEqualTo(savedEntity.getUuid());
    }

    // ==================== Timestamp Type Tests ====================

    @Test
    @DisplayName("Timestamps are of type Instant (UTC)")
    void testTimestampsAreInstantType() {
        TestJpaEntity entity = new TestJpaEntity("Test Entity");
        TestJpaEntity savedEntity = repository.save(entity);

        assertThat(savedEntity.getCreatedAt()).isInstanceOf(Instant.class);
        assertThat(savedEntity.getUpdatedAt()).isInstanceOf(Instant.class);
    }

    @Test
    @DisplayName("Instant timestamps can be compared")
    void testInstantTimestampsCanBeCompared() {
        TestJpaEntity entity = new TestJpaEntity("Test Entity");
        TestJpaEntity savedEntity = repository.save(entity);

        Instant now = Instant.now();

        assertThat(savedEntity.getCreatedAt()).isBefore(now);
        assertThat(savedEntity.getUpdatedAt()).isBefore(now);
    }

    // ==================== Batch Operation Tests ====================

    @Test
    @DisplayName("Auditing works with saveAll (batch save)")
    void testAuditingWorksWithSaveAll() {
        TestJpaEntity entity1 = new TestJpaEntity("Entity 1");
        TestJpaEntity entity2 = new TestJpaEntity("Entity 2");

        var savedEntities = repository.saveAll(java.util.List.of(entity1, entity2));

        assertThat(savedEntities).hasSize(2);
        assertThat(savedEntities).allMatch(e -> e.getCreatedAt() != null);
        assertThat(savedEntities).allMatch(e -> e.getUpdatedAt() != null);
    }

    // ==================== Roundtrip Tests ====================

    @Test
    @DisplayName("Entity roundtrip preserves all audit fields")
    void testEntityRoundtripPreservesAuditFields() {
        TestJpaEntity entity = new TestJpaEntity("Original");
        TestJpaEntity savedEntity = repository.save(entity);

        var uuid = savedEntity.getUuid();
        var id = savedEntity.getId();
        var createdAt = savedEntity.getCreatedAt();
        var updatedAt = savedEntity.getUpdatedAt();

        TestJpaEntity retrievedEntity = repository.findById(id).orElse(null);

        assertThat(retrievedEntity).isNotNull();
        assertThat(retrievedEntity.getUuid()).isEqualTo(uuid);
        assertThat(retrievedEntity.getId()).isEqualTo(id);
        assertThat(retrievedEntity.getCreatedAt()).isEqualTo(createdAt);
        assertThat(retrievedEntity.getUpdatedAt()).isEqualTo(updatedAt);
    }

    /**
     * Helper method to make Thread.sleep work in test.
     * Wrapped with try-catch to handle InterruptedException.
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
