package dev.ivanhernandez.urlshortener.infrastructure.adapter.input.rest;

import dev.ivanhernandez.urlshortener.application.port.input.GetOriginalUrlUseCase;
import dev.ivanhernandez.urlshortener.domain.exception.ExpiredUrlException;
import dev.ivanhernandez.urlshortener.domain.exception.UrlNotFoundException;
import dev.ivanhernandez.urlshortener.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedirectController")
class RedirectControllerTest {

    @Mock
    private GetOriginalUrlUseCase getOriginalUrlUseCase;

    @InjectMocks
    private RedirectController redirectController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(redirectController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /r/{shortCode} should return 302 Found with Location header")
    void redirect_shouldReturn302WithLocation() throws Exception {
        when(getOriginalUrlUseCase.getOriginalUrl("abc123"))
                .thenReturn("https://example.com");

        mockMvc.perform(get("/r/abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));

        verify(getOriginalUrlUseCase).getOriginalUrl("abc123");
    }

    @Test
    @DisplayName("GET /r/{shortCode} should return 404 Not Found when URL does not exist")
    void redirect_shouldReturn404_whenNotFound() throws Exception {
        when(getOriginalUrlUseCase.getOriginalUrl("notfound"))
                .thenThrow(new UrlNotFoundException("notfound"));

        mockMvc.perform(get("/r/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /r/{shortCode} should return 410 Gone when URL is expired")
    void redirect_shouldReturn410_whenExpired() throws Exception {
        when(getOriginalUrlUseCase.getOriginalUrl("expired"))
                .thenThrow(new ExpiredUrlException("expired"));

        mockMvc.perform(get("/r/expired"))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.status").value(410));
    }
}
