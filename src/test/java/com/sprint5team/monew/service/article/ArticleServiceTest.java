package com.sprint5team.monew.service.article;

import com.sprint5team.monew.domain.article.dto.ArticleDto;
import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.dto.CursorPageFilter;
import com.sprint5team.monew.domain.article.dto.CursorPageResponseArticleDto;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.entity.ArticleCount;
import com.sprint5team.monew.domain.article.mapper.ArticleMapper;
import com.sprint5team.monew.domain.article.mapper.ArticleViewMapper;
import com.sprint5team.monew.domain.article.repository.ArticleCountRepository;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.article.service.ArticleServiceImpl;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArticleService 단위 테스트")
public class ArticleServiceTest {

    @Mock UserRepository userRepository;
    @Mock ArticleRepository articleRepository;
    @Mock ArticleCountRepository articleCountRepository;
    @Mock ArticleViewMapper articleViewMapper;
    @Mock InterestRepository interestRepository;
    @Mock KeywordRepository keywordRepository;
    @Mock ArticleMapper articleMapper;

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

    @Test
    void 뉴스기사_목록을_커서_페이지네이션을_통해_조회할_수_있다() {
        // given
        Interest interest = new Interest(Instant.now(), "사회", 0);
        List<Keyword> keywords = List.of(
            new Keyword(Instant.now(), "AI", interest),
            new Keyword(Instant.now(), "경제", interest)
        );

        CursorPageFilter filter = new CursorPageFilter(
                "AI",
                interest.getId(),
                Arrays.asList("AI", "경제"),
                Instant.now(),
                Instant.now(),
                "publishDate",
                "ASC",
                null,
                null,
                2
        );

        List<Article> articles = List.of(
                new Article("NAVER", "https://naver.com/news/12333", "AI투자", "경제", Instant.now()),
                new Article("NAVER", "https://naver.com/news/12333", "AI로봇", "AI 기술", Instant.now()),
                new Article("NAVER", "https://naver.com/news/12333", "test", "test", Instant.now())
        );
        ReflectionTestUtils.setField(articles.get(0), "id", UUID.randomUUID());
        ReflectionTestUtils.setField(articles.get(1), "id", UUID.randomUUID());
        ReflectionTestUtils.setField(articles.get(2), "id", UUID.randomUUID());

        given(articleRepository.findByCursorFilter(any(CursorPageFilter.class), any())).willReturn(articles);
        given(interestRepository.findById(interest.getId())).willReturn(Optional.of(interest));
        given(keywordRepository.findAllByInterestIn(any())).willReturn(keywords);

        Map<UUID, Long> viewCountMap = articles.stream()
                .collect(Collectors.toMap(Article::getId, a -> 5L)); // 전부 5로 가정
        Set<UUID> viewedByMeSet = Set.of(articles.get(0).getId());

        given(articleCountRepository.countViewByArticleIds(any())).willReturn(viewCountMap);
        given(articleCountRepository.findViewedArticleIdsByUserId(any(), any())).willReturn(viewedByMeSet);

        given(articleMapper.toDto(any(), anyLong(), anyLong(), anyBoolean()))
                .willAnswer(invocation -> {
                    Article a = invocation.getArgument(0);
                    long commentCount = invocation.getArgument(1);
                    long viewCount = invocation.getArgument(2);
                    boolean viewedByMe = invocation.getArgument(3);
                    return new ArticleDto(a.getId(), a.getSource(), a.getSourceUrl(), a.getTitle(), a.getSummary(), a.getOriginalDateTime(), commentCount, viewCount, viewedByMe);
                }
        );

        // when
        CursorPageResponseArticleDto response = articleService.getArticles(filter, user.getId());

        // then
        assertThat(response.content()).hasSize(2);
        assertThat(response.hasNext()).isTrue();
        assertThat(response.content().get(0).viewCount()).isEqualTo(5L);
        assertThat(response.content().get(0).viewedByMe()).isEqualTo(true);
    }

    @Test
    void 뉴스기사_출처_목록을_조회할_수_있다() {
        // given
        List<String> sources = List.of(
                "NAVER",
                "한국경제",
                "연합뉴스"
        );

        given(articleRepository.findDistinctSource()).willReturn(sources);

        // when
        List<String> result = articleService.getSources();

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0)).isEqualTo("NAVER");
    }
}
