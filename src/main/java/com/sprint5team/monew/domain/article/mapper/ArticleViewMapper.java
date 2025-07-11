package com.sprint5team.monew.domain.article.mapper;

import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.entity.ArticleCount;
import com.sprint5team.monew.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArticleViewMapper {

    @Mapping(source = "user.id", target = "viewedBy")
    @Mapping(source = "article.id", target = "articleId")
    @Mapping(source = "article.title", target = "title")
    @Mapping(source = "article.summary", target = "summary")
    @Mapping(source = "article.source", target = "source")
    @Mapping(source = "article.sourceUrl", target = "sourceUrl")
    @Mapping(source = "article.originalDateTime", target = "publishDate")
    @Mapping(source = "articleCount.createdAt", target = "createdAt")
    @Mapping(source = "articleCount.id", target = "id")
    // commentCount, viewCount는 수동 계산 필요
    ArticleViewDto toDto(Article article, User user, ArticleCount articleCount);
}
