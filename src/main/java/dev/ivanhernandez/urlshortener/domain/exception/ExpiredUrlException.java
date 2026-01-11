package dev.ivanhernandez.urlshortener.domain.exception;

public class ExpiredUrlException extends RuntimeException {

    public ExpiredUrlException(String shortCode) {
        super("URL has expired: " + shortCode);
    }
}
