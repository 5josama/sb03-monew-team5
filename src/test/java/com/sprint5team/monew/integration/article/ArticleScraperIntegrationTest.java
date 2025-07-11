package com.sprint5team.monew.integration.article;

import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.article.service.ArticleScraper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@DisplayName("ArticleScraper 통합 테스트")
public class ArticleScraperIntegrationTest {

    @Autowired private ArticleScraper articleScraper;
    @Autowired private ArticleRepository articleRepository;

    @Test
    void API_RSS를_통하여_기사를_수집하고_저장할_수_있다() {

        // when
        articleScraper.scrapeAll();

        // then
        List<Article> articles = articleRepository.findAll();
        assertThat(articles).isNotEmpty();
    }
}
