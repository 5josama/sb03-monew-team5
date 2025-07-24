package com.sprint5team.monew.integration.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.entity.ArticleCount;
import com.sprint5team.monew.domain.article.mapper.ArticleViewMapper;
import com.sprint5team.monew.domain.article.repository.ArticleCountRepository;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.comment.dto.CommentActivityDto;
import com.sprint5team.monew.domain.comment.dto.CommentLikeActivityDto;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.entity.Like;
import com.sprint5team.monew.domain.comment.mapper.CommentMapper;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.comment.repository.LikeRepository;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import com.sprint5team.monew.domain.user.dto.UserActivityDto;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.user.service.UserActivityServiceImpl;
import com.sprint5team.monew.domain.user_interest.dto.SubscriptionDto;
import com.sprint5team.monew.domain.user_interest.entity.UserInterest;
import com.sprint5team.monew.domain.user_interest.mapper.UserInterestMapper;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserActivityIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserActivityServiceImpl userActivityService;

  @Autowired private UserRepository userRepository;
  @Autowired private InterestRepository interestRepository;
  @Autowired private KeywordRepository keywordRepository;
  @Autowired private UserInterestRepository userInterestRepository;
  @Autowired private CommentRepository commentRepository;
  @Autowired private LikeRepository likeRepository;
  @Autowired private ArticleViewMapper articleViewMapper;
  @Autowired private ArticleRepository articleRepository;
  @Autowired private ArticleCountRepository articleCountRepository;
  @Autowired private CommentMapper commentMapper;
  @Autowired private UserInterestMapper userInterestMapper;

  @Autowired
  private EntityManager entityManager;

  @Test
  void 사용자_활동_조회_API_통합_테스트() throws Exception {
    // given
    User user = User.builder()
        .email("test@test.kr")
        .nickname("test")
        .password("test1234")
        .isDeleted(false)
        .build();
    userRepository.save(user);
    entityManager.flush();

    Article article = new Article("NAVER", "https://naver.com/news/12333", "기사제목", "요약", Instant.now());
    articleRepository.save(article);
    entityManager.flush();

    ArticleCount articleCount = new ArticleCount(article, user);
    articleCountRepository.save(articleCount);
    entityManager.flush();

    Comment comment = new Comment(article, user, "댓글 내용 입니다.");
    commentRepository.save(comment);
    entityManager.flush();

    User commentUser = User.builder()
        .email("comment@test.kr")
        .nickname("commentUser")
        .password("test1234")
        .isDeleted(false)
        .build();
    userRepository.save(commentUser);
    entityManager.flush();

    Comment commentByOtherUser = new Comment(article, commentUser, "다른 사용자가 작성한 댓글 내용 입니다.");
    commentByOtherUser.update(1L);
    commentRepository.save(commentByOtherUser);
    entityManager.flush();

    Like like = new Like(commentByOtherUser, user);
    likeRepository.save(like);
    entityManager.flush();

    Interest interest = new Interest("스포츠");
    interest.subscribe();
    interestRepository.save(interest);
    entityManager.flush();

    Keyword keword1 = new Keyword(Instant.now(), "축구", interest);
    Keyword keword2 = new Keyword(Instant.now(), "야구", interest);
    keywordRepository.save(keword1);
    keywordRepository.save(keword2);
    entityManager.flush();

    UserInterest userInterest = new UserInterest(Instant.now(), user, interest);
    userInterestRepository.save(userInterest);
    entityManager.flush();

    entityManager.clear();

    SubscriptionDto subscriptionDto = userInterestMapper.toDto(userInterest);
    CommentActivityDto commentActivityDto = commentMapper.toActivityDto(comment);
    CommentLikeActivityDto commentLikeActivityDto = commentMapper.toActivityDto(like);
    ArticleViewDto articleViewDto = articleViewMapper.toDto(article, user, articleCount, 1L, 2L);

    UserActivityDto userActivityDto = userActivityService.getUserActivity(user.getId());

    // when and then
    mockMvc.perform(get("/api/user-activities/{userId}", user.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId().toString()))
        .andExpect(jsonPath("$.email").value("test@test.kr"))
        .andExpect(jsonPath("$.nickname").value("test"))
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.subscriptions").isArray())
        .andExpect(jsonPath("$.subscriptions[0].id").value(userInterest.getId().toString()))
        .andExpect(jsonPath("$.subscriptions[0].interestId").value(interest.getId().toString()))
        .andExpect(jsonPath("$.subscriptions[0].interestName").value("스포츠"))
        .andExpect(jsonPath("$.subscriptions[0].interestKeywords[0]").value("축구"))
        .andExpect(jsonPath("$.subscriptions[0].interestKeywords[1]").value("야구"))
        .andExpect(jsonPath("$.subscriptions[0].interestSubscriberCount").value(1L))
        .andExpect(jsonPath("$.subscriptions[0].createdAt").exists())
        .andExpect(jsonPath("$.comments").isArray())
        .andExpect(jsonPath("$.comments[0].id").value(comment.getId().toString()))
        .andExpect(jsonPath("$.comments[0].articleId").value(article.getId().toString()))
        .andExpect(jsonPath("$.comments[0].articleTitle").value("기사제목"))
        .andExpect(jsonPath("$.comments[0].userId").value(user.getId().toString()))
        .andExpect(jsonPath("$.comments[0].userNickname").value("test"))
        .andExpect(jsonPath("$.comments[0].content").value("댓글 내용 입니다."))
        .andExpect(jsonPath("$.comments[0].likeCount").value(0L))
        .andExpect(jsonPath("$.comments[0].createdAt").exists())
        .andExpect(jsonPath("$.commentLikes").isArray())
        .andExpect(jsonPath("$.commentLikes[0].id").value(like.getId().toString()))
        .andExpect(jsonPath("$.commentLikes[0].createdAt").exists())
        .andExpect(jsonPath("$.commentLikes[0].commentId").value(commentByOtherUser.getId().toString()))
        .andExpect(jsonPath("$.commentLikes[0].articleId").value(article.getId().toString()))
        .andExpect(jsonPath("$.commentLikes[0].articleTitle").value("기사제목"))
        .andExpect(jsonPath("$.commentLikes[0].commentUserId").value(commentUser.getId().toString()))
        .andExpect(jsonPath("$.commentLikes[0].commentUserNickname").value("commentUser"))
        .andExpect(jsonPath("$.commentLikes[0].commentContent").value("다른 사용자가 작성한 댓글 내용 입니다."))
        .andExpect(jsonPath("$.commentLikes[0].commentLikeCount").value(1L))
        .andExpect(jsonPath("$.commentLikes[0].commentCreatedAt").exists())
        .andExpect(jsonPath("$.articleViews").isArray())
        .andExpect(jsonPath("$.articleViews[0].id").value(articleCount.getId().toString()))
        .andExpect(jsonPath("$.articleViews[0].viewedBy").value(user.getId().toString()))
        .andExpect(jsonPath("$.articleViews[0].createdAt").exists())
        .andExpect(jsonPath("$.articleViews[0].articleId").value(article.getId().toString()))
        .andExpect(jsonPath("$.articleViews[0].source").value("NAVER"))
        .andExpect(jsonPath("$.articleViews[0].sourceUrl").value("https://naver.com/news/12333"))
        .andExpect(jsonPath("$.articleViews[0].articleTitle").value("기사제목"))
        .andExpect(jsonPath("$.articleViews[0].articlePublishDate").exists())
        .andExpect(jsonPath("$.articleViews[0].articleSummary").value("요약"))
        .andExpect(jsonPath("$.articleViews[0].articleCommentCount").value(2L))
        .andExpect(jsonPath("$.articleViews[0].articleViewCount").value(1L));
  }

  @Test
  void 존재하지_않는_사용자_활동_조회_실패() throws Exception {
    // given
    User user = User.builder()
        .email("test@test.kr")
        .nickname("test")
        .password("test1234")
        .isDeleted(false)
        .build();
    userRepository.save(user);
    entityManager.flush();

    Article article = new Article("NAVER", "https://naver.com/news/12333", "기사제목", "요약", Instant.now());
    articleRepository.save(article);
    entityManager.flush();

    ArticleCount articleCount = new ArticleCount(article, user);
    articleCountRepository.save(articleCount);
    entityManager.flush();

    Comment comment = new Comment(article, user, "댓글 내용 입니다.");
    commentRepository.save(comment);
    entityManager.flush();

    User commentUser = User.builder()
        .email("comment@test.kr")
        .nickname("commentUser")
        .password("test1234")
        .isDeleted(false)
        .build();
    userRepository.save(commentUser);
    entityManager.flush();

    Comment commentByOtherUser = new Comment(article, commentUser, "다른 사용자가 작성한 댓글 내용 입니다.");
    commentByOtherUser.update(1L);
    commentRepository.save(commentByOtherUser);
    entityManager.flush();

    Like like = new Like(commentByOtherUser, user);
    likeRepository.save(like);
    entityManager.flush();

    Interest interest = new Interest("스포츠");
    interestRepository.save(interest);
    entityManager.flush();

    Keyword keword1 = new Keyword(Instant.now(), "축구", interest);
    Keyword keword2 = new Keyword(Instant.now(), "야구", interest);
    keywordRepository.save(keword1);
    keywordRepository.save(keword2);
    entityManager.flush();

    UserInterest userInterest = new UserInterest(Instant.now(), user, interest);
    userInterestRepository.save(userInterest);
    entityManager.flush();

    entityManager.clear();

    SubscriptionDto subscriptionDto = userInterestMapper.toDto(userInterest);
    CommentActivityDto commentActivityDto = commentMapper.toActivityDto(comment);
    CommentLikeActivityDto commentLikeActivityDto = commentMapper.toActivityDto(like);
    ArticleViewDto articleViewDto = articleViewMapper.toDto(article, user, articleCount, 1L, 2L);

    UserActivityDto userActivityDto = new UserActivityDto(
        user.getId(),
        "test@test.kr",
        "test",
        Instant.now(),
        List.of(subscriptionDto),
        List.of(commentActivityDto),
        List.of(commentLikeActivityDto),
        List.of(articleViewDto)
    );
  }
}
