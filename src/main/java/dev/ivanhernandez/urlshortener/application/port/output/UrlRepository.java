package dev.ivanhernandez.urlshortener.application.port.output;

import dev.ivanhernandez.urlshortener.domain.model.Url;

import java.util.Optional;

public interface UrlRepository {

    Url save(Url url);

    Optional<Url> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    void deleteByShortCode(String shortCode);
}
