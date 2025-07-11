package com.sprint5team.monew.repository.article;

import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@DisplayName("ArticleRepository 단위 테스트")
public class ArticleRepositoryTest {

    @Autowired private ArticleRepository articleRepository;

    @Test
    void 뉴스_기사를_저장할_수_있다() {
        // given
        Article article = new Article("NAVER", "https://naver.com/news/12333", "test title", "test summary", Instant.now());

        // when
        Article saved = articleRepository.save(article);

        // then
        assertThat(saved.getId()).isEqualTo(article.getId());
    }
}
