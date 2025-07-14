package com.sprint5team.monew.repository.notification;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
class NotificationRepositoryTest {

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
    void 알림_저장시_DB에_정상_반영() {
        // given
        Notification notification = Notification.builder()
                .user(testUser)
                .content("새로운 알림입니다.")
                .resourceType(ResourceType.INTEREST)
                .confirmed(false)
                .createdAt(Instant.now())
                .build();

        // when
        notificationRepository.save(notification);
        // then
        assertNotNull(notification.getId());
    }

    @Test
    void 알림_저장_실패() {
        // given
        Notification notification = Notification.builder()
                .user(null)
                .content("알림입니다.")
                .resourceType(ResourceType.COMMENT)
                .confirmed(false)
                .createdAt(Instant.now())
                .build();

        // when & then
        assertThrows(DataIntegrityViolationException.class, () -> {
            notificationRepository.saveAndFlush(notification);
        });
    }

    @Test
    void 알림목록_커서_조회() {
        // when
        List<Notification> results = notificationRepository
                .findAllByUserIdAndConfirmedIsFalseWithCursorPaging(
                        testUser.getId(), null, null, 3);

        // then
        assertThat(results).isNotNull();
        assertThat(results.get(0).getContent()).contains("테스트 알림");
    }

}