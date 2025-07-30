-- postgrest 초기 설정(라이브러리 추가 + interest에 적용)
CREATE EXTENSION IF NOT EXISTS pg_trgm;


-- 테이블 생성
DROP TABLE IF EXISTS tbl_keyword CASCADE ;
DROP TABLE IF EXISTS tbl_user_interest CASCADE ;
DROP TABLE IF EXISTS tbl_interest CASCADE ;
DROP TABLE IF EXISTS tbl_user CASCADE;
DROP TABLE IF EXISTS tbl_comment CASCADE;
DROP TABLE IF EXISTS tbl_like CASCADE;
DROP TABLE IF EXISTS tbl_notification CASCADE;
DROP TABLE IF EXISTS tbl_article CASCADE;
DROP TABLE IF EXISTS tbl_article_count CASCADE;
DROP TABLE IF EXISTS tbl_article_keyword CASCADE;



-- User
-- 댓글, 좋아요, 알림, 관심사, 뉴스 조회수
CREATE TABLE tbl_user
(
    id         uuid PRIMARY KEY,
    email      varchar(100) UNIQUE   NOT NULL,
    nickname   varchar(20)           NOT NULL,
    password   varchar(20)           NOT NULL,
    created_at TIMESTAMPTZ           NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL
);


-- 뉴스
CREATE TABLE tbl_article
(
    id                  UUID PRIMARY KEY,
    source              VARCHAR(5)            NOT NULL,
    source_url          TEXT UNIQUE           NOT NULL,
    title               TEXT                  NOT NULL,
    summary             TEXT                  NOT NULL,
    is_deleted          BOOLEAN DEFAULT FALSE NOT NULL,
    original_created_at TIMESTAMPTZ           NOT NULL,
    created_at          TIMESTAMPTZ           NOT NULL
);

-- 뉴스 조회수
CREATE TABLE tbl_article_count
(
    id         UUID PRIMARY KEY,
    article_id UUID NOT NULL,
    user_id    UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    FOREIGN KEY (article_id) REFERENCES tbl_article (id) on delete cascade,
    FOREIGN KEY (user_id) REFERENCES tbl_user (id) on delete cascade
);


-- 관심사
CREATE TABLE IF NOT EXISTS tbl_interest
(
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMPTZ NOT NULL,
    updated_at       TIMESTAMPTZ,
    subscriber_count BIGINT      NOT NULL,
    name             VARCHAR(50) NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_interest_lower_name_trgm ON tbl_interest USING gin (lower(name) gin_trgm_ops);

-- 뉴스 관심사
CREATE TABLE tbl_article_keyword
(
    id          UUID PRIMARY KEY,
    article_id  UUID NOT NULL,
    interest_id UUID NOT NULL,
    FOREIGN KEY (article_id) REFERENCES tbl_article (id) on delete cascade,
    FOREIGN KEY (interest_id) REFERENCES tbl_interest (id) on delete cascade
);

-- 댓글
CREATE TABLE IF NOT EXISTS tbl_comment
(
    -- column level constraints
    id         UUID         not null primary key,
    article_id UUID         not null,
    user_id    UUID         not null,
    content    varchar(500) not null,
    created_at timestamptz  not null,
    updated_at timestamptz,
    is_deleted BOOLEAN      not null default false,
    like_count BIGINT       not null default 0,
    -- table level constraints
    CONSTRAINT fk_article_id FOREIGN KEY (article_id) REFERENCES tbl_article (id) on delete cascade,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES tbl_user (id) on delete cascade
);

-- 댓글 좋아요
CREATE TABLE IF NOT EXISTS tbl_like
(
    -- column level constraints
    id         UUID        not null primary key,
    comment_id UUID        not null,
    user_id    UUID        not null,
    created_at timestamptz not null,
    -- table level constraints
    CONSTRAINT fk_comment_id FOREIGN KEY (comment_id) REFERENCES tbl_comment (id) on delete cascade,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES tbl_user (id) on delete cascade
);

-- 알림
CREATE TABLE tbl_notification
(
    id            UUID        NOT NULL PRIMARY KEY,
    resource_type VARCHAR(8)  NOT NULL,
    content       TEXT        NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed     BOOLEAN     NOT NULL DEFAULT FALSE,

    user_id       UUID        NOT NULL,
    comment_id    UUID,
    interest_id   UUID,

    FOREIGN KEY (user_id) REFERENCES tbl_user (id) on delete cascade,
    FOREIGN KEY (comment_id) REFERENCES tbl_comment (id) on delete cascade,
    FOREIGN KEY (interest_id) REFERENCES tbl_interest (id) on delete cascade,

    CONSTRAINT chk_resource_type CHECK (resource_type IN ('INTEREST', 'COMMENT'))
);
-- 키워드
CREATE TABLE IF NOT EXISTS tbl_keyword
(
    id          UUID PRIMARY KEY,
    interest_id UUID        NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL,
    name        VARCHAR(20) NOT NULL,

    CONSTRAINT fk_interest_id FOREIGN KEY (interest_id) REFERENCES tbl_interest (id) ON DELETE CASCADE,
    CONSTRAINT uq_interest_keyword UNIQUE (interest_id, name)
);


-- 사용자관심사
CREATE TABLE IF NOT EXISTS tbl_user_interest
(
    id          UUID PRIMARY KEY,
    user_id     UUID        NOT NULL,
    interest_id UUID        NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_interest FOREIGN KEY (interest_id) REFERENCES tbl_interest (id) ON DELETE CASCADE,
    CONSTRAINT uq_user_interest UNIQUE (user_id, interest_id)
);

