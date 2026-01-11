CREATE TABLE IF NOT EXISTS urls (
    id               BIGSERIAL PRIMARY KEY,
    original_url     VARCHAR(2048) NOT NULL,
    short_code       VARCHAR(20) NOT NULL UNIQUE,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at       TIMESTAMP,
    access_count     BIGINT NOT NULL DEFAULT 0,
    last_accessed_at TIMESTAMP,
    
    CONSTRAINT uk_short_code UNIQUE (short_code)
);

CREATE INDEX IF NOT EXISTS idx_short_code ON urls(short_code);
CREATE INDEX IF NOT EXISTS idx_expires_at ON urls(expires_at);
