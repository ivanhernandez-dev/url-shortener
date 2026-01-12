package dev.ivanhernandez.urlshortener.application.port.input;

import java.util.UUID;

public interface DeleteUserUrlUseCase {

    void deleteUserUrl(String shortCode, UUID userId);
}
