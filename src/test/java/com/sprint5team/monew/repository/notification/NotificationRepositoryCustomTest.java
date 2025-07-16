package com.sprint5team.monew.repository.notification;

import com.sprint5team.monew.base.config.JpaAuditingConfig;
import com.sprint5team.monew.base.config.QuerydslConfig;
import com.sprint5team.monew.domain.notification.entity.Notification;
import com.sprint5team.monew.domain.notification.entity.ResourceType;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({QuerydslConfig.class, JpaAuditingConfig.class})
class NotificationRepositoryCustomTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@abc.com", "password");
        userRepository.save(testUser);

        for (int i = 1; i <= 5; i++) {
            Notification notification = Notification.builder()
                    .user(testUser)
                    .content("테스트 알림 " + i)
                    .confirmed(false)
                    .resourceType(ResourceType.INTEREST)
                    .interest(null)
                    .createdAt(Instant.now().minusSeconds(i * 60))
                    .build();
            notificationRepository.save(notification);
        }

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void 알림목록_커서_조회() {
        // when
        List<Notification> results = notificationRepository
                .findUnconfirmedNotificationsWithCursorPaging(
                        testUser.getId(), null, null, 3);

        // then
        assertThat(results).isNotNull();
        assertThat(results.get(0).getContent()).contains("테스트 알림");
    }

    @Test
    void 알림목록_커서_및_after_기준_조회() {
        // given
        List<Notification> all = notificationRepository
                .findUnconfirmedNotificationsWithCursorPaging(testUser.getId(), null, null, 5);

        Notification cursorBase = all.get(2);
        String cursor = cursorBase.getCreatedAt().toString(); ;
        Instant after = cursorBase.getCreatedAt();

        // when
        List<Notification> results = notificationRepository
                .findUnconfirmedNotificationsWithCursorPaging(
                        testUser.getId(), cursor, after, 3);

        // then
        assertThat(results).allSatisfy(n -> {
            assertThat(n.getCreatedAt()).isAfterOrEqualTo(after);
        });
    }

    @Test
    void 모두_확인된_알림이면_커서조회_결과없음() {
        // given
        List<Notification> notifications = notificationRepository.findAll();
        for (Notification n : notifications) {
            ReflectionTestUtils.setField(n, "confirmed", true);
        }

        notificationRepository.saveAll(notifications);
        entityManager.flush();
        entityManager.clear();

        // when
        List<Notification> results = notificationRepository
                .findUnconfirmedNotificationsWithCursorPaging(testUser.getId(), null, null, 5);

        // then
        assertThat(results).isEmpty();
    }

    @Test
    void 알림이_없는_사용자는_빈_결과를_반환한다() {
        // given
        User nonNotification = userRepository.save(
                new User("emptyuser", "empty@abc.com", "password")
        );

        // when
        List<Notification> results = notificationRepository
                .findUnconfirmedNotificationsWithCursorPaging(
                        nonNotification.getId(), null, null, 10);

        // then
        assertThat(results).isEmpty();
    }

}