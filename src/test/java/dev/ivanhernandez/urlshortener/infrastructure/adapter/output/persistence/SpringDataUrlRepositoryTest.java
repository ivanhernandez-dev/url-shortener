package dev.ivanhernandez.urlshortener.infrastructure.adapter.output.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("SpringDataUrlRepository")
class SpringDataUrlRepositoryTest {

    @Autowired
    private SpringDataUrlRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("save should persist entity and generate ID")
    void save_shouldPersistAndGenerateId() {
        UrlJpaEntity entity = createEntity("abc123");

        UrlJpaEntity saved = repository.save(entity);

        assertNotNull(saved.getId());
        assertEquals("abc123", saved.getShortCode());
    }

    @Test
    @DisplayName("save should update existing entity")
    void save_shouldUpdateExisting() {
        UrlJpaEntity entity = createEntity("update1");
        UrlJpaEntity saved = repository.save(entity);

        saved.setAccessCount(10L);
        repository.save(saved);

        UrlJpaEntity found = repository.findById(saved.getId()).orElseThrow();
        assertEquals(10L, found.getAccessCount());
    }

    @Test
    @DisplayName("findByShortCode should find entity by short code")
    void findByShortCode_shouldFindByShortCode() {
        UrlJpaEntity entity = createEntity("find1");
        repository.save(entity);

        Optional<UrlJpaEntity> found = repository.findByShortCode("find1");

        assertTrue(found.isPresent());
        assertEquals("https://example.com", found.get().getOriginalUrl());
    }

    @Test
    @DisplayName("findByShortCode should return empty when not found")
    void findByShortCode_shouldReturnEmpty_whenNotFound() {
        Optional<UrlJpaEntity> found = repository.findByShortCode("notfound");

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("existsByShortCode should return true when exists")
    void existsByShortCode_shouldReturnTrue_whenExists() {
        UrlJpaEntity entity = createEntity("exists1");
        repository.save(entity);

        assertTrue(repository.existsByShortCode("exists1"));
    }

    @Test
    @DisplayName("existsByShortCode should return false when does not exist")
    void existsByShortCode_shouldReturnFalse_whenNotExists() {
        assertFalse(repository.existsByShortCode("doesnotexist"));
    }

    @Test
    @DisplayName("deleteByShortCode should delete entity by short code")
    void deleteByShortCode_shouldDeleteByShortCode() {
        UrlJpaEntity entity = createEntity("delete1");
        repository.save(entity);
        assertTrue(repository.existsByShortCode("delete1"));

        repository.deleteByShortCode("delete1");

        assertFalse(repository.existsByShortCode("delete1"));
    }

    @Test
    @DisplayName("deleteByShortCode should not throw when deleting non-existent")
    void deleteByShortCode_shouldNotThrow_whenNotExists() {
        assertDoesNotThrow(() -> repository.deleteByShortCode("doesnotexist"));
    }

    @Test
    @DisplayName("save should enforce unique short code constraint")
    void save_shouldEnforceUniqueShortCode() {
        UrlJpaEntity entity1 = createEntity("unique1");
        repository.save(entity1);

        UrlJpaEntity entity2 = createEntity("unique1");

        assertThrows(Exception.class, () -> {
            repository.saveAndFlush(entity2);
        });
    }

    private UrlJpaEntity createEntity(String shortCode) {
        UrlJpaEntity entity = new UrlJpaEntity();
        entity.setShortCode(shortCode);
        entity.setOriginalUrl("https://example.com");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setAccessCount(0L);
        return entity;
    }
}
