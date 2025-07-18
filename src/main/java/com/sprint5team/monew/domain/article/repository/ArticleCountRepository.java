package com.sprint5team.monew.domain.article.repository;

import com.sprint5team.monew.domain.article.entity.ArticleCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArticleCountRepository extends JpaRepository<ArticleCount, UUID>, ArticleCountCustomRepository {
    Optional<ArticleCount> findByUserIdAndArticleId(UUID userId, UUID articleId);

    List<ArticleCount> findAllByUserIdAndArticleId(UUID id, UUID id1);

    List<ArticleCount> findByArticleId(UUID articleId);

  List<ArticleCount> findTop10ByUserIdOrderByCreatedAtDesc(UUID userId);
}
