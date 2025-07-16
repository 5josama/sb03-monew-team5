package com.sprint5team.monew.controller.user;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.comment.dto.CommentDto;
import com.sprint5team.monew.domain.comment.dto.CommentLikeDto;
import com.sprint5team.monew.domain.user.controller.UserActivityController;
import com.sprint5team.monew.domain.user.dto.UserActivityDto;
import com.sprint5team.monew.domain.user.service.UserActivityServiceImpl;
import com.sprint5team.monew.domain.user_interest.dto.SubscriptionDto;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserActivityController.class)
public class UserActivityControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserActivityServiceImpl userActivityService;

  @Test
  void 사용자_활동_조회_성공() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    UUID articleViewId = UUID.randomUUID();
    UUID articleId = UUID.randomUUID();
    UUID interestId = UUID.randomUUID();
    UUID commentId = UUID.randomUUID();
    UUID commentLikeId = UUID.randomUUID();
    UUID commentUserId = UUID.randomUUID();

    List<String> keywords = new ArrayList<>();
    keywords.add("축구");
    keywords.add("야구");
    keywords.add("농구");

    SubscriptionDto subscriptionDto = new SubscriptionDto(userId, interestId,"interest", keywords, 1L, Instant.now());
    CommentDto commentDto = new CommentDto(commentId, articleId, userId, "test", "댓글 내용 입니다.",0L, false, Instant.now());
    CommentLikeDto commentLikeDto = new CommentLikeDto(commentLikeId, userId, Instant.now(), commentId, articleId, commentUserId, "commentUser", "내가 좋아하는 댓글 내용", 1L, Instant.now());
    ArticleViewDto articleViewDto = new ArticleViewDto(articleViewId, userId, Instant.now(), articleId, "NAVER", "https://naver.com/news/12333", "test", Instant.now(), "요약", 10, 10);

    List<SubscriptionDto> subscriptions = new ArrayList<>();
    subscriptions.add(subscriptionDto);

    List<CommentDto> comments = new ArrayList<>();
    comments.add(commentDto);

    List<CommentLikeDto> commentLikes = new ArrayList<>();
    commentLikes.add(commentLikeDto);

    List<ArticleViewDto> articleViews = new ArrayList<>();
    articleViews.add(articleViewDto);

    given(userActivityService.getUserActivity(userId)).willReturn(new UserActivityDto(userId, "test@test.kr", "test", Instant.now(), subscriptions, comments, commentLikes, articleViews));

    // when and then
    mockMvc.perform(get("/api/user-activities/{userId}", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(userId.toString()))
        .andExpect(jsonPath("$.email").value("test@test.kr"))
        .andExpect(jsonPath("$.nickname").value("test"))
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.subscriptions").isArray())
        .andExpect(jsonPath("$.subscriptions[0].userId").value(userId.toString()))
        .andExpect(jsonPath("$.subscriptions[0].interestId").value(interestId.toString()))
        .andExpect(jsonPath("$.subscriptions[0].interestType").value("interest"))
        .andExpect(jsonPath("$.subscriptions[0].keywords[0]").value("축구"))
        .andExpect(jsonPath("$.subscriptions[0].keywords[1]").value("야구"))
        .andExpect(jsonPath("$.subscriptions[0].keywords[2]").value("농구"))
        .andExpect(jsonPath("$.subscriptions[0].count").value(1L))
        .andExpect(jsonPath("$.subscriptions[0].createdAt").exists())
        .andExpect(jsonPath("$.comments").isArray())
        .andExpect(jsonPath("$.comments[0].id").value(commentId.toString()))
        .andExpect(jsonPath("$.comments[0].articleId").value(articleId.toString()))
        .andExpect(jsonPath("$.comments[0].userId").value(userId.toString()))
        .andExpect(jsonPath("$.comments[0].userNickname").value("test"))
        .andExpect(jsonPath("$.comments[0].content").value("댓글 내용 입니다."))
        .andExpect(jsonPath("$.comments[0].likeCount").value(0L))
        .andExpect(jsonPath("$.comments[0].likedByMe").value(false))
        .andExpect(jsonPath("$.comments[0].createdAt").exists())
        .andExpect(jsonPath("$.commentLikes").isArray())
        .andExpect(jsonPath("$.commentLikes[0].id").value(commentLikeId.toString()))
        .andExpect(jsonPath("$.commentLikes[0].userId").value(userId.toString()))
        .andExpect(jsonPath("$.commentLikes[0].createdAt").exists())
        .andExpect(jsonPath("$.commentLikes[0].articleId").value(articleId.toString()))
        .andExpect(jsonPath("$.commentLikes[0].commentId").value(commentId.toString()))
        .andExpect(jsonPath("$.commentLikes[0].commentUserId").value(commentUserId.toString()))
        .andExpect(jsonPath("$.commentLikes[0].commentUserNickname").value("commentUser"))
        .andExpect(jsonPath("$.commentLikes[0].commentUserContent").value("내가 좋아하는 댓글 내용"))
        .andExpect(jsonPath("$.commentLikes[0].likeCount").value(1L))
        .andExpect(jsonPath("$.commentLikes[0].likedByMe").value(true))
        .andExpect(jsonPath("$.articleViews").isArray())
        .andExpect(jsonPath("$.articleViews[0].id").value(articleViewId.toString()))
        .andExpect(jsonPath("$.articleViews[0].userId").value(userId.toString()))
        .andExpect(jsonPath("$.articleViews[0].createdAt").exists())
        .andExpect(jsonPath("$.articleViews[0].articleId").value(articleId.toString()))
        .andExpect(jsonPath("$.articleViews[0].source").value("NAVER"))
        .andExpect(jsonPath("$.articleViews[0].link").value("https://naver.com/news/12333"))
        .andExpect(jsonPath("$.articleViews[0].title").value("test"))
        .andExpect(jsonPath("$.articleViews[0].createdAt").exists())
        .andExpect(jsonPath("$.articleViews[0].summary").value("요약"))
        .andExpect(jsonPath("$.articleViews[0].likeCount").value(10))
        .andExpect(jsonPath("$.articleViews[0].viewCount").value(10));
  }
}
