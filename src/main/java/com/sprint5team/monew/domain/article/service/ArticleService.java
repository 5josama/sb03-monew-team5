package com.sprint5team.monew.domain.article.service;

import com.sprint5team.monew.domain.article.dto.ArticleViewDto;

import java.util.UUID;

public interface ArticleService {
    ArticleViewDto saveArticleView(UUID articleId, UUID userId);
}
