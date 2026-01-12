package dev.ivanhernandez.urlshortener.infrastructure.adapter.output.auth;

import java.util.List;
import java.util.UUID;

public record IntrospectResponse(
        boolean active,
        UUID userId,
        UUID tenantId,
        String tenantSlug,
        String email,
        List<String> roles
) {
}
