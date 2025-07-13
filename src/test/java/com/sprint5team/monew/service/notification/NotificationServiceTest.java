package com.sprint5team.monew.service.notification;


import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.notification.dto.NotificationDto;
import com.sprint5team.monew.domain.notification.entity.Notification;
import com.sprint5team.monew.domain.notification.entity.ResourceType;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import com.sprint5team.monew.domain.notification.service.NotificationServiceImpl;
import com.sprint5team.monew.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private UUID userId;
    private UUID commentId;
    private UUID interestId;
    private User testUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        commentId = UUID.randomUUID();
        interestId = UUID.randomUUID();

        testUser = new User("testUser", "test@abc.com", "1234");
        ReflectionTestUtils.setField(testUser, "id", userId);
    }

    @Test
    void 구독_중인_관심사와_관련된_기사가_등록되면_알림이_생성된다() {
        // given
        String interestName = "축구";
        int articleCount = 5;
        Interest interest = Interest.builder()
                .name(interestName)
                .subscriberCount(0)
                .build();

        ReflectionTestUtils.setField(interest, "id", interestId);

        Notification notification = Notification.builder()
                .user(testUser)
                .content("[축구]와 관련된 기사가 5건 등록되었습니다.")
                .resourceType(ResourceType.INTEREST)
                .interest(interest)
                .createdAt(Instant.now())
                .confirmed(false)
                .build();

        given(notificationRepository.save(any(Notification.class))).willReturn(notification);

        // when
        NotificationDto result = notificationService.notifyArticleForInterest(userId, interestId, interestName, articleCount);

        // then
        then(notificationRepository).should().save(any(Notification.class));

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(ResourceType.INTEREST, result.resourceType());
        assertEquals(interestId, result.resourceId());
    }

    @Test
    void 내_댓글에_좋아요가_눌리면_알림이_생성된다() {
        // given
        String likerName = "홍길동";

        Article article = mock(Article.class);
        UUID articleId = UUID.randomUUID();
        ReflectionTestUtils.setField(article, "id", articleId);

        Comment comment = new Comment(article, testUser, "댓글입니다");
        ReflectionTestUtils.setField(comment, "id", commentId);

        Notification notification = Notification.builder()
                .user(testUser)
                .comment(comment)
                .resourceType(ResourceType.COMMENT)
                .content("홍길동님이 내 댓글을 좋아했습니다.")
                .createdAt(Instant.now())
                .confirmed(false)
                .build();
        ReflectionTestUtils.setField(notification, "id", UUID.randomUUID());

        // repository mocking
        given(notificationRepository.save(any(Notification.class))).willReturn(notification);

        // when
        NotificationDto result = notificationService.notifyCommentLiked(userId, commentId, likerName);

        // then
        then(notificationRepository).should().save(any(Notification.class));

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(ResourceType.COMMENT, result.resourceType());
        assertEquals(commentId, result.resourceId());
    }
}