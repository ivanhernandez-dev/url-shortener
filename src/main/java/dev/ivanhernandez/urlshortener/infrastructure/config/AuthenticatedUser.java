package dev.ivanhernandez.urlshortener.infrastructure.config;

import java.util.UUID;

public record AuthenticatedUser(UUID userId, UUID tenantId) {
}
