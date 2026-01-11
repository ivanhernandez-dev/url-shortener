package dev.ivanhernandez.urlshortener.application.usecase;

import dev.ivanhernandez.urlshortener.application.dto.response.UrlStatsResponse;
import dev.ivanhernandez.urlshortener.application.port.input.GetUrlStatsUseCase;
import dev.ivanhernandez.urlshortener.application.port.output.UrlRepository;
import dev.ivanhernandez.urlshortener.domain.exception.UrlNotFoundException;
import dev.ivanhernandez.urlshortener.domain.model.Url;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class GetUrlStatsUseCaseImpl implements GetUrlStatsUseCase {

    private final UrlRepository urlRepository;

    public GetUrlStatsUseCaseImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public UrlStatsResponse getUrlStats(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        return UrlStatsResponse.fromDomain(url);
    }
}
