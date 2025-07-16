package com.sprint5team.monew.domain.article.dto;

import java.util.UUID;

public interface ArticleCommentCount {
    UUID getArticleId();
    Long getCount();
}
