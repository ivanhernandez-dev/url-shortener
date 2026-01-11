package dev.ivanhernandez.urlshortener.application.usecase;

import dev.ivanhernandez.urlshortener.application.port.input.DeleteUrlUseCase;
import dev.ivanhernandez.urlshortener.application.port.output.UrlRepository;
import dev.ivanhernandez.urlshortener.domain.exception.UrlNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class DeleteUrlUseCaseImpl implements DeleteUrlUseCase {

    private final UrlRepository urlRepository;

    public DeleteUrlUseCaseImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public void deleteUrl(String shortCode) {
        if (!urlRepository.existsByShortCode(shortCode)) {
            throw new UrlNotFoundException(shortCode);
        }
        urlRepository.deleteByShortCode(shortCode);
    }
}
