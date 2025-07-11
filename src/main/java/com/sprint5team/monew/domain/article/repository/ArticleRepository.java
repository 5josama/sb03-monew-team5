package com.sprint5team.monew.domain.article.repository;

import com.sprint5team.monew.domain.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {
}
