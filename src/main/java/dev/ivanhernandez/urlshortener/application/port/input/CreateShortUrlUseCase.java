package dev.ivanhernandez.urlshortener.application.port.input;

import dev.ivanhernandez.urlshortener.application.dto.request.CreateUrlRequest;
import dev.ivanhernandez.urlshortener.application.dto.response.ShortUrlResponse;

public interface CreateShortUrlUseCase {

    ShortUrlResponse createShortUrl(CreateUrlRequest request);
}
