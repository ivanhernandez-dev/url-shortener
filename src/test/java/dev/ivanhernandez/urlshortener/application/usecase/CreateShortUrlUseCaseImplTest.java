package dev.ivanhernandez.urlshortener.application.usecase;

import dev.ivanhernandez.urlshortener.application.dto.request.CreateUrlRequest;
import dev.ivanhernandez.urlshortener.application.dto.response.ShortUrlResponse;
import dev.ivanhernandez.urlshortener.application.port.output.UrlRepository;
import dev.ivanhernandez.urlshortener.domain.exception.InvalidUrlException;
import dev.ivanhernandez.urlshortener.domain.model.Url;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("CreateShortUrlUseCaseImpl")
class CreateShortUrlUseCaseImplTest {

    @MockitoBean
    private UrlRepository urlRepository;

    @Autowired
    private CreateShortUrlUseCaseImpl useCase;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.short-code.length}")
    private int shortCodeLength;

    @Test
    @DisplayName("createShortUrl should create URL with generated code when no custom alias")
    void createShortUrl_shouldCreateWithGeneratedCode_whenNoCustomAlias() {
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", null, null);
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L);
            return url;
        });

        ShortUrlResponse response = useCase.createShortUrl(request);

        assertNotNull(response);
        assertEquals("https://example.com", response.originalUrl());
        assertEquals(shortCodeLength, response.shortCode().length());
        assertTrue(response.shortUrl().startsWith(baseUrl + "/r/"));
        verify(urlRepository).save(any(Url.class));
    }

    @Test
    @DisplayName("createShortUrl should create URL with custom alias")
    void createShortUrl_shouldCreateWithCustomAlias() {
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", "myalias", null);
        when(urlRepository.existsByShortCode("myalias")).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L);
            return url;
        });

        ShortUrlResponse response = useCase.createShortUrl(request);

        assertNotNull(response);
        assertEquals("myalias", response.shortCode());
        assertEquals(baseUrl + "/r/myalias", response.shortUrl());
    }

    @Test
    @DisplayName("createShortUrl should throw InvalidUrlException when custom alias already exists")
    void createShortUrl_shouldThrowInvalidUrlException_whenCustomAliasExists() {
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", "existing", null);
        when(urlRepository.existsByShortCode("existing")).thenReturn(true);

        assertThrows(InvalidUrlException.class, () -> useCase.createShortUrl(request));
        verify(urlRepository, never()).save(any());
    }

    @Test
    @DisplayName("createShortUrl should set expiration date when provided")
    void createShortUrl_shouldSetExpirationDate_whenProvided() {
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", null, expiresAt);
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L);
            return url;
        });

        ShortUrlResponse response = useCase.createShortUrl(request);

        assertEquals(expiresAt, response.expiresAt());
    }

    @Test
    @DisplayName("createShortUrl should initialize access count to zero")
    void createShortUrl_shouldInitializeAccessCountToZero() {
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", null, null);
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        ArgumentCaptor<Url> urlCaptor = ArgumentCaptor.forClass(Url.class);
        when(urlRepository.save(urlCaptor.capture())).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L);
            return url;
        });

        useCase.createShortUrl(request);

        assertEquals(0L, urlCaptor.getValue().getAccessCount());
    }

    @Test
    @DisplayName("createShortUrl should regenerate code if collision occurs")
    void createShortUrl_shouldRegenerateCode_whenCollisionOccurs() {
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", null, null);
        when(urlRepository.existsByShortCode(anyString()))
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L);
            return url;
        });

        ShortUrlResponse response = useCase.createShortUrl(request);

        assertNotNull(response);
        verify(urlRepository, atLeast(3)).existsByShortCode(anyString());
    }
}
