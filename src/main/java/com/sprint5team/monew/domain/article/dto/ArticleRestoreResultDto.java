package com.sprint5team.monew.domain.article.dto;

import java.time.Instant;
import java.util.List;

public record ArticleRestoreResultDto (
        Instant restoreDate,
        List<String> restoredArticleIds,
        long restoredArticleCount
) {
}
