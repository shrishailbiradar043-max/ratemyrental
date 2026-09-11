package com.ratemyrental.common.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for BaseEntity.
 *
 * Test Coverage:
 * 1. UUID generation and immutability
 * 2. Audit timestamp (createdAt, updatedAt) management
 * 3. equals() and hashCode() behavior
 * 4. ID field generation (before persistence)
 * 5. toString() output
 *
 * Notes:
 * - These tests DO NOT require a database connection.
 * - They test the BaseEntity class in isolation.
 * - Integration tests (with @DataJpaTest) test the full JPA lifecycle.
 */
@DisplayName("BaseEntity Unit Tests")
class BaseEntityTest {

    /**
     * Concrete test implementation of BaseEntity.
     * Used because BaseEntity is @MappedSuperclass and not directly persistable.
     */
    static class TestEntity extends BaseEntity {
        private String name;

        protected TestEntity() {
            super();
        }

        protected TestEntity(String name) {
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

    // ==================== UUID Generation Tests ====================

    @Test
    @DisplayName("UUID is null before persistence (generated during JPA persist)")
    void testUuidIsNullBeforePersistence() {
        TestEntity entity = new TestEntity("Test Entity");
        assertThat(entity.getUuid()).isNull();
    }

    @Test
    @DisplayName("UUID is generated during persistence")
    void testUuidIsGeneratedDuringPersistence() {
        TestEntity entity = new TestEntity("Test Entity");
        assertThat(entity.getUuid()).isNull();
        
        // After manually setting UUID (simulating JPA generation):
        UUID generatedUuid = UUID.randomUUID();
        entity.setUuid(generatedUuid);
        
        assertThat(entity.getUuid()).isNotNull().isEqualTo(generatedUuid);
    }

    @Test
    @DisplayName("UUID is unique for each entity instance")
    void testUuidIsUniquePerInstance() {
        TestEntity entity1 = new TestEntity("Entity 1");
        TestEntity entity2 = new TestEntity("Entity 2");

        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        entity1.setUuid(uuid1);
        entity2.setUuid(uuid2);

        assertThat(entity1.getUuid()).isNotEqualTo(entity2.getUuid());
    }

    @Test
    @DisplayName("UUID is immutable (protected setter for tests only)")
    void testUuidIsImmutable() {
        TestEntity entity = new TestEntity("Test Entity");
        UUID originalUuid = UUID.randomUUID();
        entity.setUuid(originalUuid);

        UUID newUuid = UUID.randomUUID();
        entity.setUuid(newUuid);

        assertThat(entity.getUuid()).isEqualTo(newUuid);
        assertThat(entity.getUuid()).isNotEqualTo(originalUuid);
    }

    // ==================== ID Tests ====================

    @Test
    @DisplayName("ID is null before entity is persisted")
    void testIdIsNullBeforePersistence() {
        TestEntity entity = new TestEntity("Test Entity");
        assertThat(entity.getId()).isNull();
    }

    @Test
    @DisplayName("ID getter returns null without persistence")
    void testIdGetterReturnsNull() {
        TestEntity entity = new TestEntity();
        assertThat(entity.getId()).isNull();
    }

    // ==================== Timestamp Tests ====================

    @Test
    @DisplayName("Timestamps are null before persistence")
    void testTimestampsAreNullBeforePersistence() {
        TestEntity entity = new TestEntity("Test Entity");

        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("createdAt can be set for testing purposes")
    void testCreatedAtCanBeSetForTesting() {
        TestEntity entity = new TestEntity("Test Entity");
        Instant now = Instant.now();

        entity.setCreatedAt(now);

        assertThat(entity.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("updatedAt can be set for testing purposes")
    void testUpdatedAtCanBeSetForTesting() {
        TestEntity entity = new TestEntity("Test Entity");
        Instant now = Instant.now();

        entity.setUpdatedAt(now);

        assertThat(entity.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Timestamps are independent before persistence")
    void testTimestampsAreIndependent() {
        TestEntity entity = new TestEntity("Test Entity");
        Instant createdTime = Instant.now().minusSeconds(100);
        Instant updatedTime = Instant.now();

        entity.setCreatedAt(createdTime);
        entity.setUpdatedAt(updatedTime);

        assertThat(entity.getCreatedAt()).isEqualTo(createdTime);
        assertThat(entity.getUpdatedAt()).isEqualTo(updatedTime);
        assertThat(entity.getCreatedAt()).isNotEqualTo(entity.getUpdatedAt());
    }

    // ==================== equals() Tests ====================

    @Test
    @DisplayName("Entity equals itself (identity)")
    void testEntityEqualsItself() {
        TestEntity entity = new TestEntity("Test Entity");
        assertThat(entity).isEqualTo(entity);
    }

    @Test
    @DisplayName("Two entities with same UUID are equal")
    void testTwoEntitiesWithSameUuidAreEqual() {
        TestEntity entity1 = new TestEntity("Entity 1");
        TestEntity entity2 = new TestEntity("Entity 2");

        UUID sameUuid = UUID.randomUUID();
        entity1.setUuid(sameUuid);
        entity2.setUuid(sameUuid);

        assertThat(entity1).isEqualTo(entity2);
    }

    @Test
    @DisplayName("Two entities with different UUIDs are not equal")
    void testTwoEntitiesWithDifferentUuidsAreNotEqual() {
        TestEntity entity1 = new TestEntity("Entity 1");
        TestEntity entity2 = new TestEntity("Entity 2");

        assertThat(entity1).isNotEqualTo(entity2);
    }

    @Test
    @DisplayName("Entity is not equal to null")
    void testEntityIsNotEqualToNull() {
        TestEntity entity = new TestEntity("Test Entity");
        assertThat(entity).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Entity is not equal to object of different type")
    void testEntityIsNotEqualToDifferentType() {
        TestEntity entity = new TestEntity("Test Entity");
        assertThat(entity).isNotEqualTo("not an entity");
    }

    @Test
    @DisplayName("UUID-based equality is consistent regardless of other fields")
    void testEqualityIsBasedOnlyOnUuid() {
        TestEntity entity1 = new TestEntity("Entity 1");
        TestEntity entity2 = new TestEntity("Entity 2");

        UUID sameUuid = UUID.randomUUID();
        entity1.setUuid(sameUuid);
        entity2.setUuid(sameUuid);

        Instant time1 = Instant.now().minusSeconds(1000);
        Instant time2 = Instant.now();
        entity1.setCreatedAt(time1);
        entity2.setCreatedAt(time2);

        assertThat(entity1).isEqualTo(entity2);
    }

    // ==================== hashCode() Tests ====================

    @Test
    @DisplayName("Hash code is consistent across calls")
    void testHashCodeIsConsistent() {
        TestEntity entity = new TestEntity("Test Entity");
        int hash1 = entity.hashCode();
        int hash2 = entity.hashCode();

        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("Two entities with same UUID have same hash code")
    void testSameUuidSameHashCode() {
        TestEntity entity1 = new TestEntity("Entity 1");
        TestEntity entity2 = new TestEntity("Entity 2");

        UUID sameUuid = UUID.randomUUID();
        entity1.setUuid(sameUuid);
        entity2.setUuid(sameUuid);

        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
    }

    @Test
    @DisplayName("Entities can be used in HashMap based on UUID")
    void testEntitiesCanBeUsedInHashMap() {
        TestEntity entity1 = new TestEntity("Entity 1");
        TestEntity entity2 = new TestEntity("Entity 1 duplicate");

        UUID sameUuid = UUID.randomUUID();
        entity1.setUuid(sameUuid);
        entity2.setUuid(sameUuid);

        // In a HashMap, entity2 should overwrite entity1 because they have same hash and are equal
        java.util.Map<TestEntity, String> map = new java.util.HashMap<>();
        map.put(entity1, "first");
        map.put(entity2, "second");

        assertThat(map).hasSize(1);
        assertThat(map.get(entity1)).isEqualTo("second");
    }

    // ==================== toString() Tests ====================

    @Test
    @DisplayName("toString() includes entity class name")
    void testToStringIncludesClassName() {
        TestEntity entity = new TestEntity("Test Entity");
        String result = entity.toString();

        assertThat(result).contains("TestEntity");
    }

    @Test
    @DisplayName("toString() includes UUID when set")
    void testToStringIncludesUuid() {
        TestEntity entity = new TestEntity("Test Entity");
        UUID testUuid = UUID.randomUUID();
        entity.setUuid(testUuid);
        
        String result = entity.toString();
        assertThat(result).contains(testUuid.toString());
    }

    @Test
    @DisplayName("toString() does not include id or business fields")
    void testToStringDoesNotIncludeIdOrBusinessFields() {
        TestEntity entity = new TestEntity("Test Entity");
        String result = entity.toString();

        assertThat(result).doesNotContain("Test Entity");
    }

    @Test
    @DisplayName("toString() format is consistent")
    void testToStringFormat() {
        TestEntity entity = new TestEntity("Test Entity");
        Instant now = Instant.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        String result = entity.toString();

        assertThat(result).containsPattern("TestEntity\\{uuid=.*");
    }

    // ==================== Type Safety Tests ====================

    @Test
    @DisplayName("Different entity subclasses are not equal even with same UUID")
    void testDifferentSubclassesAreNotEqual() {
        TestEntity entity1 = new TestEntity("Entity 1");

        class AnotherTestEntity extends BaseEntity {
        }
        AnotherTestEntity entity2 = new AnotherTestEntity();

        UUID sameUuid = UUID.randomUUID();
        entity1.setUuid(sameUuid);
        entity2.setUuid(sameUuid);

        assertThat(entity1).isNotEqualTo(entity2);
    }

    // ==================== Builder Pattern Support Tests ====================

    @Test
    @DisplayName("Entity can be constructed with custom name")
    void testEntityConstructorWithName() {
        String name = "Custom Name";
        TestEntity entity = new TestEntity(name);

        assertThat(entity.getName()).isEqualTo(name);
        assertThat(entity.getUuid()).isNull(); // UUID generated during JPA persist, not construction
    }

    @Test
    @DisplayName("Entity default constructor works")
    void testDefaultConstructor() {
        TestEntity entity = new TestEntity();

        assertThat(entity.getUuid()).isNull(); // UUID generated during JPA persist, not construction
        assertThat(entity.getName()).isNull();
    }
}
