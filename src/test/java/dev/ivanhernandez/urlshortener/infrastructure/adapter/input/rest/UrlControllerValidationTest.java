package dev.ivanhernandez.urlshortener.infrastructure.adapter.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ivanhernandez.urlshortener.application.dto.response.ShortUrlResponse;
import dev.ivanhernandez.urlshortener.application.port.input.CreateShortUrlUseCase;
import dev.ivanhernandez.urlshortener.application.port.input.DeleteUrlUseCase;
import dev.ivanhernandez.urlshortener.application.port.input.GetUrlStatsUseCase;
import dev.ivanhernandez.urlshortener.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("UrlController - Validation Tests")
class UrlControllerValidationTest {

    @Mock
    private CreateShortUrlUseCase createShortUrlUseCase;

    @Mock
    private GetUrlStatsUseCase getUrlStatsUseCase;

    @Mock
    private DeleteUrlUseCase deleteUrlUseCase;

    @InjectMocks
    private UrlController urlController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(urlController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @ParameterizedTest
    @DisplayName("should reject invalid URL formats")
    @ValueSource(strings = {
            "not-a-url",
            "just-text",
            "www.no-protocol.com",
            "://missing-scheme.com",
            "htp://typo-protocol.com"
    })
    void createShortUrl_shouldReject_invalidUrlFormats(String invalidUrl) throws Exception {
        String requestBody = String.format("{\"originalUrl\": \"%s\"}", invalidUrl);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.originalUrl").exists());

        verify(createShortUrlUseCase, never()).createShortUrl(any());
    }

    @ParameterizedTest
    @DisplayName("should reject blank URLs")
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void createShortUrl_shouldReject_blankUrls(String blankUrl) throws Exception {
        String requestBody = String.format("{\"originalUrl\": \"%s\"}", blankUrl);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(createShortUrlUseCase, never()).createShortUrl(any());
    }

    @ParameterizedTest
    @DisplayName("should accept valid URL formats")
    @ValueSource(strings = {
            "https://example.com",
            "http://localhost:8080",
            "https://www.google.com/search?q=test",
            "http://192.168.1.1:3000/api"
    })
    void createShortUrl_shouldAccept_validUrlFormats(String validUrl) throws Exception {
        ShortUrlResponse mockResponse = new ShortUrlResponse(
                "http://localhost:8081/r/abc123",
                "abc123",
                validUrl,
                LocalDateTime.now(),
                null
        );
        when(createShortUrlUseCase.createShortUrl(any())).thenReturn(mockResponse);

        String requestBody = String.format("{\"originalUrl\": \"%s\"}", validUrl);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(createShortUrlUseCase).createShortUrl(any());
    }
}
