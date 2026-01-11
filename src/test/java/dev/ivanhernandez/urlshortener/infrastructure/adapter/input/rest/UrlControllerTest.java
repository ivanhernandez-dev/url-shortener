package dev.ivanhernandez.urlshortener.infrastructure.adapter.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ivanhernandez.urlshortener.application.dto.request.CreateUrlRequest;
import dev.ivanhernandez.urlshortener.application.dto.response.ShortUrlResponse;
import dev.ivanhernandez.urlshortener.application.dto.response.UrlStatsResponse;
import dev.ivanhernandez.urlshortener.application.port.input.CreateShortUrlUseCase;
import dev.ivanhernandez.urlshortener.application.port.input.DeleteUrlUseCase;
import dev.ivanhernandez.urlshortener.application.port.input.GetUrlStatsUseCase;
import dev.ivanhernandez.urlshortener.domain.exception.InvalidUrlException;
import dev.ivanhernandez.urlshortener.domain.exception.UrlNotFoundException;
import dev.ivanhernandez.urlshortener.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("UrlController")
class UrlControllerTest {

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

    @Test
    @DisplayName("POST /api/v1/urls should return 201 Created with response body")
    void createShortUrl_shouldReturn201Created() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", null, null);
        ShortUrlResponse response = new ShortUrlResponse(
                "http://localhost:8081/r/abc123",
                "abc123",
                "https://example.com",
                LocalDateTime.now(),
                null
        );
        when(createShortUrlUseCase.createShortUrl(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"));

        verify(createShortUrlUseCase).createShortUrl(any());
    }

    @Test
    @DisplayName("POST /api/v1/urls should return 400 Bad Request when URL is blank")
    void createShortUrl_shouldReturn400_whenUrlBlank() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest("", null, null);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"));

        verify(createShortUrlUseCase, never()).createShortUrl(any());
    }

    @Test
    @DisplayName("POST /api/v1/urls should return 400 Bad Request when URL format is invalid")
    void createShortUrl_shouldReturn400_whenUrlInvalid() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest("not-a-url", null, null);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(createShortUrlUseCase, never()).createShortUrl(any());
    }

    @Test
    @DisplayName("POST /api/v1/urls should return 400 Bad Request when custom alias already exists")
    void createShortUrl_shouldReturn400_whenAliasExists() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", "existing", null);
        when(createShortUrlUseCase.createShortUrl(any()))
                .thenThrow(new InvalidUrlException("Custom alias already exists: existing"));

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Custom alias already exists: existing"));
    }

    @Test
    @DisplayName("GET /api/v1/urls/{shortCode}/stats should return 200 OK with stats")
    void getStats_shouldReturn200WithStats() throws Exception {
        UrlStatsResponse response = new UrlStatsResponse(
                "abc123",
                "https://example.com",
                42L,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now().minusHours(1)
        );
        when(getUrlStatsUseCase.getUrlStats("abc123")).thenReturn(response);

        mockMvc.perform(get("/api/v1/urls/abc123/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.accessCount").value(42));

        verify(getUrlStatsUseCase).getUrlStats("abc123");
    }

    @Test
    @DisplayName("GET /api/v1/urls/{shortCode}/stats should return 404 Not Found when URL does not exist")
    void getStats_shouldReturn404_whenNotFound() throws Exception {
        when(getUrlStatsUseCase.getUrlStats("notfound"))
                .thenThrow(new UrlNotFoundException("notfound"));

        mockMvc.perform(get("/api/v1/urls/notfound/stats"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("DELETE /api/v1/urls/{shortCode} should return 204 No Content when deleted successfully")
    void deleteUrl_shouldReturn204_whenDeleted() throws Exception {
        doNothing().when(deleteUrlUseCase).deleteUrl("abc123");

        mockMvc.perform(delete("/api/v1/urls/abc123"))
                .andExpect(status().isNoContent());

        verify(deleteUrlUseCase).deleteUrl("abc123");
    }

    @Test
    @DisplayName("DELETE /api/v1/urls/{shortCode} should return 404 Not Found when URL does not exist")
    void deleteUrl_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new UrlNotFoundException("notfound")).when(deleteUrlUseCase).deleteUrl("notfound");

        mockMvc.perform(delete("/api/v1/urls/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
