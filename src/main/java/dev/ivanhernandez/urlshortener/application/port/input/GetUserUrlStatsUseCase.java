package dev.ivanhernandez.urlshortener.application.port.input;

import dev.ivanhernandez.urlshortener.application.dto.response.UrlStatsResponse;

import java.util.UUID;

public interface GetUserUrlStatsUseCase {

    UrlStatsResponse getUserUrlStats(String shortCode, UUID userId);
}
