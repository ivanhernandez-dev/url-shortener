package dev.ivanhernandez.urlshortener.infrastructure.adapter.output.persistence;

import dev.ivanhernandez.urlshortener.application.port.output.UrlRepository;
import dev.ivanhernandez.urlshortener.domain.model.Url;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUrlRepository implements UrlRepository {

    private final SpringDataUrlRepository springDataUrlRepository;
    private final UrlMapper urlMapper;

    public JpaUrlRepository(SpringDataUrlRepository springDataUrlRepository, UrlMapper urlMapper) {
        this.springDataUrlRepository = springDataUrlRepository;
        this.urlMapper = urlMapper;
    }

    @Override
    public Url save(Url url) {
        UrlJpaEntity entity = urlMapper.toEntity(url);
        UrlJpaEntity savedEntity = springDataUrlRepository.save(entity);
        return urlMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Url> findByShortCode(String shortCode) {
        return springDataUrlRepository.findByShortCode(shortCode)
                .map(urlMapper::toDomain);
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
