package dev.ivanhernandez.urlshortener.infrastructure.adapter.output.persistence;

import dev.ivanhernandez.urlshortener.domain.model.Url;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JpaUrlRepository")
class JpaUrlRepositoryTest {

    @Mock
    private SpringDataUrlRepository springDataUrlRepository;

    private JpaUrlRepository jpaUrlRepository;

    @BeforeEach
    void setUp() {
        jpaUrlRepository = new JpaUrlRepository(springDataUrlRepository);
    }

    @Test
    @DisplayName("save should save and return domain model")
    void save_shouldSaveAndReturnDomainModel() {
        Url url = createUrl();
        UrlJpaEntity savedEntity = createEntity();
        when(springDataUrlRepository.save(any(UrlJpaEntity.class))).thenReturn(savedEntity);

        Url result = jpaUrlRepository.save(url);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(url.getOriginalUrl(), result.getOriginalUrl());
        assertEquals(url.getShortCode(), result.getShortCode());
        verify(springDataUrlRepository).save(any(UrlJpaEntity.class));
    }

    @Test
    @DisplayName("findByShortCode should return domain model when found")
    void findByShortCode_shouldReturnDomainModel_whenFound() {
        UrlJpaEntity entity = createEntity();
        when(springDataUrlRepository.findByShortCode("abc123")).thenReturn(Optional.of(entity));

        Optional<Url> result = jpaUrlRepository.findByShortCode("abc123");

        assertTrue(result.isPresent());
        assertEquals("abc123", result.get().getShortCode());
        assertEquals("https://example.com", result.get().getOriginalUrl());
        verify(springDataUrlRepository).findByShortCode("abc123");
    }

    @Test
    @DisplayName("findByShortCode should return empty when not found")
    void findByShortCode_shouldReturnEmpty_whenNotFound() {
        when(springDataUrlRepository.findByShortCode("notfound")).thenReturn(Optional.empty());

        Optional<Url> result = jpaUrlRepository.findByShortCode("notfound");

        assertTrue(result.isEmpty());
        verify(springDataUrlRepository).findByShortCode("notfound");
    }

    @Test
    @DisplayName("existsByShortCode should return true when exists")
    void existsByShortCode_shouldReturnTrue_whenExists() {
        when(springDataUrlRepository.existsByShortCode("abc123")).thenReturn(true);

        boolean result = jpaUrlRepository.existsByShortCode("abc123");

        assertTrue(result);
        verify(springDataUrlRepository).existsByShortCode("abc123");
    }

    @Test
    @DisplayName("existsByShortCode should return false when does not exist")
    void existsByShortCode_shouldReturnFalse_whenNotExists() {
        when(springDataUrlRepository.existsByShortCode("notfound")).thenReturn(false);

        boolean result = jpaUrlRepository.existsByShortCode("notfound");

        assertFalse(result);
        verify(springDataUrlRepository).existsByShortCode("notfound");
    }

    @Test
    @DisplayName("deleteByShortCode should delegate to spring data repository")
    void deleteByShortCode_shouldDelegateToSpringDataRepository() {
        jpaUrlRepository.deleteByShortCode("abc123");

        verify(springDataUrlRepository).deleteByShortCode("abc123");
    }

    private Url createUrl() {
        Url url = new Url();
        url.setOriginalUrl("https://example.com");
        url.setShortCode("abc123");
        url.setCreatedAt(LocalDateTime.now());
        url.setAccessCount(0L);
        return url;
    }

    private UrlJpaEntity createEntity() {
        UrlJpaEntity entity = new UrlJpaEntity();
        entity.setId(1L);
        entity.setOriginalUrl("https://example.com");
        entity.setShortCode("abc123");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setAccessCount(0L);
        return entity;
    }
}
