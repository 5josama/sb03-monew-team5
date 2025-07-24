package com.sprint5team.monew.repository.article;

import com.sprint5team.monew.base.config.QuerydslConfig;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.entity.ArticleCount;
import com.sprint5team.monew.domain.article.repository.ArticleCountRepository;
import com.sprint5team.monew.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@DisplayName("ArticleCountRepository 단위 테스트")
@Import(QuerydslConfig.class)
public class ArticleCountRepositoryTest {

    @Autowired private ArticleCountRepository articleCountRepository;

    @Autowired private TestEntityManager em;

    private Article article;

    private User user;

    @BeforeEach
    void setUp() {
        article = new Article("NAVER", "https://test.com", "AI", "요약", false, Instant.now(), Instant.now(), new ArrayList<>());

        em.persist(article);

        user = new User("test@naver.com", "test", "0000", Instant.now(), false);
        em.persist(user);
    }

    @Test
    void 뉴스_기사_뷰를_등록할_수_있다() {
        // given
        ArticleCount articleCount = new ArticleCount(article, user);

        // when
        ArticleCount saved = articleCountRepository.save(articleCount);

        // then
        assertThat(saved.getArticle().getId()).isEqualTo(article.getId());
    }

    @Test
    void 사용자와_기사목록으로_열람한_기사_ID를_조회할_수_있다() {
        // given
        UUID userId = user.getId();

        Article a1 = new Article("NAVER", "https://a1.com", "AI", "요약", false, Instant.now(), Instant.now(), new ArrayList<>());
        Article a2 = new Article("NAVER", "https://a2.com", "AI", "요약", false, Instant.now(), Instant.now(), new ArrayList<>());
        Article a3 = new Article("NAVER", "https://a3.com", "AI", "요약", false, Instant.now(), Instant.now(), new ArrayList<>());
        em.persist(a1);
        em.persist(a2);
        em.persist(a3);

        ArticleCount c1 = new ArticleCount(a1, user);
        ReflectionTestUtils.setField(c1, "createdAt", Instant.now());
        ArticleCount c2 = new ArticleCount(a3, user);
        ReflectionTestUtils.setField(c2, "createdAt", Instant.now());
        em.persist(c1);
        em.persist(c2);
        em.flush();

        List<UUID> articleIds = List.of(a1.getId(), a2.getId(), a3.getId());

        // when
        Set<UUID> result = articleCountRepository.findViewedArticleIdsByUserId(userId, articleIds);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(a1.getId(), a3.getId());
    }
}
