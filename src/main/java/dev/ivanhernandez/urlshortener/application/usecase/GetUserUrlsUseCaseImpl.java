package dev.ivanhernandez.urlshortener.application.usecase;

import dev.ivanhernandez.urlshortener.application.dto.response.ShortUrlResponse;
import dev.ivanhernandez.urlshortener.application.port.input.GetUserUrlsUseCase;
import dev.ivanhernandez.urlshortener.application.port.output.UrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class GetUserUrlsUseCaseImpl implements GetUserUrlsUseCase {

    private final UrlRepository urlRepository;
    private final String baseUrl;

    public GetUserUrlsUseCaseImpl(
            UrlRepository urlRepository,
            @Value("${app.base-url}") String baseUrl) {
        this.urlRepository = urlRepository;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<ShortUrlResponse> getUserUrls(UUID userId) {
        return urlRepository.findByUserId(userId).stream()
                .map(url -> ShortUrlResponse.fromDomain(url, baseUrl))
                .toList();
    }
}
