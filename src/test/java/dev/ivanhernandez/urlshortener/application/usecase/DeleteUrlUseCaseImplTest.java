package dev.ivanhernandez.urlshortener.application.usecase;

import dev.ivanhernandez.urlshortener.application.port.output.UrlRepository;
import dev.ivanhernandez.urlshortener.domain.exception.UrlNotFoundException;
import dev.ivanhernandez.urlshortener.domain.exception.UrlOwnershipException;
import dev.ivanhernandez.urlshortener.domain.model.Url;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

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
    @DisplayName("deleteUrl should delete anonymous URL")
    void deleteUrl_shouldDelete_whenUrlIsAnonymous() {
        Url anonymousUrl = new Url();
        anonymousUrl.setShortCode("abc123");
        anonymousUrl.setUserId(null);

        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(anonymousUrl));

        assertDoesNotThrow(() -> useCase.deleteUrl("abc123"));

        verify(urlRepository).deleteByShortCode("abc123");
    }

    @Test
    @DisplayName("deleteUrl should throw UrlNotFoundException when URL does not exist")
    void deleteUrl_shouldThrowUrlNotFoundException_whenNotFound() {
        when(urlRepository.findByShortCode("notfound")).thenReturn(Optional.empty());

        UrlNotFoundException exception = assertThrows(
                UrlNotFoundException.class,
                () -> useCase.deleteUrl("notfound")
        );

        assertTrue(exception.getMessage().contains("notfound"));
        verify(urlRepository, never()).deleteByShortCode(anyString());
    }

    @Test
    @DisplayName("deleteUrl should throw UrlOwnershipException when URL belongs to a user")
    void deleteUrl_shouldThrowUrlOwnershipException_whenUrlHasOwner() {
        Url ownedUrl = new Url();
        ownedUrl.setShortCode("owned123");
        ownedUrl.setUserId(UUID.randomUUID());

        when(urlRepository.findByShortCode("owned123")).thenReturn(Optional.of(ownedUrl));

        UrlOwnershipException exception = assertThrows(
                UrlOwnershipException.class,
                () -> useCase.deleteUrl("owned123")
        );

        assertTrue(exception.getMessage().contains("owned123"));
        verify(urlRepository, never()).deleteByShortCode(anyString());
    }
}
