package com.sprint5team.monew.service.notification;


import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.notification.dto.CursorPageResponseNotificationDto;
import com.sprint5team.monew.domain.notification.dto.NotificationDto;
import com.sprint5team.monew.domain.notification.entity.Notification;
import com.sprint5team.monew.domain.notification.entity.ResourceType;
import com.sprint5team.monew.domain.notification.exception.InvalidRequestParameterException;
import com.sprint5team.monew.domain.notification.mapper.NotificationMapper;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import com.sprint5team.monew.domain.notification.service.NotificationServiceImpl;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.exception.UserNotFoundException;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserInterestRepository userInterestRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private NotificationMapper notificationMapper;

    private UUID userId;
    private UUID commentId;
    private UUID interestId;
    private User testUser;
    private Interest testInterest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        commentId = UUID.randomUUID();
        interestId = UUID.randomUUID();

        testUser = new User("testUser", "test@abc.com", "1234");
        ReflectionTestUtils.setField(testUser, "id", userId);

        testInterest = Interest.builder()
                .name("스포츠")
                .subscriberCount(1)
                .build();
        ReflectionTestUtils.setField(testInterest, "id", interestId);
    }

    private Notification createTestNotification(String content, ResourceType type) {
        return Notification.builder()
                .user(testUser)
                .interest(testInterest)
                .content(content)
                .resourceType(type)
                .confirmed(false)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void 구독_중인_관심사와_관련된_기사가_등록되면_알림이_생성된다() {
        // given
        String interestName = "축구";
        int articleCount = 5;

        Notification notification = createTestNotification(
                "[축구]와 관련된 기사가 5건 등록되었습니다.",
                ResourceType.INTEREST
        );

        given(notificationRepository.save(any(Notification.class))).willReturn(notification);
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(interestRepository.findById(interestId)).willReturn(Optional.of(testInterest));

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
    void 관심사별_기사_등록시_알림이_생성된다() {
        // given
        Notification notification = createTestNotification(
                "[스포츠]와 관련된 기사가 5건 등록되었습니다.",
                ResourceType.INTEREST
        );

        given(interestRepository.findAll()).willReturn(List.of(testInterest));
        given(articleRepository.countRecentArticlesByInterestId(eq(interestId), any())).willReturn(3L);
        given(userInterestRepository.findUsersByInterestId(interestId)).willReturn(List.of(testUser));
        given(notificationRepository.existsByUserIdAndInterestIdAndCreatedAtAfter(any(), any(), any())).willReturn(false);
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(interestRepository.findById(interestId)).willReturn(Optional.of(testInterest));
        given(notificationRepository.save(any())).willReturn(notification);

        // when
        notificationService.notifyNewArticles();

        // then
        then(notificationRepository).should(times(1)).save(any());
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
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when
        NotificationDto result = notificationService.notifyCommentLiked(userId, commentId, likerName);

        // then
        then(notificationRepository).should().save(any(Notification.class));

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(ResourceType.COMMENT, result.resourceType());
        assertEquals(commentId, result.resourceId());
    }

    @Test
    void 알림_목록_커서_기반_조회() {
        // given
        Instant createdAt = Instant.now();
        String cursor = createdAt.toString();
        Instant after = createdAt;
        int limit = 10;

        Notification notification = Notification.builder()
                .user(testUser)
                .content("알림 내용입니다")
                .resourceType(ResourceType.INTEREST)
                .interest(mock(Interest.class))
                .createdAt(createdAt)
                .confirmed(false)
                .build();
        ReflectionTestUtils.setField(notification, "id", UUID.randomUUID());

        NotificationDto dto = NotificationDto.builder()
                .id(notification.getId())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getCreatedAt())
                .confirmed(notification.isConfirmed())
                .userId(testUser.getId())
                .content(notification.getContent())
                .resourceType(ResourceType.INTEREST)
                .resourceId(interestId)
                .build();

        given(notificationMapper.toDto(notification)).willReturn(dto);

        List<Notification> notificationList = List.of(notification);

        given(userRepository.existsById(userId)).willReturn(true);
        given(notificationRepository.findUnconfirmedNotificationsWithCursorPaging(
                eq(userId), eq(cursor), eq(after), eq(limit))).willReturn(notificationList);
        given(notificationRepository.countByUserIdAndConfirmedIsFalse(userId)).willReturn(1L);

        // when
        CursorPageResponseNotificationDto result = notificationService.getAllNotifications(userId, cursor, after, limit);

        // then
        then(notificationRepository).should()
                .findUnconfirmedNotificationsWithCursorPaging(userId, cursor, after, limit);
        then(notificationRepository).should().countByUserIdAndConfirmedIsFalse(userId);

        assertEquals(1, result.content().size());
        assertEquals(userId, result.content().get(0).userId());
        assertEquals(ResourceType.INTEREST, result.content().get(0).resourceType());
        assertEquals(interestId, result.content().get(0).resourceId());
    }

    @Test
    void userId가_null인_알림목록조회_요청시_예외발생() {
        // given
        UUID nullUserId = null;

        // when, then
        assertThrows(InvalidRequestParameterException.class, () -> {
            notificationService.getAllNotifications(nullUserId, null, null, 10);
        });
    }

    @Test
    void 존재하지_않는_사용자가_알림목록조회_요청시_예외발생() {
        // given
        given(userRepository.existsById(userId)).willReturn(false);

        // when, then
        assertThrows(UserNotFoundException.class, () -> {
            notificationService.getAllNotifications(userId, null, null, 10);
        });
    }


    @Test
    void 단일_알림_확인_요청시_알림이_확인된다() {
        // given
        UUID notificationId = UUID.randomUUID();
        Notification notification = Notification.builder()
                .user(testUser)
                .confirmed(false)
                .content("테스트 알림")
                .resourceType(ResourceType.COMMENT)
                .build();
        ReflectionTestUtils.setField(notification, "id", UUID.randomUUID());

        given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));

        // when
        notificationService.confirmNotification(notificationId, userId);

        // then
        assertThat(notification.isConfirmed()).isTrue();
        verify(notificationRepository).findById(notificationId);
    }

    @Test
    void 전체_알림_확인_요청시_모든_알림이_확인된다() {
        // given
        Notification noti1 = Notification.builder()
                .user(testUser)
                .confirmed(false)
                .content("알림1")
                .resourceType(ResourceType.INTEREST)
                .build();
        ReflectionTestUtils.setField(noti1, "id", UUID.randomUUID());

        Notification noti2 = Notification.builder()
                .user(testUser)
                .confirmed(false)
                .content("알림2")
                .resourceType(ResourceType.COMMENT)
                .build();
        ReflectionTestUtils.setField(noti2, "id", UUID.randomUUID());

        List<Notification> notifications = List.of(noti1, noti2);

        given(notificationRepository.findByUserIdAndConfirmedIsFalse(userId)).willReturn(notifications);

        // when
        notificationService.confirmAllNotifications(userId);

        // then
        for (Notification notification : notifications) {
            assertThat(notification.isConfirmed()).isTrue();
        }

        verify(notificationRepository).findByUserIdAndConfirmedIsFalse(userId);
    }
}