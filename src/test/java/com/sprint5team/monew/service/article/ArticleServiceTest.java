package com.sprint5team.monew.service.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sprint5team.monew.base.util.S3Storage;
import com.sprint5team.monew.domain.article.dto.*;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

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
    @Mock S3Storage s3Storage;

    @InjectMocks private ArticleServiceImpl articleService;

    private User user;
    private Article article;

    @BeforeEach
    void setUp() {
        user = new User("test@naver.com", "test", "0000");
        article = new Article();

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        ReflectionTestUtils.setField(articleService, "objectMapper", mapper);
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
        given(articleViewMapper.toDto(any(), any(), any(), any(), any())).willReturn(articleViewDto);

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
                Arrays.asList("NAVER", "한국경제"),
                Instant.now(),
                Instant.now(),
                "publishDate",
                "ASC",
                null,
                null,
                5
        );

        List<Article> articles = List.of(
                new Article("NAVER", "https://naver.com/news/12333", "AI투자", "경제", false, Instant.now(), Instant.now()),
                new Article("NAVER", "https://naver.com/news/12333", "AI로봇", "AI 기술", false, Instant.now(), Instant.now()),
                new Article("NAVER", "https://naver.com/news/12333", "test", "test", false, Instant.now(), Instant.now())
        );
        ReflectionTestUtils.setField(articles.get(0), "id", UUID.randomUUID());
        ReflectionTestUtils.setField(articles.get(1), "id", UUID.randomUUID());
        ReflectionTestUtils.setField(articles.get(2), "id", UUID.randomUUID());

        List<Article> filteredArticles = articles.subList(0, 2); // keyword 포함된 두 개만
        given(articleRepository.findByCursorFilter(any(CursorPageFilter.class), any()))
                .willReturn(filteredArticles);
        lenient().when(interestRepository.findById(interest.getId())).thenReturn(Optional.of(interest));
        lenient().when(keywordRepository.findAllByInterestIn(any())).thenReturn(keywords);

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
        assertThat(response.hasNext()).isFalse();
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

        given(articleRepository.findDistinctSources()).willReturn(sources);

        // when
        List<String> result = articleService.getSources();

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0)).isEqualTo("NAVER");
    }

    @Test
    void 주어진_날짜_범위의_유실된_뉴스를_복구할_수_있다() {
        // given
        Instant from = Instant.parse("2025-07-13T00:00:00Z");
        Instant to = Instant.parse("2025-07-13T23:59:59Z");

        List<Article> backupArticles = List.of(
                new Article("NAVER", "https://...1", "AI", "경제", false, Instant.now(), Instant.now()),
                new Article("한국경제", "https://...2", "AI2", "경제2", false, Instant.now(), Instant.now())
        );
        UUID id1 = UUID.fromString("3b98b117-369e-4c36-a44d-7eef0a341d67");
        UUID id2 = UUID.fromString("ba4b516e-3ab3-44ae-aa9d-713d29911e50");

        ReflectionTestUtils.setField(backupArticles.get(0), "id", id1);
        ReflectionTestUtils.setField(backupArticles.get(1), "id", id2);
        List<String> backupJsons = List.of(
                "[{" +
                        "\"id\": \"3b98b117-369e-4c36-a44d-7eef0a341d67\"," +
                        "\"source\": \"NAVER\"," +
                        "\"sourceUrl\": \"https://naver.com/1\"," +
                        "\"title\": \"AI\"," +
                        "\"summary\": \"경제\"," +
                        "\"originalDateTime\": \"2025-07-13T10:00:00Z\"," +
                        "\"createdAt\": \"2025-07-13T10:00:00Z\"" +
                        "}]",
                "[{" +
                        "\"id\": \"ba4b516e-3ab3-44ae-aa9d-713d29911e50\"," +
                        "\"source\": \"한국경제\"," +
                        "\"sourceUrl\": \"https://hankyung.com/2\"," +
                        "\"title\": \"AI2\"," +
                        "\"summary\": \"경제2\"," +
                        "\"originalDateTime\": \"2025-07-13T11:00:00Z\"," +
                        "\"createdAt\": \"2025-07-13T11:00:00Z\"" +
                        "}]"
        );
        List<String> restoredIds = List.of(
                "3b98b117-369e-4c36-a44d-7eef0a341d67",
                "ba4b516e-3ab3-44ae-aa9d-713d29911e50"
        );

        given(s3Storage.readArticlesFromBackup(from, to)).willReturn(backupJsons);
        given(articleRepository.saveAll(anyList())).willReturn(backupArticles);

        // when
        ArticleRestoreResultDto result = articleService.restoreArticle(from, to);

        // then
        assertThat(result.restoredArticleIds().size()).isEqualTo(2);
        assertThat(result.restoredArticleIds()).containsExactlyInAnyOrderElementsOf(restoredIds);
        verify(s3Storage).readArticlesFromBackup(from, to);
        verify(articleRepository).saveAll(anyList());
    }

    @Test
    void 주어진_뉴스기사_ID로_뉴스기사를_논리삭제_할_수_있다() {
        // given
        List<Article> articles = List.of(
                new Article("NAVER", "https://...1", "AI", "경제", false, Instant.now(), Instant.now()),
                new Article("한국경제", "https://...2", "AI2", "경제2", false, Instant.now(), Instant.now())
        );
        UUID id1 = UUID.fromString("3b98b117-369e-4c36-a44d-7eef0a341d67");
        UUID id2 = UUID.fromString("ba4b516e-3ab3-44ae-aa9d-713d29911e50");
        ReflectionTestUtils.setField(articles.get(0), "id", id1);
        ReflectionTestUtils.setField(articles.get(1), "id", id2);

        given(articleRepository.findById(id2)).willReturn(Optional.of(articles.get(1)));

        // when
        articleService.softDeleteArticle(id2);

        // then
        assertThat(article2.isDeleted()).isTrue();
        verify(articleRepository).save(articles.get(1));
    }
}
