package com.sprint5team.monew.domain.comment.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentActivityDto(
    UUID id,
    UUID articleId,
    String articleTitle,
    UUID userId,
    String userNickname,
    String content,
    long likeCount,
    Instant createdAt
) {

}
