CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE TABLE IF NOT EXISTS tbl_interest (
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP   NOT NULL,
    updated_at       TIMESTAMP ,
    subscriber_count BIGINT      NOT NULL,
    name             VARCHAR(50) NOT NULL
    );
CREATE INDEX IF NOT EXISTS idx_interest_name_trgm ON tbl_interest USING gin (name gin_trgm_ops);
