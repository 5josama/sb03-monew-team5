package com.sprint5team.monew.domain.article.repository;

import com.sprint5team.monew.domain.article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {
    boolean existsBySourceUrl(String link);

    Page<Article> findByOriginalDateTimeBetween(Instant start, Instant end, Pageable pageable);
}
