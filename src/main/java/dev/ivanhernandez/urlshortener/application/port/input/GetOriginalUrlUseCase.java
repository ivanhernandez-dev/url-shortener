package dev.ivanhernandez.urlshortener.application.port.input;

public interface GetOriginalUrlUseCase {

    String getOriginalUrl(String shortCode);
}
