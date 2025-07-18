package com.sprint5team.monew.domain.comment.repository;

import com.sprint5team.monew.domain.comment.entity.Comment;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CommentRepositoryCustom {

    long countTotalElements(UUID articleId);
    List<Comment> findCommentsWithCursor(UUID articleId, String cursor, Instant after, Pageable pageable);
}
