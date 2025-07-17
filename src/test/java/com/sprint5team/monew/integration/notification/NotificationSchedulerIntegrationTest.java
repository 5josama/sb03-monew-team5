package com.sprint5team.monew.integration.notification;

import com.sprint5team.monew.domain.notification.entity.Notification;
import com.sprint5team.monew.domain.notification.entity.ResourceType;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import com.sprint5team.monew.domain.notification.service.NotificationScheduler;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationSchedulerIntegrationTest {

    @Autowired private NotificationRepository notificationRepository;
    @Autowired private NotificationScheduler notificationScheduler;
    @Autowired private UserRepository userRepository;

    @Test
    void 확인된_알림이_1주일_경과시_스케줄러_자동으로_삭제된다() {
        // given
        User user = userRepository.save(new User("testuser", "test@abc.com", "1234"));

        Notification notification = Notification.builder()
                .user(user)
                .content("알림 입니다.")
                .confirmed(true)
                .resourceType(ResourceType.COMMENT)
                .createdAt(Instant.now().minus(8, ChronoUnit.DAYS))
                .build();

        notificationRepository.save(notification);

        // when
        notificationScheduler.deleteConfirmedNotifications();

        // then
        List<Notification> remaining = notificationRepository.findAll();
        assertThat(remaining).isEmpty();
    }
}