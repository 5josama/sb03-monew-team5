package com.sprint5team.monew.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.entity.ArticleCount;
import com.sprint5team.monew.domain.article.mapper.ArticleViewMapper;
import com.sprint5team.monew.domain.article.repository.ArticleCountRepository;
import com.sprint5team.monew.domain.comment.dto.CommentDto;
import com.sprint5team.monew.domain.comment.dto.CommentLikeDto;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.entity.Like;
import com.sprint5team.monew.domain.comment.mapper.CommentMapper;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.comment.repository.LikeRepository;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.user.dto.UserActivityDto;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.user.service.UserActivityServiceImpl;
import com.sprint5team.monew.domain.user.service.UserServiceImpl;
import com.sprint5team.monew.domain.user_interest.dto.SubscriptionDto;
import com.sprint5team.monew.domain.user_interest.entity.UserInterest;
import com.sprint5team.monew.domain.user_interest.mapper.UserInterestMapper;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserActivityServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private UserInterestRepository userInterestRepository;
  @Mock private UserInterestMapper userInterestMapper;
  @Mock private CommentRepository commentRepository;
  @Mock private LikeRepository likeRepository;
  @Mock private CommentMapper commentMapper;
  @Mock private ArticleCountRepository articleCountRepository;
  @Mock private ArticleViewMapper articleViewMapper;

  @InjectMocks
  private UserActivityServiceImpl userActivityService;

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  void 사용자_활동_내역_조회_성공() {
    // given
    UUID userId = UUID.randomUUID();
    User user = User.builder()
        .email("test@test.kr")
        .nickname("test")
        .password("test1234")
        .build();
    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    // 관심사
    Interest interest = new Interest(Instant.now(), "test", 1L);
    Keyword keyword1 = new Keyword(Instant.now(), "test1", interest);
    Keyword keyword2 = new Keyword(Instant.now(), "test2", interest);
    UserInterest userInterest = new UserInterest(Instant.now(), user, interest);
    given(userInterestRepository.findByUserId(userId)).willReturn(Set.of(userInterest));

    List<String> interestKeywords = List.of(keyword1.getName(), keyword2.getName());
    given(userInterestMapper.toDto(userInterest)).willReturn(new SubscriptionDto(userInterest.getId(), interest.getId(), interest.getName(), interestKeywords, 2L, Instant.now()));

    // 기사
    Article article1 = new Article("Naver", "http://naver.com/testURL", "테스트 기사", "테스트 기사 내용 요약", Instant.now());
    Article article2 = new Article("Naver", "http://naver.com/testURL2", "테스트 기사2", "테스트 기사 내용 요약2", Instant.now());

    ArticleCount articleCount1 = new ArticleCount(article1, user);
    ArticleCount articleCount2 = new ArticleCount(article2, user);

    given(articleCountRepository.findAllByUserId(userId)).willReturn(List.of(articleCount1, articleCount2));
    given(articleViewMapper.toDto(article1, user, articleCount1)).willReturn(new ArticleViewDto(articleCount1.getId(), user.getId(), articleCount1.getCreatedAt(), article1.getId(), article1.getSource(), article1.getSourceUrl(), article1.getTitle(), article1.getOriginalDateTime(), article1.getSummary(), 1L, 1L));
    given(articleViewMapper.toDto(article2, user, articleCount2)).willReturn(new ArticleViewDto(articleCount2.getId(), user.getId(), articleCount2.getCreatedAt(), article2.getId(), article2.getSource(), article2.getSourceUrl(), article2.getTitle(), article2.getOriginalDateTime(), article2.getSummary(), 1L, 1L));

    // 댓글
    Comment comment = new Comment(article1, user, "content");
    given(commentRepository.findByUserId(userId)).willReturn(List.of(comment));
    given(commentMapper.toDto(comment)).willReturn(new CommentDto(comment.getId(), article1.getId(), user.getId(), user.getNickname(), comment.getContent(), 0L, false, comment.getCreatedAt()));

    // 댓글 좋아요
    Like like = new Like(comment, user);
    given(likeRepository.findByUserId(userId)).willReturn(List.of(like));
    given(commentMapper.toDto(like)).willReturn(new CommentLikeDto(like.getId(), user.getId(), like.getCreatedAt(), comment.getId(), article1.getId(), comment.getUser().getId(), comment.getUser().getNickname(), comment.getContent(), 1L, comment.getCreatedAt()));

    // when
    UserActivityDto result = userActivityService.getUserActivity(userId);

    // then
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(userId);
    assertThat(result.email()).isEqualTo("test@test.kr");
    assertThat(result.nickname()).isEqualTo("test");
    assertThat(result.createdAt()).isEqualTo(user.getCreatedAt());
    assertThat(result.subscriptions()).hasSize(1);
    assertThat(result.comments()).hasSize(1);
    assertThat(result.commentLikes()).hasSize(1);
    assertThat(result.articleViews()).hasSize(2);
  }
}
