package com.sprint5team.monew.repository.article;

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
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@DisplayName("ArticleCountRepository 단위 테스트")
public class ArticleCountRepositoryTest {

    @Autowired private ArticleCountRepository articleCountRepository;

    @Autowired private TestEntityManager em;

    private Article article;

    private User user;

    @BeforeEach
    void setUp() {
        article = new Article();
        em.persist(article);

        user = new User("test@naver.com", "test", "0000");
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
}
