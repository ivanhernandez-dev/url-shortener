package dev.ivanhernandez.urlshortener;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ivanhernandez.urlshortener.application.dto.request.CreateUrlRequest;
import dev.ivanhernandez.urlshortener.infrastructure.adapter.output.persistence.SpringDataUrlRepository;
import dev.ivanhernandez.urlshortener.infrastructure.adapter.output.persistence.UrlJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("URL Shortener Integration Tests")
class UrlShortenerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringDataUrlRepository urlRepository;

    @BeforeEach
    void setUp() {
        urlRepository.deleteAll();
    }

    @Test
    @DisplayName("createShortUrl should create and persist to database")
    void createShortUrl_shouldCreateAndPersist() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest(
                "https://example.com",
                "myalias",
                null
        );

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("myalias"))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8081/r/myalias"));

        assertTrue(urlRepository.existsByShortCode("myalias"));
    }

    @Test
    @DisplayName("createShortUrl should generate random code when no alias provided")
    void createShortUrl_shouldGenerateRandomCode_whenNoAlias() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest(
                "https://example.com",
                null,
                null
        );

        MvcResult result = mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").isNotEmpty())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("shortCode"));
        assertEquals(1, urlRepository.count());
    }

    @Test
    @DisplayName("createShortUrl should reject duplicate custom alias")
    void createShortUrl_shouldRejectDuplicateAlias() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest(
                "https://example.com",
                "duplicate",
                null
        );

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Custom alias already exists: duplicate"));
    }

    @Test
    @DisplayName("createShortUrl should reject invalid URL format")
    void createShortUrl_shouldRejectInvalidUrl() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest(
                "not-a-valid-url",
                null,
                null
        );

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.originalUrl").exists());
    }

    @Test
    @DisplayName("createShortUrl should reject blank URL")
    void createShortUrl_shouldRejectBlankUrl() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest(
                "",
                null,
                null
        );

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.originalUrl").exists());
    }

    @Test
    @DisplayName("redirect should redirect to original URL")
    void redirect_shouldRedirectToOriginalUrl() throws Exception {
        createTestUrl("redirect1", "https://example.com", null);

        mockMvc.perform(get("/r/redirect1"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }

    @Test
    @DisplayName("redirect should increment access count")
    void redirect_shouldIncrementAccessCount() throws Exception {
        createTestUrl("counter1", "https://example.com", null);

        mockMvc.perform(get("/r/counter1")).andExpect(status().isFound());
        mockMvc.perform(get("/r/counter1")).andExpect(status().isFound());
        mockMvc.perform(get("/r/counter1")).andExpect(status().isFound());

        UrlJpaEntity entity = urlRepository.findByShortCode("counter1").orElseThrow();
        assertEquals(3L, entity.getAccessCount());
        assertNotNull(entity.getLastAccessedAt());
    }

    @Test
    @DisplayName("redirect should return 404 for non-existent short code")
    void redirect_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(get("/r/doesnotexist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("redirect should return 410 for expired URL")
    void redirect_shouldReturn410_whenExpired() throws Exception {
        createTestUrl("expired1", "https://example.com", LocalDateTime.now().minusDays(1));

        mockMvc.perform(get("/r/expired1"))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.status").value(410));
    }

    @Test
    @DisplayName("getStats should return URL statistics")
    void getStats_shouldReturnStats() throws Exception {
        createTestUrl("stats1", "https://example.com", null);

        mockMvc.perform(get("/r/stats1")).andExpect(status().isFound());
        mockMvc.perform(get("/r/stats1")).andExpect(status().isFound());

        mockMvc.perform(get("/api/v1/urls/stats1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("stats1"))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"))
                .andExpect(jsonPath("$.accessCount").value(2))
                .andExpect(jsonPath("$.lastAccessedAt").isNotEmpty());
    }

    @Test
    @DisplayName("getStats should return 404 for non-existent short code")
    void getStats_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/urls/doesnotexist/stats"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("deleteUrl should delete URL from database")
    void deleteUrl_shouldDelete() throws Exception {
        createTestUrl("todelete", "https://example.com", null);
        assertTrue(urlRepository.existsByShortCode("todelete"));

        mockMvc.perform(delete("/api/v1/urls/todelete"))
                .andExpect(status().isNoContent());

        assertFalse(urlRepository.existsByShortCode("todelete"));
    }

    @Test
    @DisplayName("deleteUrl should return 404 for non-existent short code")
    void deleteUrl_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/urls/doesnotexist"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("full lifecycle: create, access, stats, delete")
    void fullLifecycle_shouldWork() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest(
                "https://github.com",
                "lifecycle",
                LocalDateTime.now().plusDays(30)
        );
        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/r/lifecycle"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://github.com"));

        mockMvc.perform(get("/api/v1/urls/lifecycle/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessCount").value(1));

        mockMvc.perform(delete("/api/v1/urls/lifecycle"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/r/lifecycle"))
                .andExpect(status().isNotFound());
    }

    private void createTestUrl(String shortCode, String originalUrl, LocalDateTime expiresAt) {
        UrlJpaEntity entity = new UrlJpaEntity();
        entity.setShortCode(shortCode);
        entity.setOriginalUrl(originalUrl);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setExpiresAt(expiresAt);
        entity.setAccessCount(0L);
        urlRepository.save(entity);
    }
}
