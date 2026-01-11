package dev.ivanhernandez.urlshortener.application.dto.response;

import dev.ivanhernandez.urlshortener.domain.model.Url;

import java.time.LocalDateTime;

public record ShortUrlResponse(
        String shortUrl,
        String shortCode,
        String originalUrl,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {
    public static ShortUrlResponse fromDomain(Url url, String baseUrl) {
        return new ShortUrlResponse(
                baseUrl + "/r/" + url.getShortCode(),
                url.getShortCode(),
                url.getOriginalUrl(),
                url.getCreatedAt(),
                url.getExpiresAt()
        );
    }
}
