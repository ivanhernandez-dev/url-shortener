package dev.ivanhernandez.urlshortener.application.usecase;

import dev.ivanhernandez.urlshortener.application.dto.request.CreateUrlRequest;
import dev.ivanhernandez.urlshortener.application.dto.response.ShortUrlResponse;
import dev.ivanhernandez.urlshortener.application.port.output.UrlRepository;
import dev.ivanhernandez.urlshortener.domain.model.Url;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateShortUrlUseCaseImpl - Edge Cases")
class CreateShortUrlUseCaseImplEdgeCasesTest {

    @Mock
    private UrlRepository urlRepository;

    private CreateShortUrlUseCaseImpl useCase;

    private static final String BASE_URL = "http://localhost:8081";
    private static final int SHORT_CODE_LENGTH = 7;

    @BeforeEach
    void setUp() {
        useCase = new CreateShortUrlUseCaseImpl(urlRepository, BASE_URL, SHORT_CODE_LENGTH);
    }

    @ParameterizedTest
    @DisplayName("should treat blank custom alias as null and generate code")
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void createShortUrl_shouldTreatBlankAliasAsNull(String blankAlias) {
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", blankAlias, null);
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L);
            return url;
        });

        ShortUrlResponse response = useCase.createShortUrl(request);

        assertNotNull(response);
        assertEquals(SHORT_CODE_LENGTH, response.shortCode().length());
    }

    @Test
    @DisplayName("should handle very long URLs")
    void createShortUrl_shouldHandleVeryLongUrls() {
        String longUrl = "https://example.com/" + "a".repeat(2000);
        CreateUrlRequest request = new CreateUrlRequest(longUrl, null, null);
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L);
            return url;
        });

        ShortUrlResponse response = useCase.createShortUrl(request);

        assertNotNull(response);
        assertEquals(longUrl, response.originalUrl());
    }

    @Test
    @DisplayName("should handle URLs with special characters")
    void createShortUrl_shouldHandleUrlsWithSpecialCharacters() {
        String urlWithSpecialChars = "https://example.com/path?param=value&other=test#anchor";
        CreateUrlRequest request = new CreateUrlRequest(urlWithSpecialChars, null, null);
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L);
            return url;
        });

        ShortUrlResponse response = useCase.createShortUrl(request);

        assertEquals(urlWithSpecialChars, response.originalUrl());
    }

    @Test
    @DisplayName("should set createdAt to current time")
    void createShortUrl_shouldSetCreatedAtToCurrentTime() {
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", null, null);
        LocalDateTime beforeCreate = LocalDateTime.now();
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L);
            return url;
        });

        ShortUrlResponse response = useCase.createShortUrl(request);

        assertNotNull(response.createdAt());
        assertTrue(response.createdAt().isAfter(beforeCreate.minusSeconds(1)));
        assertTrue(response.createdAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("generated codes should only contain alphanumeric characters")
    void createShortUrl_generatedCodes_shouldBeAlphanumeric() {
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", null, null);
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L);
            return url;
        });

        ShortUrlResponse response = useCase.createShortUrl(request);

        assertTrue(response.shortCode().matches("^[A-Za-z0-9]+$"));
    }
}
