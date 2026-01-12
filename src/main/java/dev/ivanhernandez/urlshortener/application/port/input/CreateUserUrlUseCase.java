package dev.ivanhernandez.urlshortener.application.port.input;

import dev.ivanhernandez.urlshortener.application.dto.request.CreateUrlRequest;
import dev.ivanhernandez.urlshortener.application.dto.response.ShortUrlResponse;

import java.util.UUID;

public interface CreateUserUrlUseCase {

    ShortUrlResponse createUserUrl(CreateUrlRequest request, UUID userId, UUID tenantId);
}
