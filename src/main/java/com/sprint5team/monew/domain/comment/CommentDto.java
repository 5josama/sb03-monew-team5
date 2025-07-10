package com.sprint5team.monew.domain.comment;

import java.time.Instant;
import java.util.UUID;

public record CommentDto(
        UUID id,
        UUID articleId,
        UUID userId,
        String userNickname,
        String content,
        Long likeCount,
        Boolean likedByMe,
        Instant createdAt
) {}
