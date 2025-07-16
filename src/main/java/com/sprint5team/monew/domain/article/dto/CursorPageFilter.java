package com.sprint5team.monew.domain.article.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CursorPageFilter (
        String keyword,
        UUID interestId,
        List<String> sourceIn,
        Instant publishDateFrom,
        Instant publishDateTo,
        String orderBy,
        String direction,
        String cursor,
        Instant after,
        int limit
) {
}
