package com.sprint5team.monew.domain.comment;

import java.util.UUID;

public record CommentRegisterRequest(
   UUID articleId,
   UUID userId,
   String content
) {}
