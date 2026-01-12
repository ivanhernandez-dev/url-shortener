package dev.ivanhernandez.urlshortener.application.usecase;

import dev.ivanhernandez.urlshortener.application.port.input.DeleteUserUrlUseCase;
import dev.ivanhernandez.urlshortener.application.port.output.UrlRepository;
import dev.ivanhernandez.urlshortener.domain.exception.UrlNotFoundException;
import dev.ivanhernandez.urlshortener.domain.model.Url;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Transactional
public class DeleteUserUrlUseCaseImpl implements DeleteUserUrlUseCase {

    private final UrlRepository urlRepository;

    public DeleteUserUrlUseCaseImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public void deleteUserUrl(String shortCode, UUID userId) {
        Url url = urlRepository.findByShortCodeAndUserId(shortCode, userId)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        urlRepository.deleteByShortCode(shortCode);
    }
}
