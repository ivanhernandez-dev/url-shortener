package dev.ivanhernandez.urlshortener.domain.exception;

public class UrlOwnershipException extends RuntimeException {

    public UrlOwnershipException(String shortCode) {
        super("Cannot delete URL with short code: " + shortCode + ". URL belongs to a user.");
    }
}
