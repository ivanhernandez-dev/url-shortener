package dev.ivanhernandez.urlshortener.application.dto.response;

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
@DisplayName("UrlStatsResponse")
class UrlStatsResponseTest {

    @Mock
    private Url mockUrl;

    @Test
    @DisplayName("fromDomain should correctly map all fields")
    void fromDomain_shouldMapAllFields() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(7);
        LocalDateTime lastAccessedAt = LocalDateTime.now().minusHours(1);

        when(mockUrl.getShortCode()).thenReturn("abc123");
        when(mockUrl.getOriginalUrl()).thenReturn("https://example.com");
        when(mockUrl.getAccessCount()).thenReturn(42L);
        when(mockUrl.getCreatedAt()).thenReturn(createdAt);
        when(mockUrl.getLastAccessedAt()).thenReturn(lastAccessedAt);

        UrlStatsResponse response = UrlStatsResponse.fromDomain(mockUrl);

        assertEquals("abc123", response.shortCode());
        assertEquals("https://example.com", response.originalUrl());
        assertEquals(42L, response.accessCount());
        assertEquals(createdAt, response.createdAt());
        assertEquals(lastAccessedAt, response.lastAccessedAt());
    }

    @Test
    @DisplayName("fromDomain should handle null lastAccessedAt")
    void fromDomain_shouldHandleNullLastAccessedAt() {
        when(mockUrl.getShortCode()).thenReturn("abc123");
        when(mockUrl.getOriginalUrl()).thenReturn("https://example.com");
        when(mockUrl.getAccessCount()).thenReturn(0L);
        when(mockUrl.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(mockUrl.getLastAccessedAt()).thenReturn(null);

        UrlStatsResponse response = UrlStatsResponse.fromDomain(mockUrl);

        assertNull(response.lastAccessedAt());
        assertEquals(0L, response.accessCount());
    }

    @Test
    @DisplayName("fromDomain should handle zero access count")
    void fromDomain_shouldHandleZeroAccessCount() {
        when(mockUrl.getShortCode()).thenReturn("abc123");
        when(mockUrl.getOriginalUrl()).thenReturn("https://example.com");
        when(mockUrl.getAccessCount()).thenReturn(0L);
        when(mockUrl.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(mockUrl.getLastAccessedAt()).thenReturn(null);

        UrlStatsResponse response = UrlStatsResponse.fromDomain(mockUrl);

        assertEquals(0L, response.accessCount());
    }
}
