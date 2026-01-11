package dev.ivanhernandez.urlshortener.infrastructure.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataUrlRepository extends JpaRepository<UrlJpaEntity, Long> {

    Optional<UrlJpaEntity> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    void deleteByShortCode(String shortCode);
}
