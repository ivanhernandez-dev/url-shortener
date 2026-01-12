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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserUrlStatsUseCaseImpl")
class GetUserUrlStatsUseCaseImplTest {

    @Mock
    private UrlRepository urlRepository;

    private GetUserUrlStatsUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetUserUrlStatsUseCaseImpl(urlRepository);
    }

    @Test
    @DisplayName("getUserUrlStats should return stats when URL belongs to user")
    void getUserUrlStats_shouldReturnStats_whenUrlBelongsToUser() {
        UUID userId = UUID.randomUUID();
        String shortCode = "abc123";
        
        Url url = new Url();
        url.setShortCode(shortCode);
        url.setOriginalUrl("https://example.com");
        url.setUserId(userId);
        url.setAccessCount(42L);
        url.setCreatedAt(LocalDateTime.now());
        url.setLastAccessedAt(LocalDateTime.now());

        when(urlRepository.findByShortCodeAndUserId(shortCode, userId))
                .thenReturn(Optional.of(url));

        UrlStatsResponse response = useCase.getUserUrlStats(shortCode, userId);

        assertNotNull(response);
        assertEquals(shortCode, response.shortCode());
        assertEquals(42L, response.accessCount());
        verify(urlRepository).findByShortCodeAndUserId(shortCode, userId);
    }

    @Test
    @DisplayName("getUserUrlStats should throw UrlNotFoundException when URL not found")
    void getUserUrlStats_shouldThrowException_whenUrlNotFound() {
        UUID userId = UUID.randomUUID();
        String shortCode = "notfound";

        when(urlRepository.findByShortCodeAndUserId(shortCode, userId))
                .thenReturn(Optional.empty());

        UrlNotFoundException exception = assertThrows(
                UrlNotFoundException.class,
                () -> useCase.getUserUrlStats(shortCode, userId)
        );

        assertTrue(exception.getMessage().contains(shortCode));
    }

    @Test
    @DisplayName("getUserUrlStats should throw UrlNotFoundException when URL belongs to different user")
    void getUserUrlStats_shouldThrowException_whenUrlBelongsToDifferentUser() {
        UUID userId = UUID.randomUUID();
        UUID differentUserId = UUID.randomUUID();
        String shortCode = "abc123";

        when(urlRepository.findByShortCodeAndUserId(shortCode, userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UrlNotFoundException.class,
                () -> useCase.getUserUrlStats(shortCode, userId)
        );
    }
}
