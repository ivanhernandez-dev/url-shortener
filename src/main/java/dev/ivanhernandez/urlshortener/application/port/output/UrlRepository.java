package dev.ivanhernandez.urlshortener.application.port.output;

import dev.ivanhernandez.urlshortener.domain.model.Url;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UrlRepository {

    Url save(Url url);

    Optional<Url> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    void deleteByShortCode(String shortCode);

    List<Url> findByUserId(UUID userId);

    Optional<Url> findByShortCodeAndUserId(String shortCode, UUID userId);
}
