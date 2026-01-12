package dev.ivanhernandez.urlshortener.infrastructure.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataUrlRepository extends JpaRepository<UrlJpaEntity, Long> {

    Optional<UrlJpaEntity> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    void deleteByShortCode(String shortCode);

    List<UrlJpaEntity> findByUserId(UUID userId);

    Optional<UrlJpaEntity> findByShortCodeAndUserId(String shortCode, UUID userId);
}
