package com.sprint5team.monew.integration.notification;

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
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import com.sprint5team.monew.domain.notification.service.NotificationService;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationIntegrationTest {

    @Autowired private NotificationService notificationService;
    @Autowired private UserRepository userRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private ArticleRepository articleRepository;
    @Autowired private InterestRepository interestRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private EntityManager entityManager;

    private User user;
    private Interest interest;
    private Comment comment;
    private Article article;
    private Notification notification3;

    @BeforeEach
    void setup() {
        user = userRepository.save(new User("user1", "test@abc.com", "1234"));
        interest = interestRepository.save(Interest.builder().name("경제").subscriberCount(0).build());
        Interest interest2 = interestRepository.save(Interest.builder().name("IT").subscriberCount(0).build());
        Interest interest3 = interestRepository.save(Interest.builder().name("스포츠").subscriberCount(0).build());

        article = articleRepository.save(new Article(
                "NAVER",
                "https://naver.com/news/123331",
                "title",
                "요약",
                Instant.now()
        ));
        comment = commentRepository.save(new Comment(article, user, "테스트 댓글"));

        notificationService.notifyArticleForInterest(user.getId(), interest.getId(), interest.getName(), 5);

        NotificationDto notificationDto2 = notificationService.notifyArticleForInterest(
                user.getId(), interest2.getId(), interest2.getName(), 2);

        Notification notification2 = notificationRepository.findById(notificationDto2.id()).orElseThrow();
        Instant modifiedCreatedAt2 = Instant.now().plusSeconds(120);
        ReflectionTestUtils.setField(notification2, "createdAt", modifiedCreatedAt2);
        notificationRepository.save(notification2);
        entityManager.flush();
        entityManager.clear();

        NotificationDto notificationDto3 = notificationService.notifyArticleForInterest(
                user.getId(), interest3.getId(), interest3.getName(), 9);

        notification3 = notificationRepository.findById(notificationDto3.id()).orElseThrow();
        Instant modifiedCreatedAt = Instant.now().plusSeconds(300);
        ReflectionTestUtils.setField(notification3, "createdAt", modifiedCreatedAt);
        notificationRepository.save(notification3);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void 관심사_기사등록_알림_생성_통합테스트() {
        // when
        NotificationDto result = notificationService.notifyArticleForInterest(user.getId(), interest.getId(), interest.getName(), 3);

        // then
        assertNotNull(result);
        assertEquals(user.getId(), result.userId());
        assertEquals(ResourceType.INTEREST, result.resourceType());
    }

    @Test
    void 댓글좋아요_알림_통합테스트() {
        // when
        NotificationDto result = notificationService.notifyCommentLiked(user.getId(), comment.getId(), "홍길동");

        // then
        assertNotNull(result);
        assertEquals(comment.getId(), result.resourceId());
        assertEquals(ResourceType.COMMENT, result.resourceType());
    }

    @Test
    void 알림목록_커서기반_페이지네이션_조회_통합테스트() {
        // when
        CursorPageResponseNotificationDto response1 =
                notificationService.getAllNotifications(user.getId(), null, null, 2);

        // then
        assertThat(response1.content()).hasSize(2);
        assertThat(response1.hasNext()).isTrue();
        assertThat(response1.nextCursor()).isNotNull();
        assertThat(response1.nextAfter()).isNotNull();
        assertThat(response1.totalElements()).isEqualTo(3);
        assertThat(response1.size()).isEqualTo(2);

        Instant lastCreatedAt = response1.content().get(1).createdAt();
        assertThat(response1.nextCursor()).isEqualTo(lastCreatedAt.toString());
        assertThat(response1.nextAfter()).isEqualTo(lastCreatedAt);

        // when
        Instant after = response1.nextAfter();
        CursorPageResponseNotificationDto response2 =
                notificationService.getAllNotifications(
                        user.getId(), response1.nextCursor(), after, 2);

        // then
        assertThat(response2.content()).hasSize(1);
        assertThat(response2.hasNext()).isFalse();
        assertThat(response2.totalElements()).isEqualTo(3);
        assertThat(response2.size()).isEqualTo(2);
        assertThat(response2.nextCursor()).isNull();
        assertThat(response2.nextAfter()).isNull();
    }

    @Test
    void 단일_알림_수정_통합테스트() {
        // given
        List<NotificationDto> notifications = notificationService
                .getAllNotifications(user.getId(), null, null, 10)
                .content();

        UUID notificationId = notifications.get(0).id();

        // when
        notificationService.confirmNotification(notificationId, user.getId());

        // then
        Notification updated = notificationRepository.findById(notificationId).orElseThrow();
        assertThat(updated.isConfirmed()).isTrue();

        List<Notification> remaining = notificationRepository.findByUserIdAndConfirmedIsFalse(user.getId());
        assertThat(remaining).hasSize(2);
        assertThat(remaining).noneMatch(n -> n.getId().equals(notificationId));
    }

    @Test
    void 전체_알림_수정_통합테스트() {
        // when
        notificationService.confirmAllNotifications(user.getId());

        // then
        List<Notification> afterConfirm = notificationRepository.findByUserIdAndConfirmedIsFalse(user.getId());
        assertThat(afterConfirm).isEmpty();

        List<Notification> all = notificationRepository.findAll();
        assertThat(all).allMatch(Notification::isConfirmed);
    }

}