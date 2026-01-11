package dev.ivanhernandez.urlshortener.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Url Domain Model")
class UrlTest {

    @Test
    @DisplayName("isExpired should return false when expiresAt is null")
    void isExpired_shouldReturnFalse_whenExpiresAtIsNull() {
        Url url = new Url();
        url.setExpiresAt(null);

        assertFalse(url.isExpired());
    }

    @Test
    @DisplayName("isExpired should return false when expiresAt is in the future")
    void isExpired_shouldReturnFalse_whenExpiresAtIsInFuture() {
        Url url = new Url();
        url.setExpiresAt(LocalDateTime.now().plusDays(1));

        assertFalse(url.isExpired());
    }

    @Test
    @DisplayName("isExpired should return true when expiresAt is in the past")
    void isExpired_shouldReturnTrue_whenExpiresAtIsInPast() {
        Url url = new Url();
        url.setExpiresAt(LocalDateTime.now().minusDays(1));

        assertTrue(url.isExpired());
    }

    @Test
    @DisplayName("incrementAccessCount should increment from zero")
    void incrementAccessCount_shouldIncrementFromZero() {
        Url url = new Url();
        url.setAccessCount(0L);

        url.incrementAccessCount();

        assertEquals(1L, url.getAccessCount());
    }

    @Test
    @DisplayName("incrementAccessCount should increment from existing value")
    void incrementAccessCount_shouldIncrementFromExistingValue() {
        Url url = new Url();
        url.setAccessCount(10L);

        url.incrementAccessCount();

        assertEquals(11L, url.getAccessCount());
    }

    @Test
    @DisplayName("incrementAccessCount should handle null access count")
    void incrementAccessCount_shouldHandleNullAccessCount() {
        Url url = new Url();
        url.setAccessCount(null);

        url.incrementAccessCount();

        assertEquals(1L, url.getAccessCount());
    }

    @Test
    @DisplayName("incrementAccessCount should update lastAccessedAt")
    void incrementAccessCount_shouldUpdateLastAccessedAt() {
        Url url = new Url();
        url.setAccessCount(0L);
        LocalDateTime before = LocalDateTime.now();

        url.incrementAccessCount();

        assertNotNull(url.getLastAccessedAt());
        assertTrue(url.getLastAccessedAt().isAfter(before.minusSeconds(1)));
    }

    @Test
    @DisplayName("constructor should create Url with all parameters")
    void constructor_shouldCreateUrlWithAllParameters() {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
        LocalDateTime lastAccessedAt = LocalDateTime.now();

        Url url = new Url(1L, "https://example.com", "abc123", createdAt, expiresAt, 5L, lastAccessedAt);

        assertEquals(1L, url.getId());
        assertEquals("https://example.com", url.getOriginalUrl());
        assertEquals("abc123", url.getShortCode());
        assertEquals(createdAt, url.getCreatedAt());
        assertEquals(expiresAt, url.getExpiresAt());
        assertEquals(5L, url.getAccessCount());
        assertEquals(lastAccessedAt, url.getLastAccessedAt());
    }

    @Test
    @DisplayName("default constructor should create empty Url")
    void defaultConstructor_shouldCreateEmptyUrl() {
        Url url = new Url();

        assertNull(url.getId());
        assertNull(url.getOriginalUrl());
        assertNull(url.getShortCode());
    }
}
