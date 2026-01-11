package dev.ivanhernandez.urlshortener.application.usecase;

import dev.ivanhernandez.urlshortener.application.dto.response.UrlStatsResponse;
import dev.ivanhernandez.urlshortener.application.port.output.UrlRepository;
import dev.ivanhernandez.urlshortener.domain.exception.UrlNotFoundException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUrlStatsUseCaseImpl")
class GetUrlStatsUseCaseImplTest {

    @Mock
    private UrlRepository urlRepository;

    private GetUrlStatsUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetUrlStatsUseCaseImpl(urlRepository);
    }

    @Test
    @DisplayName("getUrlStats should return stats when URL exists")
    void getUrlStats_shouldReturnStats_whenUrlExists() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(7);
        LocalDateTime lastAccessedAt = LocalDateTime.now().minusHours(1);
        Url url = createUrl("abc123", 42L, createdAt, lastAccessedAt);
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(url));

        UrlStatsResponse response = useCase.getUrlStats("abc123");

        assertNotNull(response);
        assertEquals("abc123", response.shortCode());
        assertEquals("https://example.com", response.originalUrl());
        assertEquals(42L, response.accessCount());
        assertEquals(createdAt, response.createdAt());
        assertEquals(lastAccessedAt, response.lastAccessedAt());
    }

    @Test
    @DisplayName("getUrlStats should throw UrlNotFoundException when URL does not exist")
    void getUrlStats_shouldThrowUrlNotFoundException_whenNotFound() {
        when(urlRepository.findByShortCode("notfound")).thenReturn(Optional.empty());

        UrlNotFoundException exception = assertThrows(
                UrlNotFoundException.class,
                () -> useCase.getUrlStats("notfound")
        );

        assertTrue(exception.getMessage().contains("notfound"));
    }

    @Test
    @DisplayName("getUrlStats should return stats with null lastAccessedAt when never accessed")
    void getUrlStats_shouldReturnNullLastAccessedAt_whenNeverAccessed() {
        Url url = createUrl("neveraccessed", 0L, LocalDateTime.now(), null);
        when(urlRepository.findByShortCode("neveraccessed")).thenReturn(Optional.of(url));

        UrlStatsResponse response = useCase.getUrlStats("neveraccessed");

        assertEquals(0L, response.accessCount());
        assertNull(response.lastAccessedAt());
    }

    private Url createUrl(String shortCode, Long accessCount, LocalDateTime createdAt, LocalDateTime lastAccessedAt) {
        Url url = new Url();
        url.setId(1L);
        url.setOriginalUrl("https://example.com");
        url.setShortCode(shortCode);
        url.setCreatedAt(createdAt);
        url.setAccessCount(accessCount);
        url.setLastAccessedAt(lastAccessedAt);
        return url;
    }
}
