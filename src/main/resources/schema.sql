-- 테이블 생성
DROP TABLE IF EXISTS tbl_user;
DROP TABLE IF EXISTS tbl_comment CASCADE;
DROP TABLE IF EXISTS tbl_like CASCADE;
DROP TABLE IF EXISTS tbl_notification CASCADE;
drop table if exists tbl_article;
drop table if exists tbl_article_count;
drop table if exists tbl_article_keyword;
-- User
-- 댓글, 좋아요, 알림, 관심사, 뉴스 조회수
CREATE TABLE tbl_user
(
    id uuid PRIMARY KEY,
    email   varchar(100)    UNIQUE  NOT NULL,
    nickname    varchar(20) NOT NULL,
    password    varchar(60) NOT NULL,
    created_at  TIMESTAMPTZ    NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    updated_at  TIMESTAMPTZ    NOT NULL
);

-- 뉴스
CREATE TABLE tbl_article (
    id UUID PRIMARY KEY,
    source VARCHAR(5) NOT NULL,
    source_url VARCHAR(500) UNIQUE NOT NULL,
    title VARCHAR(100) NOT NULL,
    summary TEXT NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    original_created_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);
-- 뉴스 조회수
CREATE TABLE tbl_article_count (
    id UUID PRIMARY KEY,
    article_id UUID NOT NULL,
    user_id UUID NOT NULL,
    FOREIGN KEY (article_id) REFERENCES tbl_article(id) on delete cascade,
    FOREIGN KEY (user_id) REFERENCES tbl_user(id) on delete cascade
);

-- 관심사
CREATE TABLE IF NOT EXISTS tbl_interest (
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMPTZ NOT NULL,
    updated_at   TIMESTAMPTZ,
    name         VARCHAR(50) NOT NULL
);
-- 뉴스 관심사
CREATE TABLE tbl_article_keyword (
    id UUID PRIMARY KEY,
    article_id UUID NOT NULL,
    interest_id UUID NOT NULL,
    FOREIGN KEY (article_id) REFERENCES tbl_article(id) on delete cascade,
    FOREIGN KEY (interest_id) REFERENCES tbl_interest(id) on delete cascade
);
-- 댓글
CREATE TABLE IF NOT EXISTS tbl_comment
(
    -- column level constraints
    id UUID not null primary key ,
    article_id UUID not null ,
    user_id UUID not null ,
    content varchar(500) not null,
    created_at timestamptz not null,
    updated_at timestamptz,
    is_deleted BOOLEAN not null default false,
    like_count BIGINT not null default 0,
    -- table level constraints
    CONSTRAINT fk_article_id FOREIGN KEY (article_id) REFERENCES tbl_article (id) on delete cascade,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES tbl_user(id) on delete cascade
);
-- 댓글 좋아요
CREATE TABLE IF NOT EXISTS tbl_like
(
    -- column level constraints
    id UUID not null primary key ,
    comment_id UUID not null,
    user_id UUID not null,
    created_at timestamptz not null,
    -- table level constraints
    CONSTRAINT fk_comment_id FOREIGN KEY (comment_id) REFERENCES tbl_comment (id) on delete cascade,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES tbl_user(id) on delete cascade
);
-- 알림
CREATE TABLE tbl_notification
(
    id            UUID        NOT NULL PRIMARY KEY,
    resource_type VARCHAR(8)  NOT NULL,
    content       TEXT        NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed     BOOLEAN     NOT NULL DEFAULT FALSE,

    user_id       UUID        NOT NULL,
    comment_id    UUID,
    interest_id   UUID,

    FOREIGN KEY (user_id) REFERENCES tbl_user (id) on delete cascade,
    FOREIGN KEY (comment_id) REFERENCES tbl_comment (id) on delete cascade,
    FOREIGN KEY (interest_id) REFERENCES tbl_interest (id) on delete cascade,

    CONSTRAINT chk_resource_type CHECK (resource_type IN ('interest', 'comment'))
);
-- 키워드
CREATE TABLE IF NOT EXISTS tbl_keyword (
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMPTZ NOT NULL,
    subscriber_count BIGINT NOT NULL
);
-- 관심사별 키워드
CREATE TABLE IF NOT EXISTS tbl_interest_keyword (
    interest_id UUID NOT NULL,
    keyword_id  UUID NOT NULL,
    CONSTRAINT fk_interest FOREIGN KEY (interest_id) REFERENCES tbl_interest(id) ON DELETE CASCADE,
    CONSTRAINT fk_keyword  FOREIGN KEY (keyword_id)  REFERENCES tbl_keyword(id) ON DELETE CASCADE,
    CONSTRAINT uk_interest_keyword UNIQUE (interest_id, keyword_id)
);