package com.sprint5team.monew.domain.comment.repository;

import com.sprint5team.monew.domain.comment.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {

    List<Like> findAllByUserIdAndCommentId(UUID userId, UUID CommentId);

    // 사용자 활동 내역 조회 시 사용
    List<Like> findTop10ByUserIdOrderByCreatedAtDesc(UUID userId);

    // 사용자 물리삭제 시 사용
    void deleteAllByUserId(UUID userId);
}
