package dev.ivanhernandez.urlshortener.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public record CreateUrlRequest(
        @NotBlank(message = "Original URL is required")
        @URL(message = "Invalid URL format")
        String originalUrl,

        String customAlias,

        LocalDateTime expiresAt
) {
}
