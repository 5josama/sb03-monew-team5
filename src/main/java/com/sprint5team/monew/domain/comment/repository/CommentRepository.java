package com.sprint5team.monew.domain.comment.repository;

import com.sprint5team.monew.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

}
