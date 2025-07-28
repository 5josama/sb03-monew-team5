package com.sprint5team.monew.repository.article;

import com.sprint5team.monew.base.config.QuerydslConfig;
import com.sprint5team.monew.domain.article.dto.CursorPageFilter;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
        Article article = new Article("NAVER", "https://naver.com/news/12333", "test title", "test summary", false, Instant.now(), Instant.now(), new ArrayList<>());

        // when
        Article saved = articleRepository.save(article);

        // then
        assertThat(saved.getId()).isEqualTo(article.getId());
    }

    @Test
    void 커서_기반으로_이후_뉴스기사를_조회할_수_있다() {
        // given
        articleRepository.deleteAll();
        Instant article1Time = Instant.parse("2025-07-12T20:00:00Z").truncatedTo(ChronoUnit.MILLIS);
        Instant article2Time = Instant.parse("2025-07-12T21:00:00Z").truncatedTo(ChronoUnit.MILLIS);
        Instant article3Time = article2Time.plusSeconds(10).truncatedTo(ChronoUnit.MILLIS);

        Article article1 = new Article("NAVER", "https://...1", "AI", "경제", false, article1Time.truncatedTo(ChronoUnit.MILLIS), article1Time.truncatedTo(ChronoUnit.MILLIS), new ArrayList<>());
        Article article2 = new Article("NAVER", "https://...2", "AI2", "경제2", false, article2Time.truncatedTo(ChronoUnit.MILLIS), article2Time.truncatedTo(ChronoUnit.MILLIS), new ArrayList<>());
        Article article3 = new Article("NAVER", "https://...3", "AI3", "경제3", false, article3Time.truncatedTo(ChronoUnit.MILLIS), article3Time.truncatedTo(ChronoUnit.MILLIS), new ArrayList<>());
        em.persist(article1);
        em.persist(article2);
        em.persist(article3);
        em.flush();
        ReflectionTestUtils.setField(article1, "createdAt", article1Time);
        ReflectionTestUtils.setField(article2, "createdAt", article2Time);
        ReflectionTestUtils.setField(article3, "createdAt", article3Time);

        ZoneId zone = ZoneId.of("Asia/Seoul");

        CursorPageFilter filter = new CursorPageFilter(
                null,
                interest.getId(),
                Arrays.asList("NAVER"),
                article1Time.atZone(zone).toLocalDateTime(),
                article3Time.atZone(zone).toLocalDateTime(),
                "publishDate",
                "ASC",
                null,
                article2Time,
                10
        );

        List<String> keywordList = List.of();

        // when
        List<Article> result = articleRepository.findByCursorFilter(filter, keywordList);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("AI3");
    }

    @Test
    void 뉴스기사_출처를_중복없이_조회할_수_있다() {
        // given
        Article article1 = new Article("NAVER", "https://...1", "AI", "경제", false, Instant.now(), Instant.now(), new ArrayList<>());
        Article article2 = new Article("한국경제", "https://...2", "AI2", "경제2", false, Instant.now(), Instant.now(), new ArrayList<>());
        Article article3 = new Article("연합뉴스", "https://...3", "AI3", "경제3", false, Instant.now(), Instant.now(), new ArrayList<>());
        Article article4 = new Article("연합뉴스", "https://...4", "AI4", "경제4", false, Instant.now(), Instant.now(), new ArrayList<>());

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

    @Test
    void sourceUrl로_기사_존재_여부를_확인할_수_있다() {
        // given
        Article article = new Article("NAVER", "https://test.com", "AI", "경제", false, Instant.now(), Instant.now(), new ArrayList<>());
        articleRepository.save(article);

        // when
        boolean exists = articleRepository.existsBySourceUrl("https://test.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void sourceUrl_목록으로_기사_목록을_조회할_수_있다() {
        // given
        Article article1 = new Article("NAVER", "https://test1.com", "AI", "경제", false, Instant.now(), Instant.now(), new ArrayList<>());
        Article article2 = new Article("한국경제", "https://test2.com", "AI2", "경제2", false, Instant.now(), Instant.now(), new ArrayList<>());
        articleRepository.saveAll(List.of(article1, article2));

        // when
        List<Article> result = articleRepository.findAllBySourceUrlIn(List.of("https://test1.com", "https://test2.com"));

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    void 특정_시간_이후_기사들을_조회할_수_있다() {
        // given
        articleRepository.deleteAll();
        Instant baseTime = Instant.parse("2025-07-24T00:00:00Z").truncatedTo(ChronoUnit.MILLIS);

        Instant oldCreatedAt = baseTime.minusSeconds(60);
        Instant newCreatedAt = baseTime.plusSeconds(60);

        Article oldArticle = new Article("NAVER", "https://old.com", "AI", "경제", false, oldCreatedAt.truncatedTo(ChronoUnit.MILLIS), oldCreatedAt.truncatedTo(ChronoUnit.MILLIS), new ArrayList<>());
        Article newArticle = new Article("한국경제", "https://new.com", "AI2", "경제2", false, newCreatedAt.truncatedTo(ChronoUnit.MILLIS), newCreatedAt.truncatedTo(ChronoUnit.MILLIS), new ArrayList<>());

        articleRepository.saveAll(List.of(oldArticle, newArticle));
        em.flush();

        ReflectionTestUtils.setField(oldArticle, "createdAt", oldCreatedAt);
        ReflectionTestUtils.setField(newArticle, "createdAt", newCreatedAt);
        articleRepository.findAll().forEach((article) -> {
            System.out.println(article.getCreatedAt());
        });
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Article> result = articleRepository.findByCreatedAtAfterOrderByCreatedAtAsc(baseTime, pageable);

        // then
        assertThat(result).extracting("sourceUrl").containsExactly("https://new.com");
    }

    @Test
    void 필터로_기사를_조회할_수_있다() {
        // given
        Interest interest = new Interest(Instant.now(), "IT", 0, new ArrayList<>(), new ArrayList<>());
        Keyword k1 = new Keyword(Instant.now(), "AI", interest);
        Keyword k2 = new Keyword(Instant.now(), "블록체인", interest);

        Instant now = Instant.now();
        Article a1 = new Article("NAVER", "https://a1.com", "AI 관련 뉴스", "내용", false, Instant.now(), now.minusSeconds(30), new ArrayList<>());
        Article a2 = new Article("NAVER", "https://a2.com", "블록체인 혁신", "내용", false, Instant.now(), now.minusSeconds(20), new ArrayList<>());
        Article a3 = new Article("NAVER", "https://a3.com", "일반 뉴스", "내용", false, Instant.now(), now.minusSeconds(10), new ArrayList<>());

        articleRepository.saveAll(List.of(a1, a2, a3));

        CursorPageFilter filter = new CursorPageFilter(
                null,
                interest.getId(),
                List.of("NAVER"),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                "publishDate",
                "DESC",
                null,
                null,
                10
        );

        // when
        List<Article> result = articleRepository.findByCursorFilter(filter, List.of("AI", "블록체인"));

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).contains("블록체인");
        assertThat(result.get(1).getTitle()).contains("AI");
    }

    @Test
    void 키워드가_없으면_결과가_없다() {
        // given
        Article a1 = new Article("NAVER", "https://a1.com", "기타 뉴스", "기타 요약", false, Instant.now(), Instant.now(), new ArrayList<>());
        articleRepository.save(a1);

        CursorPageFilter filter = new CursorPageFilter(
                null,
                null,
                List.of("NAVER"),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                "publishDate",
                "DESC",
                null,
                null,
                10
        );

        // when
        List<Article> result = articleRepository.findByCursorFilter(filter, List.of("AI"));

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 필터로_기사_수를_세어볼_수_있다() {
        // given
        Interest interest = new Interest(Instant.now(), "IT", 0, new ArrayList<>(), new ArrayList<>());
        Keyword k1 = new Keyword(Instant.now(), "AI", interest);
        Keyword k2 = new Keyword(Instant.now(), "블록체인", interest);

        Instant now = Instant.now();
        Article a1 = new Article("NAVER", "https://a1.com", "AI 관련 뉴스", "내용", false, Instant.now(), now.minusSeconds(30), new ArrayList<>());
        Article a2 = new Article("NAVER", "https://a2.com", "블록체인 혁신", "내용", false, Instant.now(), now.minusSeconds(20), new ArrayList<>());
        Article a3 = new Article("NAVER", "https://a3.com", "일반 뉴스", "내용", false, Instant.now(), now.minusSeconds(10), new ArrayList<>());

        articleRepository.saveAll(List.of(a1, a2, a3));

        CursorPageFilter filter = new CursorPageFilter(
                null,
                interest.getId(),
                List.of("NAVER"),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                "publishDate",
                "DESC",
                null,
                null,
                10
        );

        // when
        long count = articleRepository.countByCursorFilter(filter, List.of("AI", "블록체인"));

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void 조회기준이_viewCount일_때_정렬된다() {
        Article a1 = new Article("NAVER", "https://v1.com", "AI", "내용", false, Instant.now(), Instant.now(), new ArrayList<>());
        Article a2 = new Article("NAVER", "https://v2.com", "AI2", "내용", false, Instant.now(), Instant.now(), new ArrayList<>());
        em.persist(a1);
        em.persist(a2);
        em.flush();

        CursorPageFilter filter = new CursorPageFilter(
                null,
                null,
                List.of("NAVER"),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                "viewCount",
                "ASC",
                null,
                null,
                10
        );

        List<Article> result = articleRepository.findByCursorFilter(filter, List.of("AI", "AI2"));

        assertThat(result).hasSize(2);
    }

    @Test
    void 키워드_검색조건이_있으면_제목이나_요약에_포함된_기사만_조회된다() {
        Article a1 = new Article("NAVER", "https://kw1.com", "AI 기술 발전", "내용", false, Instant.now(), Instant.now(), new ArrayList<>());
        Article a2 = new Article("NAVER", "https://kw2.com", "일반 뉴스", "AI 키워드 있음", false, Instant.now(), Instant.now(), new ArrayList<>());
        Article a3 = new Article("NAVER", "https://kw3.com", "일반 뉴스", "내용", false, Instant.now(), Instant.now(), new ArrayList<>());
        em.persist(a1);
        em.persist(a2);
        em.persist(a3);
        em.flush();

        CursorPageFilter filter = new CursorPageFilter(
                "AI",
                null,
                List.of("NAVER"),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                "publishDate",
                "DESC",
                null,
                null,
                10
        );

        List<Article> result = articleRepository.findByCursorFilter(filter, List.of());

        assertThat(result).hasSize(2);
        assertThat(result).extracting("sourceUrl").containsExactlyInAnyOrder("https://kw1.com", "https://kw2.com");
    }

    @Test
    void 댓글_수로_정렬하여_조회할_수_있다() {
        // given
        User user1 = new User("test@naver.com", "test", "0000", Instant.now(), false);
        User user2 = new User("test2@naver.com", "test2", "0000", Instant.now(), false);
        em.persist(user1);
        em.persist(user2);
        em.flush();

        Article a1 = new Article("NAVER", "https://a1.com", "뉴스1", "내용1", false, Instant.now(), Instant.now(), new ArrayList<>());
        Article a2 = new Article("NAVER", "https://a2.com", "뉴스2", "내용2", false, Instant.now(), Instant.now(), new ArrayList<>());
        em.persist(a1);
        em.persist(a2);

        Comment c1 = new Comment(a2, user1, "댓글1");
        ReflectionTestUtils.setField(c1, "createdAt", Instant.now());
        Comment c2 = new Comment(a2, user2, "댓글2");
        ReflectionTestUtils.setField(c2, "createdAt", Instant.now());
        Comment c3 = new Comment(a1, user1, "댓글3");
        ReflectionTestUtils.setField(c3, "createdAt", Instant.now());

        em.persist(c1);
        em.persist(c2);
        em.persist(c3);
        em.flush();

        CursorPageFilter filter = new CursorPageFilter(
                null, null,
                List.of("NAVER"),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                "commentCount", "DESC", null, null, 10
        );

        // when
        List<Article> result = articleRepository.findByCursorFilter(filter, List.of());

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSourceUrl()).isEqualTo("https://a2.com"); // 댓글 2개
        assertThat(result.get(1).getSourceUrl()).isEqualTo("https://a1.com"); // 댓글 1개
    }

    @Test
    void 시작일만_있는_필터로_조회할_수_있다() {
        Instant now = Instant.now();
        Instant base = now.minusSeconds(100);

        Article a1 = new Article("NAVER", "https://a1.com", "제목1", "내용1", false, Instant.now(), now.minusSeconds(90), new ArrayList<>());
        Article a2 = new Article("NAVER", "https://a2.com", "제목2", "내용2", false, Instant.now(), now.minusSeconds(10), new ArrayList<>());

        articleRepository.saveAll(List.of(a1, a2));

        CursorPageFilter filter = new CursorPageFilter(
                null,
                null,
                List.of("NAVER"),
                base.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime(), // From만 설정
                null,
                "publishDate",
                "ASC",
                null,
                null,
                10
        );

        List<Article> result = articleRepository.findByCursorFilter(filter, List.of());

        assertThat(result).hasSize(2);
    }

    @Test
    void 종료일만_있는_필터로_조회할_수_있다() {
        Instant now = Instant.now();
        Instant base = now.minusSeconds(50);

        Article a1 = new Article("NAVER", "https://a1.com", "제목1", "내용1", false, Instant.now(), now.minusSeconds(90), new ArrayList<>());
        Article a2 = new Article("NAVER", "https://a2.com", "제목2", "내용2", false, Instant.now(), now.minusSeconds(10), new ArrayList<>());

        articleRepository.saveAll(List.of(a1, a2));
        ReflectionTestUtils.setField(a1, "createdAt", now.minusSeconds(90));
        ReflectionTestUtils.setField(a2, "createdAt", now.minusSeconds(10));

        CursorPageFilter filter = new CursorPageFilter(
                null,
                null,
                List.of("NAVER"),
                null,
                base.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime(),
                "publishDate",
                "ASC",
                null,
                null,
                10
        );

        List<Article> result = articleRepository.findByCursorFilter(filter, List.of());

        assertThat(result).hasSize(1); // base 이전 기사 1개
        assertThat(result.get(0).getSourceUrl()).isEqualTo("https://a1.com");
    }
}
