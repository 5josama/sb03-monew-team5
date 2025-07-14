-- 테이블 삭제
DROP TABLE IF EXISTS tbl_like CASCADE;
DROP TABLE IF EXISTS tbl_comment CASCADE;
DROP TABLE IF EXISTS tbl_notification CASCADE;
DROP TABLE IF EXISTS tbl_article_keyword CASCADE;
DROP TABLE IF EXISTS tbl_article_count CASCADE;
DROP TABLE IF EXISTS tbl_article CASCADE;
DROP TABLE IF EXISTS tbl_interest_keyword CASCADE;
DROP TABLE IF EXISTS tbl_interest CASCADE;
DROP TABLE IF EXISTS tbl_keyword CASCADE;
DROP TABLE IF EXISTS tbl_user CASCADE;

-- 사용자
CREATE TABLE IF NOT EXISTS tbl_user
(
    id         UUID PRIMARY KEY,
    email      VARCHAR(100) UNIQUE   NOT NULL,
    nickname   VARCHAR(20)           NOT NULL,
    password   VARCHAR(60)           NOT NULL,
    created_at TIMESTAMP             NOT NULL,
    updated_at TIMESTAMP             NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL
);

-- 뉴스
CREATE TABLE IF NOT EXISTS tbl_article
(
    id                  UUID PRIMARY KEY,
    source              VARCHAR(5)            NOT NULL,
    source_url          VARCHAR(500) UNIQUE   NOT NULL,
    title               VARCHAR(100)          NOT NULL,
    summary             TEXT                  NOT NULL,
    original_created_at TIMESTAMP             NOT NULL,
    created_at          TIMESTAMP             NOT NULL,
    is_deleted          BOOLEAN DEFAULT FALSE NOT NULL
);


-- 뉴스 조회수
CREATE TABLE IF NOT EXISTS tbl_article_count
(
    id         UUID PRIMARY KEY,
    article_id UUID NOT NULL,
    user_id    UUID NOT NULL,
    FOREIGN KEY (article_id) REFERENCES tbl_article (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES tbl_user (id) ON DELETE CASCADE
);


-- 관심사
CREATE TABLE IF NOT EXISTS tbl_interest
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(50) NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    updated_at TIMESTAMP
);

-- 뉴스-관심사 연결
CREATE TABLE IF NOT EXISTS tbl_article_keyword
(
    id          UUID PRIMARY KEY,
    article_id  UUID NOT NULL,
    interest_id UUID NOT NULL,
    FOREIGN KEY (article_id) REFERENCES tbl_article (id) ON DELETE CASCADE,
    FOREIGN KEY (interest_id) REFERENCES tbl_interest (id) ON DELETE CASCADE
);

-- 댓글
CREATE TABLE IF NOT EXISTS tbl_comment
(
    id         UUID PRIMARY KEY,
    article_id UUID         NOT NULL,
    user_id    UUID         NOT NULL,
    content    VARCHAR(500) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN               DEFAULT FALSE NOT NULL,
    like_count BIGINT       NOT NULL DEFAULT 0,
    FOREIGN KEY (article_id) REFERENCES tbl_article (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES tbl_user (id) ON DELETE CASCADE
);

-- 댓글 좋아요
CREATE TABLE IF NOT EXISTS tbl_like
(
    id         UUID PRIMARY KEY,
    comment_id UUID      NOT NULL,
    user_id    UUID      NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (comment_id) REFERENCES tbl_comment (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES tbl_user (id) ON DELETE CASCADE
);

-- 알림
CREATE TABLE IF NOT EXISTS tbl_notification (
    id UUID PRIMARY KEY,
    resource_type VARCHAR(8) NOT NULL CHECK (resource_type IN ('interest', 'comment')),
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    user_id UUID NOT NULL,
    comment_id UUID,
    interest_id UUID,
    FOREIGN KEY (user_id) REFERENCES tbl_user(id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES tbl_comment(id) ON DELETE CASCADE,
    FOREIGN KEY (interest_id) REFERENCES tbl_interest(id) ON DELETE CASCADE
);

-- 키워드
CREATE TABLE IF NOT EXISTS tbl_keyword
(
    id               UUID PRIMARY KEY,
    created_at       TIMESTAMP NOT NULL,
    subscriber_count BIGINT    NOT NULL
);


-- 관심사-키워드 연결
CREATE TABLE IF NOT EXISTS tbl_interest_keyword
(
    interest_id UUID NOT NULL,
    keyword_id  UUID NOT NULL,
    FOREIGN KEY (interest_id) REFERENCES tbl_interest (id) ON DELETE CASCADE,
    FOREIGN KEY (keyword_id) REFERENCES tbl_keyword (id) ON DELETE CASCADE,
    UNIQUE (interest_id, keyword_id)
);

