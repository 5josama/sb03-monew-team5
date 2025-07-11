package com.sprint5team.monew.service.article;

import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.entity.ArticleCount;
import com.sprint5team.monew.domain.article.mapper.ArticleViewMapper;
import com.sprint5team.monew.domain.article.repository.ArticleCountRepository;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.article.service.ArticleServiceImpl;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArticleService 단위 테스트")
public class ArticleServiceTest {

    @Mock UserRepository userRepository;
    @Mock ArticleRepository articleRepository;
    @Mock ArticleCountRepository articleCountRepository;
    @Mock ArticleViewMapper articleViewMapper;

    @InjectMocks private ArticleServiceImpl articleService;

    private User user;
    private Article article;

    @BeforeEach
    void setUp() {
        user = new User("test@naver.com", "test", "0000");
        article = new Article();
    }

    @Test
    void 뉴스기사_VIEW를_중복_없이_저장할_수_있다() {
        // given
        UUID articleId = article.getId();
        UUID userId = user.getId();
        ArticleViewDto articleViewDto = new ArticleViewDto(
                UUID.randomUUID(),
                userId,
                Instant.now(),
                articleId,
                "NAVER",
                "https://naver.com/news/12333",
                "test",
                Instant.now(),
                "요약",
                10,
                10
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        given(articleCountRepository.findByUserIdAndArticleId(userId, articleId)).willReturn(Optional.empty());
        given(articleViewMapper.toDto(any(), any(), any())).willReturn(articleViewDto);

        // when
        ArticleViewDto result = articleService.saveArticleView(articleId, userId);

        // then
        then(articleCountRepository).should().save(any(ArticleCount.class));
        assertThat(result.articleId()).isEqualTo(articleId);
    }
}
