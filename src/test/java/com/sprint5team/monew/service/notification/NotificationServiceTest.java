package com.sprint5team.monew.service.notification;


import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.notification.dto.CursorPageResponseNotificationDto;
import com.sprint5team.monew.domain.notification.dto.NotificationDto;
import com.sprint5team.monew.domain.notification.entity.Notification;
import com.sprint5team.monew.domain.notification.entity.ResourceType;
import com.sprint5team.monew.domain.notification.mapper.NotificationMapper;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import com.sprint5team.monew.domain.notification.service.NotificationServiceImpl;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private NotificationMapper notificationMapper;

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
    @Transactional
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
        given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
        given(interestRepository.findById(interestId)).willReturn(Optional.of(interest));

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
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
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