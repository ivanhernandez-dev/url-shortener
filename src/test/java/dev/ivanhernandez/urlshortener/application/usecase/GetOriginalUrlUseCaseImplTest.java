package dev.ivanhernandez.urlshortener.application.usecase;

import dev.ivanhernandez.urlshortener.application.port.output.UrlRepository;
import dev.ivanhernandez.urlshortener.domain.exception.ExpiredUrlException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetOriginalUrlUseCaseImpl")
class GetOriginalUrlUseCaseImplTest {

    @Mock
    private UrlRepository urlRepository;

    private GetOriginalUrlUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetOriginalUrlUseCaseImpl(urlRepository);
    }

    @Test
    @DisplayName("getOriginalUrl should return original URL when short code exists and is not expired")
    void getOriginalUrl_shouldReturnOriginalUrl_whenExistsAndNotExpired() {
        Url url = createValidUrl();
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(url));
        when(urlRepository.save(any(Url.class))).thenReturn(url);

        String result = useCase.getOriginalUrl("abc123");

        assertEquals("https://example.com", result);
    }

    @Test
    @DisplayName("getOriginalUrl should increment access count")
    void getOriginalUrl_shouldIncrementAccessCount() {
        Url url = createValidUrl();
        url.setAccessCount(5L);
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(url));
        when(urlRepository.save(any(Url.class))).thenReturn(url);

        useCase.getOriginalUrl("abc123");

        assertEquals(6L, url.getAccessCount());
        verify(urlRepository).save(url);
    }

    @Test
    @DisplayName("getOriginalUrl should throw UrlNotFoundException when short code does not exist")
    void getOriginalUrl_shouldThrowUrlNotFoundException_whenNotFound() {
        when(urlRepository.findByShortCode("notfound")).thenReturn(Optional.empty());

        UrlNotFoundException exception = assertThrows(
                UrlNotFoundException.class,
                () -> useCase.getOriginalUrl("notfound")
        );

        assertTrue(exception.getMessage().contains("notfound"));
        verify(urlRepository, never()).save(any());
    }

    @Test
    @DisplayName("getOriginalUrl should throw ExpiredUrlException when URL is expired")
    void getOriginalUrl_shouldThrowExpiredUrlException_whenExpired() {
        Url url = createExpiredUrl();
        when(urlRepository.findByShortCode("expired")).thenReturn(Optional.of(url));

        ExpiredUrlException exception = assertThrows(
                ExpiredUrlException.class,
                () -> useCase.getOriginalUrl("expired")
        );

        assertTrue(exception.getMessage().contains("expired"));
        verify(urlRepository, never()).save(any());
    }

    @Test
    @DisplayName("getOriginalUrl should work when URL has no expiration date")
    void getOriginalUrl_shouldWork_whenNoExpirationDate() {
        Url url = createUrlWithoutExpiration();
        when(urlRepository.findByShortCode("noexpiry")).thenReturn(Optional.of(url));
        when(urlRepository.save(any(Url.class))).thenReturn(url);

        String result = useCase.getOriginalUrl("noexpiry");

        assertEquals("https://example.com", result);
    }

    private Url createValidUrl() {
        Url url = new Url();
        url.setId(1L);
        url.setOriginalUrl("https://example.com");
        url.setShortCode("abc123");
        url.setCreatedAt(LocalDateTime.now());
        url.setExpiresAt(LocalDateTime.now().plusDays(30));
        url.setAccessCount(0L);
        return url;
    }

    private Url createExpiredUrl() {
        Url url = new Url();
        url.setId(1L);
        url.setOriginalUrl("https://example.com");
        url.setShortCode("expired");
        url.setCreatedAt(LocalDateTime.now().minusDays(60));
        url.setExpiresAt(LocalDateTime.now().minusDays(30));
        url.setAccessCount(0L);
        return url;
    }

    private Url createUrlWithoutExpiration() {
        Url url = new Url();
        url.setId(1L);
        url.setOriginalUrl("https://example.com");
        url.setShortCode("noexpiry");
        url.setCreatedAt(LocalDateTime.now());
        url.setExpiresAt(null);
        url.setAccessCount(0L);
        return url;
    }
}
