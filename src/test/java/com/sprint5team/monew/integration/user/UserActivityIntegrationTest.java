package com.sprint5team.monew.integration.user;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.entity.ArticleCount;
import com.sprint5team.monew.domain.article.mapper.ArticleViewMapper;
import com.sprint5team.monew.domain.article.repository.ArticleCountRepository;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.comment.dto.CommentDto;
import com.sprint5team.monew.domain.comment.dto.CommentLikeDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

  @Autowired
  private EntityManager entityManager;

  @Mock private UserRepository userRepository;
  @Mock private ArticleRepository articleRepository;
  @Mock private ArticleCountRepository articleCountRepository;
  @Mock private ArticleViewMapper articleViewMapper;
  @Mock private InterestRepository interestRepository;
  @Mock private KeywordRepository keywordRepository;
  @Mock private UserInterestRepository userInterestRepository;
  @Mock private UserInterestMapper userInterestMapper;
  @Mock private CommentRepository commentRepository;
  @Mock private LikeRepository likeRepository;
  @Mock private CommentMapper commentMapper;

  @Test
  void 사용자_활동_조회_API_통합_테스트() throws Exception {
    // given
    User user = userRepository.save(new User("test@test.kr","test","test1234"));
    User otherUser = userRepository.save(new User("other@test.kr","other","test1234"));

    Article article = articleRepository.save(new Article("Naver","http://www.nspna.com/news/?mode=view&newsid=42872","뉴스 제목","요약",Instant.now()));
    ArticleCount articleCount = articleCountRepository.save(new ArticleCount(article, user));
    ArticleViewDto articleViewDto = articleViewMapper.toDto(article, user, articleCount);

    Interest interest = interestRepository.save(new Interest(Instant.now(), "test", 2L));
    keywordRepository.save(new Keyword(Instant.now(), "축구", interest));
    keywordRepository.save(new Keyword(Instant.now(), "야구", interest));
    UserInterest userInterest = userInterestRepository.save(new UserInterest(Instant.now(), user, interest));
    SubscriptionDto subscriptionDto = userInterestMapper.toDto(userInterest);

    Comment comment = commentRepository.save(new Comment(article, user, "댓글 내용 입니다."));
    CommentDto commentDto = commentMapper.toDto(comment);

    Comment commentFromOther =  commentRepository.save(new Comment(article, otherUser, "다른 사람이 단 댓글 내용입니다."));

    Like like = likeRepository.save(new Like(commentFromOther, user));
    CommentLikeDto commentLikeDto = commentMapper.toDto(like);

    entityManager.flush();
    entityManager.clear();

    List<SubscriptionDto> subscriptions = new ArrayList<>();
    subscriptions.add(subscriptionDto);

    List<CommentDto> comments = new ArrayList<>();
    comments.add(commentDto);

    List<CommentLikeDto> commentLikes = new ArrayList<>();
    commentLikes.add(commentLikeDto);

    List<ArticleViewDto> articleViews = new ArrayList<>();
    articleViews.add(articleViewDto);

    given(userActivityService.getUserActivity(user.getId())).willReturn(new UserActivityDto(user.getId(), "test@test.kr", "test", Instant.now(), subscriptions, comments, commentLikes, articleViews));

    // when and then
    mockMvc.perform(get("/api/user-activities/" + user.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(user.getId().toString()))
        .andExpect(jsonPath("$.email").value("test@test.kr"))
        .andExpect(jsonPath("$.nickname").value("test"))
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.subscriptions").isArray())
        .andExpect(jsonPath("$.subscriptions[0].userId").value(user.getId().toString()))
        .andExpect(jsonPath("$.subscriptions[0].interestId").value(interest.getId().toString()))
        .andExpect(jsonPath("$.subscriptions[0].interestType").value("interest"))
        .andExpect(jsonPath("$.subscriptions[0].keywords[0]").value("축구"))
        .andExpect(jsonPath("$.subscriptions[0].keywords[1]").value("야구"))
        .andExpect(jsonPath("$.subscriptions[0].count").value(2L))
        .andExpect(jsonPath("$.subscriptions[0].createdAt").exists())
        .andExpect(jsonPath("$.comments").isArray())
        .andExpect(jsonPath("$.comments[0].id").value(comment.getId().toString()))
        .andExpect(jsonPath("$.comments[0].articleId").value(article.getId().toString()))
        .andExpect(jsonPath("$.comments[0].userId").value(user.getId().toString()))
        .andExpect(jsonPath("$.comments[0].userNickname").value("test"))
        .andExpect(jsonPath("$.comments[0].content").value("댓글 내용 입니다."))
        .andExpect(jsonPath("$.comments[0].likeCount").value(0L))
        .andExpect(jsonPath("$.comments[0].likedByMe").value(false))
        .andExpect(jsonPath("$.comments[0].createdAt").exists())
        .andExpect(jsonPath("$.commentLikes").isArray())
        .andExpect(jsonPath("$.commentLikes[0].id").value(like.getId().toString()))
        .andExpect(jsonPath("$.commentLikes[0].userId").value(user.getId().toString()))
        .andExpect(jsonPath("$.commentLikes[0].createdAt").exists())
        .andExpect(jsonPath("$.commentLikes[0].articleId").value(article.getId().toString()))
        .andExpect(jsonPath("$.commentLikes[0].commentId").value(comment.getId().toString()))
        .andExpect(jsonPath("$.commentLikes[0].commentUserId").value(otherUser.getId().toString()))
        .andExpect(jsonPath("$.commentLikes[0].commentUserNickname").value("commentUser"))
        .andExpect(jsonPath("$.commentLikes[0].commentUserContent").value("내가 좋아하는 댓글 내용"))
        .andExpect(jsonPath("$.commentLikes[0].likeCount").isNotEmpty())
        .andExpect(jsonPath("$.commentLikes[0].likedByMe").value(true))
        .andExpect(jsonPath("$.articleViews").isArray())
        .andExpect(jsonPath("$.articleViews[0].id").value(articleCount.getId().toString()))
        .andExpect(jsonPath("$.articleViews[0].userId").value(user.getId().toString()))
        .andExpect(jsonPath("$.articleViews[0].createdAt").exists())
        .andExpect(jsonPath("$.articleViews[0].articleId").value(article.getId().toString()))
        .andExpect(jsonPath("$.articleViews[0].source").value("NAVER"))
        .andExpect(jsonPath("$.articleViews[0].link").value("http://www.nspna.com/news/?mode=view&newsid=42872"))
        .andExpect(jsonPath("$.articleViews[0].title").value("뉴스 제목"))
        .andExpect(jsonPath("$.articleViews[0].createdAt").exists())
        .andExpect(jsonPath("$.articleViews[0].summary").value("요약"))
        .andExpect(jsonPath("$.articleViews[0].likeCount").isNotEmpty())
        .andExpect(jsonPath("$.articleViews[0].viewCount").isNotEmpty());
  }

  @Test
  void 존재하지_않는_사용자_활동_조회_실패() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    mockMvc.perform(get("/api/user-activities/"+userId))
        .andExpect(status().isBadRequest());
  }
}
