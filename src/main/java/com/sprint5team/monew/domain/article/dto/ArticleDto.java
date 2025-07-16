package com.sprint5team.monew.domain.article.dto;

import java.time.Instant;
import java.util.UUID;

public record ArticleDto(
        UUID id,
        String source,
        String sourceUrl,
        String title,
        String summary,
        Instant publishDate,
        long commentCount,
        long viewCount,
        boolean viewedByMe
) {
}
