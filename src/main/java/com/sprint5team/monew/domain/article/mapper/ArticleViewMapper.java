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
    @Mapping(source = "article.title", target = "articleTitle")
    @Mapping(source = "article.summary", target = "articleSummary")
    @Mapping(source = "article.source", target = "source")
    @Mapping(source = "article.sourceUrl", target = "sourceUrl")
    @Mapping(source = "article.originalDateTime", target = "articlePublishDate")
    @Mapping(source = "articleCount.createdAt", target = "createdAt")
    @Mapping(source = "articleCount.id", target = "id")
    @Mapping(source = "viewedCount", target = "articleViewCount")
    @Mapping(source = "commentCount", target = "articleCommentCount")
    ArticleViewDto toDto(Article article, User user, ArticleCount articleCount, Long viewedCount, Long commentCount);
}
