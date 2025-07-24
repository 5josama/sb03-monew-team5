package com.sprint5team.monew.integration.article;

import com.sprint5team.monew.domain.article.dto.CursorPageFilter;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.entity.ArticleCount;
import com.sprint5team.monew.domain.article.repository.ArticleCountRepository;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.article.service.ArticleService;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
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
    @Autowired private KeywordRepository keywordRepository;
    @Autowired private InterestRepository interestRepository;
    @Autowired private CommentRepository commentRepository;

    private User user;

    private Article article;

    @BeforeEach
    void setUp() {
        user = new User("test@naver.com","test", "12345678");
        userRepository.save(user);

        article = new Article(
                "NAVER",
                "https://naver.com/news/123331",
                "title",
                "요약",
                false,
                Instant.now(),
                Instant.now(),
                new ArrayList<>()
        );
        articleRepository.save(article);
    }

    @AfterEach
    void tearDown() {
        articleCountRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void 유저가_뉴스를_클릭하면_뉴스_조회수_테이블에_등록_되어야_한다() {
        // when
        articleService.saveArticleView(article.getId(), user.getId());

        // then
        Optional<ArticleCount> articleCount = articleCountRepository.findByUserIdAndArticleId(user.getId(), article.getId());
        assertThat(articleCount).isPresent();
    }

    @Test
    void 유저가_뉴스를_클릭했을_때_뉴스_조회수_테이블에_데이터가_존재하면_저장하지_않는다() {
        articleService.saveArticleView(article.getId(), user.getId());

        // when
        articleService.saveArticleView(article.getId(), user.getId());

        // then
        List<ArticleCount> articleCounts = articleCountRepository.findAllByUserIdAndArticleId(user.getId(), article.getId());
        assertThat(articleCounts).hasSize(1);
    }

    @Test
    void 주어진_관심사를_포함한_뉴스기사를_게시일을_기준으로_내림차순_정렬후_조회할_수_있다() {
        // given
        Interest interest = new Interest(Instant.now(), "IT", 0, new ArrayList<>(), new ArrayList<>());
        interestRepository.save(interest);

        Keyword keyword1 = new Keyword(Instant.now(), "AI", interest);
        Keyword keyword2 = new Keyword(Instant.now(), "블록체인", interest);
        keywordRepository.save(keyword1);
        keywordRepository.save(keyword2);

        Instant now = Instant.now();
        Instant time1 = now.minusSeconds(10);
        Instant time2 = now.minusSeconds(20);
        Instant time3 = now.minusSeconds(30);

        Article article1 = new Article("NAVER", "https://a.com", "AI 혁신", "미래 변화", false, time1, time1, new ArrayList<>());
        Article article2 = new Article("NAVER", "https://b.com", "블록체인과 사회", "IT 기술", false, time2, time2, new ArrayList<>());
        Article article3 = new Article("한국경제", "https://c.com", "일반 뉴스", "정치 이슈", false, time3, time3, new ArrayList<>());

        articleRepository.save(article1);
        articleRepository.save(article2);
        articleRepository.save(article3);
        articleRepository.flush();

        List<String> keywords = List.of("AI", "블록체인");

        ZoneId zone = ZoneId.of("Asia/Seoul");

        Instant fromInstant = now.minusSeconds(60); // 충분히 넓게
        Instant toInstant = now.plusSeconds(60);

        CursorPageFilter filter = new CursorPageFilter(
                null,
                interest.getId(),
                Arrays.asList("NAVER"),
                fromInstant.atZone(zone).toLocalDateTime(),
                toInstant.atZone(zone).toLocalDateTime(),
                "publishDate",
                "DESC",
                null,
                null,
                10
        );

        // when
        List<Article> result = articleRepository.findByCursorFilter(filter, keywords);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).contains("블록체인");
        assertThat(result.get(1).getTitle()).contains("AI");
    }

    @Test
    void 뉴스기사_출처_목록을_조회할_수_있다() {
        // given
        Article article1 = new Article("NAVER", "https://a.com", "AI 혁신", "미래 변화", false, Instant.now(), Instant.now(), new ArrayList<>());
        Article article2 = new Article("NAVER", "https://b.com", "블록체인과 사회", "IT 기술", false, Instant.now(), Instant.now(), new ArrayList<>());
        Article article3 = new Article("한국경제", "https://c.com", "일반 뉴스", "정치 이슈", false, Instant.now(), Instant.now(), new ArrayList<>());

        articleRepository.save(article1);
        articleRepository.save(article2);
        articleRepository.save(article3);
        articleRepository.flush();

        // when
        List<String> sources = articleService.getSources();

        // then
        assertThat(sources).hasSize(2);
        assertThat(sources.get(1)).isEqualTo("한국경제");
    }

    @Test
    void 주어진_ID로_뉴스기사를_논리_삭제_할_수_있다() {
        // given
        Article article1 = new Article("NAVER", "https://a.com", "AI 혁신", "미래 변화", false, Instant.now(), Instant.now(), new ArrayList<>());
        Article article2 = new Article("NAVER", "https://b.com", "블록체인과 사회", "IT 기술", false, Instant.now(), Instant.now(), new ArrayList<>());
        Article article3 = new Article("한국경제", "https://c.com", "일반 뉴스", "정치 이슈", false, Instant.now(), Instant.now(), new ArrayList<>());

        articleRepository.save(article1);
        articleRepository.save(article2);
        articleRepository.save(article3);
        articleRepository.flush();

        // when
        articleService.softDeleteArticle(article2.getId());

        // then
        Article updated = articleRepository.findById(article2.getId()).orElseThrow();
        assertThat(updated.isDeleted()).isTrue();
    }

    @Test
    void 주어진_ID로_뉴스기사를_물리_삭제_할_수_있다() {
        // given
        Article article1 = new Article("NAVER", "https://a.com", "AI 혁신", "미래 변화", false, Instant.now(), Instant.now(), new ArrayList<>());
        Article article2 = new Article("NAVER", "https://b.com", "블록체인과 사회", "IT 기술", false, Instant.now(), Instant.now(), new ArrayList<>());
        Article article3 = new Article("한국경제", "https://c.com", "일반 뉴스", "정치 이슈", false, Instant.now(), Instant.now(), new ArrayList<>());

        articleRepository.save(article1);
        articleRepository.save(article2);
        articleRepository.save(article3);
        articleRepository.flush();

        Comment comment = new Comment(article2, user, "test 댓글");
        commentRepository.save(comment);
        ArticleCount viewCount = new ArticleCount(article2, user);
        articleCountRepository.save(viewCount);

        // when
        articleService.hardDeleteArticle(article2.getId());

        // then
        List<Comment> comments = commentRepository.findByArticleId(article2.getId());
        assertThat(comments).isEmpty();

        List<ArticleCount> views = articleCountRepository.findByArticleId(article2.getId());
        assertThat(views).isEmpty();

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
        assertThat(articleCountRepository.findById(article2.getId())).isEmpty();
    }
}
