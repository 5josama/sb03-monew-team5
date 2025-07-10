package com.sprint5team.monew.domain.comment.dto.request;

import java.util.UUID;

public record CommentRegisterRequest(
   UUID articleId,
   UUID userId,
   String content
) {}
