package dev.ivanhernandez.urlshortener.infrastructure.adapter.output.persistence;

import dev.ivanhernandez.urlshortener.domain.model.Url;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
public class UrlJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String originalUrl;

    @Column(nullable = false, unique = true, length = 20)
    private String shortCode;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Long accessCount = 0L;

    private LocalDateTime lastAccessedAt;

    public UrlJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Long getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(Long accessCount) {
        this.accessCount = accessCount;
    }

    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public static UrlJpaEntity fromDomain(Url url) {
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

    public Url toDomain() {
        return new Url(
                this.id,
                this.originalUrl,
                this.shortCode,
                this.createdAt,
                this.expiresAt,
                this.accessCount,
                this.lastAccessedAt
        );
    }
}
