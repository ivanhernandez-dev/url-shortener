package dev.ivanhernandez.urlshortener.infrastructure.adapter.output.persistence;

import dev.ivanhernandez.urlshortener.domain.model.Url;
import org.springframework.stereotype.Component;

@Component
public class UrlMapper {

    public UrlJpaEntity toEntity(Url url) {
        UrlJpaEntity entity = new UrlJpaEntity();
        entity.setId(url.getId());
        entity.setOriginalUrl(url.getOriginalUrl());
        entity.setShortCode(url.getShortCode());
        entity.setCreatedAt(url.getCreatedAt());
        entity.setExpiresAt(url.getExpiresAt());
        entity.setAccessCount(url.getAccessCount());
        entity.setLastAccessedAt(url.getLastAccessedAt());
        return entity;
    }

    public Url toDomain(UrlJpaEntity entity) {
        return new Url(
                entity.getId(),
                entity.getOriginalUrl(),
                entity.getShortCode(),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getAccessCount(),
                entity.getLastAccessedAt()
        );
    }
}
