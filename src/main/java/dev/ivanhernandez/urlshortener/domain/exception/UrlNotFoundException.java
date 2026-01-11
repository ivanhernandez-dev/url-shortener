package dev.ivanhernandez.urlshortener.domain.exception;

public class UrlNotFoundException extends RuntimeException {

    public UrlNotFoundException(String shortCode) {
        super("URL not found with short code: " + shortCode);
    }
}
