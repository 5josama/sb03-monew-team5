package com.sprint5team.monew.domain.comment.service;


import com.sprint5team.monew.domain.comment.dto.*;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.UUID;

public interface CommentService {

    CommentDto create(UUID userId,CommentRegisterRequest request);

    CursorPageResponseCommentDto find(UUID articleId, UUID userId, String cursor, Instant after, Pageable pageable);

    void softDelete(UUID commentId);

    void hardDelete(UUID commentId);

    CommentDto update(UUID commentId, UUID userId, CommentUpdateRequest request);

    CommentLikeDto like(UUID commentId, UUID userId);

    void cancelLike(UUID commentId, UUID userId);
}
