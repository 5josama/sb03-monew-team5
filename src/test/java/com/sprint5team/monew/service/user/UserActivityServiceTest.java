package com.sprint5team.monew.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.entity.ArticleCount;
import com.sprint5team.monew.domain.article.mapper.ArticleViewMapper;
import com.sprint5team.monew.domain.article.repository.ArticleCountRepository;
import com.sprint5team.monew.domain.comment.dto.CommentActivityDto;
import com.sprint5team.monew.domain.comment.dto.CommentLikeActivityDto;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.entity.Like;
import com.sprint5team.monew.domain.comment.mapper.CommentMapper;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.comment.repository.LikeRepository;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.user.dto.UserActivityDto;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.exception.UserNotFoundException;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.user.service.UserActivityServiceImpl;
import com.sprint5team.monew.domain.user_interest.dto.SubscriptionDto;
import com.sprint5team.monew.domain.user_interest.entity.UserInterest;
import com.sprint5team.monew.domain.user_interest.mapper.UserInterestMapper;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserActivityServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserInterestRepository userInterestRepository;
  @Mock
  private CommentRepository commentRepository;
  @Mock
  private LikeRepository likeRepository;
  @Mock
  private ArticleViewMapper articleViewMapper;
  @Mock
  private ArticleCountRepository articleCountRepository;
  @Mock
  private UserInterestMapper userInterestMapper;
  @Mock
  private CommentMapper commentMapper;

  @InjectMocks
  private UserActivityServiceImpl userActivityService;

  @Test
  void 사용자_활동_내역_조회_성공() {
    // given
    User user = User.builder()
        .email("test@test.kr")
        .nickname("test")
        .password("test1234")
        .build();
    UUID userId = UUID.randomUUID();
    ReflectionTestUtils.setField(user, "id", userId);
    given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

    // 관심사
    Interest interest = new Interest("test");
    Keyword keyword1 = new Keyword(Instant.now(), "test1", interest);
    Keyword keyword2 = new Keyword(Instant.now(), "test2", interest);
    UserInterest userInterest = new UserInterest(Instant.now(), user, interest);
    given(userInterestRepository.findTop10ByUserIdOrderByCreatedAtDesc(user.getId())).willReturn(
        List.of(userInterest));

    List<String> interestKeywords = List.of(keyword1.getName(), keyword2.getName());
    given(userInterestMapper.toDto(userInterest)).willReturn(
        new SubscriptionDto(userInterest.getId(), interest.getId(), interest.getName(),
            interestKeywords, 2L, Instant.now()));

    // 기사
    Article article1 = new Article("Naver", "http://naver.com/testURL", "테스트 기사", "테스트 기사 내용 요약",
        Instant.now());
    Article article2 = new Article("Naver", "http://naver.com/testURL", "테스트 기사2", "테스트 기사 내용 요약2",
        Instant.now());

    ArticleCount articleCount1 = new ArticleCount(article1, user);
    ArticleCount articleCount2 = new ArticleCount(article2, user);
    given(articleCountRepository.findTop10ByUserIdOrderByCreatedAtDesc(user.getId())).willReturn(
        List.of(articleCount1, articleCount2));

    ArticleViewDto articleViewDto1 = new ArticleViewDto(articleCount1.getId(), user.getId(),
        articleCount1.getCreatedAt(), article1.getId(), article1.getSource(),
        article1.getSourceUrl(), article1.getTitle(), article1.getOriginalDateTime(),
        article1.getSummary(), 1L, 1L);
    ArticleViewDto articleViewDto2 = new ArticleViewDto(articleCount2.getId(), user.getId(),
        articleCount2.getCreatedAt(), article2.getId(), article2.getSource(),
        article2.getSourceUrl(), article2.getTitle(), article2.getOriginalDateTime(),
        article2.getSummary(), 1L, 1L);
    doReturn(articleViewDto1, articleViewDto2)
        .when(articleViewMapper)
        .toDto(nullable(Article.class), any(User.class), nullable(ArticleCount.class), nullable(Long.class), nullable(Long.class));

    // 댓글
    Comment comment = new Comment(article1, user, "content");
    given(commentRepository.findTop10ByUserIdOrderByCreatedAtDesc(user.getId())).willReturn(
        List.of(comment));
    given(commentMapper.toActivityDto(comment)).willReturn(
        new CommentActivityDto(comment.getId(), article1.getId(), article1.getTitle(), user.getId(), user.getNickname(),
            comment.getContent(), 0L, comment.getCreatedAt()));

    // 댓글 좋아요
    Like like = new Like(comment, user);
    given(likeRepository.findTop10ByUserIdOrderByCreatedAtDesc(user.getId())).willReturn(
        List.of(like));
    given(commentMapper.toActivityDto(like)).willReturn(
        new CommentLikeActivityDto(like.getId(), like.getCreatedAt(), comment.getId(),
            article1.getId(), article1.getTitle(), comment.getUser().getId(), comment.getUser().getNickname(),
            comment.getContent(), 1L, comment.getCreatedAt()));

    // when
    UserActivityDto result = userActivityService.getUserActivity(user.getId());

    // then
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(user.getId());
    assertThat(result.email()).isEqualTo("test@test.kr");
    assertThat(result.nickname()).isEqualTo("test");
    assertThat(result.createdAt()).isEqualTo(user.getCreatedAt());
    assertThat(result.subscriptions()).hasSize(1);
    assertThat(result.comments()).hasSize(1);
    assertThat(result.commentLikes()).hasSize(1);
    assertThat(result.articleViews()).hasSize(2);
  }

  @Test
  void 사용자_활동_내역_조회_실패_존재하지_않는_사용자() {
    // given
    UUID notExistUserId = UUID.randomUUID();
    given(userRepository.findById(notExistUserId)).willReturn(Optional.empty());

    // when and then
    assertThrows(UserNotFoundException.class,
        () -> userActivityService.getUserActivity(notExistUserId));
  }
}
