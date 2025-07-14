package com.sprint5team.monew.domain.article.service;

import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.dto.CursorPageFilter;
import com.sprint5team.monew.domain.article.dto.CursorPageResponseArticleDto;

import java.util.UUID;

public interface ArticleService {
    ArticleViewDto saveArticleView(UUID articleId, UUID userId);

    CursorPageResponseArticleDto getArticles(CursorPageFilter filter);
}
