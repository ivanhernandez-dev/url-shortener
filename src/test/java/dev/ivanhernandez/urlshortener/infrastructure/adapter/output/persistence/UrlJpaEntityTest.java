package dev.ivanhernandez.urlshortener.infrastructure.adapter.output.persistence;

import dev.ivanhernandez.urlshortener.domain.model.Url;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UrlJpaEntity")
class UrlJpaEntityTest {

    @Mock
    private Url mockUrl;

    @Test
    @DisplayName("fromDomain should correctly map all fields from domain model")
    void fromDomain_shouldMapAllFields() {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
        LocalDateTime lastAccessedAt = LocalDateTime.now().minusHours(1);

        when(mockUrl.getId()).thenReturn(1L);
        when(mockUrl.getOriginalUrl()).thenReturn("https://example.com");
        when(mockUrl.getShortCode()).thenReturn("abc123");
        when(mockUrl.getUserId()).thenReturn(null);
        when(mockUrl.getTenantId()).thenReturn(null);
        when(mockUrl.getCreatedAt()).thenReturn(createdAt);
        when(mockUrl.getExpiresAt()).thenReturn(expiresAt);
        when(mockUrl.getAccessCount()).thenReturn(42L);
        when(mockUrl.getLastAccessedAt()).thenReturn(lastAccessedAt);

        UrlJpaEntity entity = UrlJpaEntity.fromDomain(mockUrl);

        assertEquals(1L, entity.getId());
        assertEquals("https://example.com", entity.getOriginalUrl());
        assertEquals("abc123", entity.getShortCode());
        assertNull(entity.getUserId());
        assertNull(entity.getTenantId());
        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(expiresAt, entity.getExpiresAt());
        assertEquals(42L, entity.getAccessCount());
        assertEquals(lastAccessedAt, entity.getLastAccessedAt());
    }

    @Test
    @DisplayName("fromDomain should handle null optional fields")
    void fromDomain_shouldHandleNullFields() {
        when(mockUrl.getId()).thenReturn(null);
        when(mockUrl.getOriginalUrl()).thenReturn("https://example.com");
        when(mockUrl.getShortCode()).thenReturn("abc123");
        when(mockUrl.getUserId()).thenReturn(null);
        when(mockUrl.getTenantId()).thenReturn(null);
        when(mockUrl.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(mockUrl.getExpiresAt()).thenReturn(null);
        when(mockUrl.getAccessCount()).thenReturn(0L);
        when(mockUrl.getLastAccessedAt()).thenReturn(null);

        UrlJpaEntity entity = UrlJpaEntity.fromDomain(mockUrl);

        assertNull(entity.getId());
        assertNull(entity.getUserId());
        assertNull(entity.getTenantId());
        assertNull(entity.getExpiresAt());
        assertNull(entity.getLastAccessedAt());
    }

    @Test
    @DisplayName("toDomain should correctly map all fields to domain model")
    void toDomain_shouldMapAllFields() {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
        LocalDateTime lastAccessedAt = LocalDateTime.now().minusHours(1);

        UrlJpaEntity entity = new UrlJpaEntity();
        entity.setId(1L);
        entity.setOriginalUrl("https://example.com");
        entity.setShortCode("abc123");
        entity.setCreatedAt(createdAt);
        entity.setExpiresAt(expiresAt);
        entity.setAccessCount(42L);
        entity.setLastAccessedAt(lastAccessedAt);

        Url url = entity.toDomain();

        assertEquals(1L, url.getId());
        assertEquals("https://example.com", url.getOriginalUrl());
        assertEquals("abc123", url.getShortCode());
        assertEquals(createdAt, url.getCreatedAt());
        assertEquals(expiresAt, url.getExpiresAt());
        assertEquals(42L, url.getAccessCount());
        assertEquals(lastAccessedAt, url.getLastAccessedAt());
    }

    @Test
    @DisplayName("toDomain should handle null optional fields")
    void toDomain_shouldHandleNullFields() {
        UrlJpaEntity entity = new UrlJpaEntity();
        entity.setId(1L);
        entity.setOriginalUrl("https://example.com");
        entity.setShortCode("abc123");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setExpiresAt(null);
        entity.setAccessCount(0L);
        entity.setLastAccessedAt(null);

        Url url = entity.toDomain();

        assertNull(url.getExpiresAt());
        assertNull(url.getLastAccessedAt());
    }

    @Test
    @DisplayName("round-trip conversion should preserve all data")
    void roundTrip_shouldPreserveData() {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
        LocalDateTime lastAccessedAt = LocalDateTime.now().minusHours(1);

        when(mockUrl.getId()).thenReturn(1L);
        when(mockUrl.getOriginalUrl()).thenReturn("https://example.com");
        when(mockUrl.getShortCode()).thenReturn("abc123");
        when(mockUrl.getUserId()).thenReturn(null);
        when(mockUrl.getTenantId()).thenReturn(null);
        when(mockUrl.getCreatedAt()).thenReturn(createdAt);
        when(mockUrl.getExpiresAt()).thenReturn(expiresAt);
        when(mockUrl.getAccessCount()).thenReturn(42L);
        when(mockUrl.getLastAccessedAt()).thenReturn(lastAccessedAt);

        UrlJpaEntity entity = UrlJpaEntity.fromDomain(mockUrl);
        Url restored = entity.toDomain();

        assertEquals(1L, restored.getId());
        assertEquals("https://example.com", restored.getOriginalUrl());
        assertEquals("abc123", restored.getShortCode());
        assertNull(restored.getUserId());
        assertNull(restored.getTenantId());
        assertEquals(createdAt, restored.getCreatedAt());
        assertEquals(expiresAt, restored.getExpiresAt());
        assertEquals(42L, restored.getAccessCount());
        assertEquals(lastAccessedAt, restored.getLastAccessedAt());
    }
}
