package dev.ivanhernandez.urlshortener.application.usecase;

import dev.ivanhernandez.urlshortener.application.dto.response.UrlStatsResponse;
import dev.ivanhernandez.urlshortener.application.port.input.GetUserUrlStatsUseCase;
import dev.ivanhernandez.urlshortener.application.port.output.UrlRepository;
import dev.ivanhernandez.urlshortener.domain.exception.UrlNotFoundException;
import dev.ivanhernandez.urlshortener.domain.model.Url;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class GetUserUrlStatsUseCaseImpl implements GetUserUrlStatsUseCase {

    private final UrlRepository urlRepository;

    public GetUserUrlStatsUseCaseImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public UrlStatsResponse getUserUrlStats(String shortCode, UUID userId) {
        Url url = urlRepository.findByShortCodeAndUserId(shortCode, userId)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        return UrlStatsResponse.fromDomain(url);
    }
}
