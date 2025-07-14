package com.sprint5team.monew.domain.article.mapper;

import com.sprint5team.monew.domain.article.dto.ArticleDto;
import com.sprint5team.monew.domain.article.entity.Article;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    ArticleDto toDto(Article article, long commentCount, long viewCount, boolean viewedByMe);
}
