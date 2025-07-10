package com.sprint5team.monew.domain.article.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @param id articleCount PK
 * @param viewedBy 기사를 조회한 User ID
 * @param createdAt 기사를 조회한 날짜
 * @param articleId 기사 ID
 * @param source 출처
 * @param sourceUrl 원본 기사 URL
 * @param title 제목
 * @param publishDate 기사 날짜
 * @param summary 요약
 * @param commentCount 댓글 수
 * @param viewCount 조회 수
 */
public record ArticleViewDto(
        UUID id,
        UUID viewedBy,
        LocalDateTime createdAt,
        UUID articleId,
        String source,
        String sourceUrl,
        String title,
        LocalDateTime publishDate,
        String summary,
        long commentCount,
        long viewCount
) {
}
