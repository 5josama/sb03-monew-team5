package com.sprint5team.monew.domain.article.mapper;

import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.entity.ArticleCount;
import com.sprint5team.monew.domain.user.entity.User;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-12T00:08:26+0900",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.2.jar, environment: Java 17.0.15 (Eclipse Adoptium)"
)
@Component
public class ArticleViewMapperImpl implements ArticleViewMapper {

    @Override
    public ArticleViewDto toDto(Article article, User user, ArticleCount articleCount) {
        if ( article == null && user == null && articleCount == null ) {
            return null;
        }

        UUID articleId = null;
        String title = null;
        String summary = null;
        String source = null;
        String sourceUrl = null;
        Instant publishDate = null;
        if ( article != null ) {
            articleId = article.getId();
            title = article.getTitle();
            summary = article.getSummary();
            source = article.getSource();
            sourceUrl = article.getSourceUrl();
            publishDate = article.getOriginalDateTime();
        }
        UUID viewedBy = null;
        if ( user != null ) {
            viewedBy = user.getId();
        }
        Instant createdAt = null;
        UUID id = null;
        if ( articleCount != null ) {
            createdAt = articleCount.getCreatedAt();
            id = articleCount.getId();
        }

        long commentCount = 0L;
        long viewCount = 0L;

        ArticleViewDto articleViewDto = new ArticleViewDto( id, viewedBy, createdAt, articleId, source, sourceUrl, title, publishDate, summary, commentCount, viewCount );

        return articleViewDto;
    }
}
