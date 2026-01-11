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
@DisplayName("ShortUrlResponse")
class ShortUrlResponseTest {

    @Mock
    private Url mockUrl;

    @Test
    @DisplayName("fromDomain should correctly map all fields")
    void fromDomain_shouldMapAllFields() {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);

        when(mockUrl.getShortCode()).thenReturn("abc123");
        when(mockUrl.getOriginalUrl()).thenReturn("https://example.com");
        when(mockUrl.getCreatedAt()).thenReturn(createdAt);
        when(mockUrl.getExpiresAt()).thenReturn(expiresAt);

        ShortUrlResponse response = ShortUrlResponse.fromDomain(mockUrl, "http://localhost:8081");

        assertEquals("http://localhost:8081/r/abc123", response.shortUrl());
        assertEquals("abc123", response.shortCode());
        assertEquals("https://example.com", response.originalUrl());
        assertEquals(createdAt, response.createdAt());
        assertEquals(expiresAt, response.expiresAt());
    }

    @Test
    @DisplayName("fromDomain should handle null expiresAt")
    void fromDomain_shouldHandleNullExpiresAt() {
        when(mockUrl.getShortCode()).thenReturn("abc123");
        when(mockUrl.getOriginalUrl()).thenReturn("https://example.com");
        when(mockUrl.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(mockUrl.getExpiresAt()).thenReturn(null);

        ShortUrlResponse response = ShortUrlResponse.fromDomain(mockUrl, "http://localhost:8081");

        assertNull(response.expiresAt());
    }

    @Test
    @DisplayName("fromDomain should build correct short URL with different base URLs")
    void fromDomain_shouldBuildCorrectShortUrl_withDifferentBaseUrls() {
        when(mockUrl.getShortCode()).thenReturn("mycode");
        when(mockUrl.getOriginalUrl()).thenReturn("https://example.com");
        when(mockUrl.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(mockUrl.getExpiresAt()).thenReturn(null);

        ShortUrlResponse response1 = ShortUrlResponse.fromDomain(mockUrl, "https://url.example.com");
        ShortUrlResponse response2 = ShortUrlResponse.fromDomain(mockUrl, "http://localhost:8080");

        assertEquals("https://url.example.com/r/mycode", response1.shortUrl());
        assertEquals("http://localhost:8080/r/mycode", response2.shortUrl());
    }
}
