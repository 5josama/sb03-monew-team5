package com.sprint5team.monew.repository.article;

import com.sprint5team.monew.base.config.QuerydslConfig;
import com.sprint5team.monew.domain.article.dto.CursorPageFilter;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@DisplayName("ArticleRepository 단위 테스트")
@Import(QuerydslConfig.class)
public class ArticleRepositoryTest {

    @Autowired private ArticleRepository articleRepository;

    @Autowired private TestEntityManager em;

    private Interest interest;

    private List<Keyword> keywords = new ArrayList<>();

    @BeforeEach
    void setUp() {
        interest = new Interest(Instant.now(), "사회", 0, new ArrayList<>(), new ArrayList<>());
        em.persist(interest);
        em.flush();

        Keyword keyword1 = new Keyword(Instant.now(), "AI", interest);
        Keyword keyword2 = new Keyword(Instant.now(), "경제", interest);
        keywords.add(keyword1);
        keywords.add(keyword2);

        em.persist(keyword1);
        em.persist(keyword2);
        em.flush();
    }

    @Test
    void 뉴스_기사를_저장할_수_있다() {
        // given
        Article article = new Article("NAVER", "https://naver.com/news/12333", "test title", "test summary", false, Instant.now(), Instant.now());

        // when
        Article saved = articleRepository.save(article);

        // then
        assertThat(saved.getId()).isEqualTo(article.getId());
    }

    @Test
    void 커서_기반으로_이후_뉴스기사를_조회할_수_있다() {
        // given
        Instant article1Time = Instant.parse("2025-07-12T20:00:00Z");
        Instant article2Time = Instant.parse("2025-07-12T21:00:00Z");
        Instant article3Time = Instant.parse("2025-07-12T22:00:00Z");

        Article article1 = new Article("NAVER", "https://...1", "AI", "경제", false, article1Time, article1Time);
        Article article2 = new Article("NAVER", "https://...2", "AI2", "경제2", false, article2Time, article2Time);
        Article article3 = new Article("NAVER", "https://...3", "AI3", "경제3", false, article3Time, article3Time);
        em.persist(article1);
        em.persist(article2);
        em.persist(article3);
        em.flush();

        CursorPageFilter filter = new CursorPageFilter(
                "AI",
                interest.getId(),
                Arrays.asList("NAVER"),
                LocalDateTime.parse("2025-07-12T00:00:00"),
                LocalDateTime.parse("2025-07-13T00:00:00"),
                "publishDate",
                "ASC",
                null,
                article2Time,
                10
        );

        List<String> keywordList = keywords.stream().map(Keyword::getName).toList();

        // when
        List<Article> result = articleRepository.findByCursorFilter(filter, keywordList);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("AI3");
    }

    @Test
    void 뉴스기사_출처를_중복없이_조회할_수_있다() {
        // given
        Article article1 = new Article("NAVER", "https://...1", "AI", "경제", false, Instant.now(), Instant.now());
        Article article2 = new Article("한국경제", "https://...2", "AI2", "경제2", false, Instant.now(), Instant.now());
        Article article3 = new Article("연합뉴스", "https://...3", "AI3", "경제3", false, Instant.now(), Instant.now());
        Article article4 = new Article("연합뉴스", "https://...4", "AI4", "경제4", false, Instant.now(), Instant.now());

        em.persist(article1);
        em.persist(article2);
        em.persist(article3);
        em.persist(article4);
        em.flush();

        // when
        List<String> result = articleRepository.findDistinctSources();

        // then
        assertThat(result).hasSize(3);
    }
}
