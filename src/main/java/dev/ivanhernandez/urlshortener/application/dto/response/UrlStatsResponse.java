package dev.ivanhernandez.urlshortener.application.dto.response;

import java.time.LocalDateTime;

public record UrlStatsResponse(
        String shortCode,
        String originalUrl,
        Long accessCount,
        LocalDateTime createdAt,
        LocalDateTime lastAccessedAt
) {
}
