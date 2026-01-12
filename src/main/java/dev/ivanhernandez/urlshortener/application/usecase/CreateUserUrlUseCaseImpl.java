package dev.ivanhernandez.urlshortener.application.usecase;

import dev.ivanhernandez.urlshortener.application.dto.request.CreateUrlRequest;
import dev.ivanhernandez.urlshortener.application.dto.response.ShortUrlResponse;
import dev.ivanhernandez.urlshortener.application.port.input.CreateUserUrlUseCase;
import dev.ivanhernandez.urlshortener.application.port.output.UrlRepository;
import dev.ivanhernandez.urlshortener.domain.exception.InvalidUrlException;
import dev.ivanhernandez.urlshortener.domain.model.Url;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Transactional
public class CreateUserUrlUseCaseImpl implements CreateUserUrlUseCase {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UrlRepository urlRepository;
    private final String baseUrl;
    private final int shortCodeLength;

    public CreateUserUrlUseCaseImpl(
            UrlRepository urlRepository,
            @Value("${app.base-url}") String baseUrl,
            @Value("${app.short-code.length}") int shortCodeLength) {
        this.urlRepository = urlRepository;
        this.baseUrl = baseUrl;
        this.shortCodeLength = shortCodeLength;
    }

    @Override
    public ShortUrlResponse createUserUrl(CreateUrlRequest request, UUID userId, UUID tenantId) {
        String shortCode = resolveShortCode(request.customAlias());

        Url url = new Url();
        url.setOriginalUrl(request.originalUrl());
        url.setShortCode(shortCode);
        url.setUserId(userId);
        url.setTenantId(tenantId);
        url.setCreatedAt(LocalDateTime.now());
        url.setExpiresAt(request.expiresAt());
        url.setAccessCount(0L);

        Url savedUrl = urlRepository.save(url);

        return ShortUrlResponse.fromDomain(savedUrl, baseUrl);
    }

    private String resolveShortCode(String customAlias) {
        if (customAlias != null && !customAlias.isBlank()) {
            if (urlRepository.existsByShortCode(customAlias)) {
                throw new InvalidUrlException("Custom alias already exists: " + customAlias);
            }
            return customAlias;
        }
        return generateUniqueShortCode();
    }

    private String generateUniqueShortCode() {
        String shortCode;
        do {
            shortCode = generateRandomCode();
        } while (urlRepository.existsByShortCode(shortCode));
        return shortCode;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(shortCodeLength);
        for (int i = 0; i < shortCodeLength; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
