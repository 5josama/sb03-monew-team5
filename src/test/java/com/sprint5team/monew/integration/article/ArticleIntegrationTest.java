package com.sprint5team.monew.integration.article;

import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.entity.ArticleCount;
import com.sprint5team.monew.domain.article.repository.ArticleCountRepository;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.article.service.ArticleService;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@DisplayName("Article 통합 테스트")
public class ArticleIntegrationTest {

    @Autowired private ArticleService articleService;
    @Autowired private ArticleRepository articleRepository;
    @Autowired private ArticleCountRepository articleCountRepository;
    @Autowired private UserRepository userRepository;

    private User setUpUser() {
        User user = new User("test@naver.com","test", "12345678");
        userRepository.save(user);
        return user;
    }

    private Article setUpArticle() {
        Article article = new Article(
                "NAVER",
                "https://naver.com/news/123331",
                "title",
                "요약",
                Instant.now()
        );
        articleRepository.save(article);
        return article;
    }

    @Test
    void 유저가_뉴스를_클릭하면_뉴스_조회수_테이블에_등록_되어야_한다() {
        // given
        User user = setUpUser();
        Article article = setUpArticle();

        // when
        articleService.saveArticleView(article.getId(), user.getId());

        // then
        Optional<ArticleCount> articleCount = articleCountRepository.findByUserIdAndArticleId(user.getId(), article.getId());
        assertThat(articleCount).isPresent();
    }

    @Test
    void 유저가_뉴스를_클릭했을_때_뉴스_조회수_테이블에_데이터가_존재하면_저장하지_않는다() {
        // given
        User user = setUpUser();
        Article article = setUpArticle();

        articleService.saveArticleView(article.getId(), user.getId());

        // when
        articleService.saveArticleView(article.getId(), user.getId());

        // then
        List<ArticleCount> articleCounts = articleCountRepository.findAllByUserIdAndArticleId(user.getId(), article.getId());
        assertThat(articleCounts).hasSize(1);
    }

}
