package dev.ivanhernandez.urlshortener.application.port.input;

import dev.ivanhernandez.urlshortener.application.dto.response.UrlStatsResponse;

public interface GetUrlStatsUseCase {

    UrlStatsResponse getUrlStats(String shortCode);
}
