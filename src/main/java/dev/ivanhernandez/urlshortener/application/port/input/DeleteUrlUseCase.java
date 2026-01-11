package dev.ivanhernandez.urlshortener.application.port.input;

public interface DeleteUrlUseCase {

    void deleteUrl(String shortCode);
}
