package com.sprint5team.monew.domain.article.repository;

import com.sprint5team.monew.domain.article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID>, ArticleCustomRepository {
    boolean existsBySourceUrl(String link);

    Page<Article> findAllByOrderByIdAsc(Pageable pageable);

    @Query("SELECT DISTINCT a.source FROM Article a")
    List<String> findDistinctSources();

    List<Article> findAllBySourceUrlIn(List<String> sourceUrls);

    @Query("""
    SELECT COUNT(ak)
    FROM ArticleKeyword ak
    JOIN ak.article a
    WHERE ak.interest.id = :interestId
      AND a.createdAt >= :since
    """)
    long countRecentArticlesByInterestId(@Param("interestId") UUID interestId, @Param("since") Instant since);

    Page<Article> findByCreatedAtAfterOrderByCreatedAtAsc(Instant createdAt, Pageable pageable);
}
