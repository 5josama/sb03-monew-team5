package com.sprint5team.monew.domain.comment.service;


import com.sprint5team.monew.domain.comment.dto.CommentDto;
import com.sprint5team.monew.domain.comment.dto.CommentRegisterRequest;
import com.sprint5team.monew.domain.comment.dto.CursorPageResponseCommentDto;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.UUID;

public interface CommentService {

    CommentDto create(CommentRegisterRequest request);

    CursorPageResponseCommentDto find(UUID articleId, String cursor, Instant after, Pageable pageable);

    void softDelete(UUID commentId);

}
