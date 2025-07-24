package com.sprint5team.monew.domain.article.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * @param id articleCount PK
 * @param viewedBy 기사를 조회한 User ID
 * @param createdAt 기사를 조회한 날짜
 * @param articleId 기사 ID
 * @param source 출처
 * @param sourceUrl 원본 기사 URL
 * @param articleTitle 제목
 * @param articlePublishedDate 기사 날짜
 * @param articleSummary 요약
 * @param articleCommentCount 댓글 수
 * @param articleViewCount 조회 수
 */
public record ArticleViewDto(
        UUID id,
        UUID viewedBy,
        Instant createdAt,
        UUID articleId,
        String source,
        String sourceUrl,
        String articleTitle,
        Instant articlePublishedDate,
        String articleSummary,
        long articleCommentCount,
        long articleViewCount
) {
}
