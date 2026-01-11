package dev.ivanhernandez.urlshortener.application.usecase;

import dev.ivanhernandez.urlshortener.application.port.output.UrlRepository;
import dev.ivanhernandez.urlshortener.domain.exception.UrlNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteUrlUseCaseImpl")
class DeleteUrlUseCaseImplTest {

    @Mock
    private UrlRepository urlRepository;

    private DeleteUrlUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteUrlUseCaseImpl(urlRepository);
    }

    @Test
    @DisplayName("deleteUrl should delete URL when it exists")
    void deleteUrl_shouldDelete_whenUrlExists() {
        when(urlRepository.existsByShortCode("abc123")).thenReturn(true);

        assertDoesNotThrow(() -> useCase.deleteUrl("abc123"));

        verify(urlRepository).deleteByShortCode("abc123");
    }

    @Test
    @DisplayName("deleteUrl should throw UrlNotFoundException when URL does not exist")
    void deleteUrl_shouldThrowUrlNotFoundException_whenNotFound() {
        when(urlRepository.existsByShortCode("notfound")).thenReturn(false);

        UrlNotFoundException exception = assertThrows(
                UrlNotFoundException.class,
                () -> useCase.deleteUrl("notfound")
        );

        assertTrue(exception.getMessage().contains("notfound"));
        verify(urlRepository, never()).deleteByShortCode(anyString());
    }
}
