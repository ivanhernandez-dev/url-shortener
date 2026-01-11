package dev.ivanhernandez.urlshortener.application.dto.response;

import java.time.LocalDateTime;

public record ShortUrlResponse(
        String shortUrl,
        String shortCode,
        String originalUrl,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {
}
