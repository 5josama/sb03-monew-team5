package com.sprint5team.monew.domain.article.repository;

import com.sprint5team.monew.domain.article.dto.CursorPageFilter;
import com.sprint5team.monew.domain.article.entity.Article;

import java.util.List;

public interface ArticleCustomRepository {

    List<Article> findByCursorFilter(CursorPageFilter filter, List<String> interestKeyword);

    long countByCursorFilter(CursorPageFilter filter, List<String> interestKeyword);
}
