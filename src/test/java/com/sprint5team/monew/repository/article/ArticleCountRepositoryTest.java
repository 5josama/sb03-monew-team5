package com.sprint5team.monew.repository.article;

import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.entity.ArticleCount;
import com.sprint5team.monew.domain.article.repository.ArticleCountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("ArticleCountRepository 단위 테스트")
public class ArticleCountRepositoryTest {

    @Autowired private ArticleCountRepository articleCountRepository;

    @Test
    void 뉴스_기사_뷰를_등록할_수_있다() {
        // given
        Article article = new Article();
        User user = new User();

        ArticleCount articleCount = new ArticleCount(article, user);

        // when
        ArticleCount saved = articleCountRepository.save(articleCount);

        // then
        assertThat(saved.getArticle().getId()).isEqualTo(article.getId());
    }
}
