package com.sprint5team.monew.domain.article.mapper;

import com.sprint5team.monew.domain.article.dto.ArticleDto;
import com.sprint5team.monew.domain.article.entity.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    @Mapping(target = "publishDate", source = "article.createdAt")
    ArticleDto toDto(Article article, long commentCount, long viewCount, boolean viewedByMe);
}
