package com.sprint5team.monew.repository.notification;

import com.sprint5team.monew.domain.notification.entity.Notification;
import com.sprint5team.monew.domain.notification.entity.ResourceType;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import com.sprint5team.monew.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@abc.com", "password");
        entityManager.persist(testUser);
    }

    @Test
    @DisplayName("알림 저장시 DB에 정상 반영되어야 한다")
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
}