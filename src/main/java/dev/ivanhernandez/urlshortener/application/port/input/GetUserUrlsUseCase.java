package dev.ivanhernandez.urlshortener.application.port.input;

import dev.ivanhernandez.urlshortener.application.dto.response.ShortUrlResponse;

import java.util.List;
import java.util.UUID;

public interface GetUserUrlsUseCase {

    List<ShortUrlResponse> getUserUrls(UUID userId);
}
