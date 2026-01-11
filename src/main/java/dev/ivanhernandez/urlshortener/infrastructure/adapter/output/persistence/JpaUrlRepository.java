package dev.ivanhernandez.urlshortener.infrastructure.adapter.output.persistence;

import dev.ivanhernandez.urlshortener.application.port.output.UrlRepository;
import dev.ivanhernandez.urlshortener.domain.model.Url;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUrlRepository implements UrlRepository {

    private final SpringDataUrlRepository springDataUrlRepository;

    public JpaUrlRepository(SpringDataUrlRepository springDataUrlRepository) {
        this.springDataUrlRepository = springDataUrlRepository;
    }

    @Override
    public Url save(Url url) {
        UrlJpaEntity entity = UrlJpaEntity.fromDomain(url);
        UrlJpaEntity savedEntity = springDataUrlRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Url> findByShortCode(String shortCode) {
        return springDataUrlRepository.findByShortCode(shortCode)
                .map(UrlJpaEntity::toDomain);
    }

    @Override
    public boolean existsByShortCode(String shortCode) {
        return springDataUrlRepository.existsByShortCode(shortCode);
    }

    @Override
    public void deleteByShortCode(String shortCode) {
        springDataUrlRepository.deleteByShortCode(shortCode);
    }
}
