package com.sprint5team.monew.domain.comment.repository;

import com.sprint5team.monew.domain.article.dto.ArticleCommentCount;
import com.sprint5team.monew.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID>, CommentRepositoryCustom{

    @Query("""
        SELECT c.article.id AS articleId, COUNT(c) AS count
        FROM Comment c
        WHERE c.article.id IN :articleIds
        GROUP BY c.article.id
    """)
    List<ArticleCommentCount> countByArticleIds(@Param("articleIds") List<UUID> articleIds);

    List<Comment> findByArticleId(UUID articleId);
}
