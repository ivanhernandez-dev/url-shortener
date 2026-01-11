package dev.ivanhernandez.urlshortener.application.dto.response;

import dev.ivanhernandez.urlshortener.domain.model.Url;

import java.time.LocalDateTime;

public record UrlStatsResponse(
        String shortCode,
        String originalUrl,
        Long accessCount,
        LocalDateTime createdAt,
        LocalDateTime lastAccessedAt
) {
    public static UrlStatsResponse fromDomain(Url url) {
        return new UrlStatsResponse(
                url.getShortCode(),
                url.getOriginalUrl(),
                url.getAccessCount(),
                url.getCreatedAt(),
                url.getLastAccessedAt()
        );
    }
}
