package dev.ivanhernandez.urlshortener.application.usecase;

import dev.ivanhernandez.urlshortener.application.port.input.GetOriginalUrlUseCase;
import dev.ivanhernandez.urlshortener.application.port.output.UrlRepository;
import dev.ivanhernandez.urlshortener.domain.exception.ExpiredUrlException;
import dev.ivanhernandez.urlshortener.domain.exception.UrlNotFoundException;
import dev.ivanhernandez.urlshortener.domain.model.Url;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class GetOriginalUrlUseCaseImpl implements GetOriginalUrlUseCase {

    private final UrlRepository urlRepository;

    public GetOriginalUrlUseCaseImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public String getOriginalUrl(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        if (url.isExpired()) {
            throw new ExpiredUrlException(shortCode);
        }

        url.incrementAccessCount();
        urlRepository.save(url);

        return url.getOriginalUrl();
    }
}
