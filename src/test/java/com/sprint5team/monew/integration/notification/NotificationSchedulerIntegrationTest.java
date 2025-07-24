package com.sprint5team.monew.integration.notification;

import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.notification.dto.NotificationDto;
import com.sprint5team.monew.domain.notification.entity.Notification;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import com.sprint5team.monew.domain.notification.service.NotificationScheduler;
import com.sprint5team.monew.domain.notification.service.NotificationService;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
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
    @Autowired private InterestRepository interestRepository;
    @Autowired private NotificationScheduler notificationScheduler;
    @Autowired private NotificationService notificationService;
    @Autowired private UserRepository userRepository;

    private Interest interest;

    @Test
    void 확인된_알림이_1주일_경과시_스케줄러_자동으로_삭제된다() {
        // given
        User user = userRepository.save(new User("testuser", "test@abc.com", "1234"));
        interest = interestRepository.save(Interest.builder().name("경제").subscriberCount(0).build());

        NotificationDto notificationDto = notificationService.notifyArticleForInterest(
                user.getId(), interest.getId(), interest.getName(), 9);

        Notification notification = notificationRepository.findById(notificationDto.id()).orElseThrow();
        Instant modifiedCreatedAt = Instant.now().minus(12,ChronoUnit.DAYS);

        ReflectionTestUtils.setField(notification, "createdAt", modifiedCreatedAt);
        ReflectionTestUtils.setField(notification, "confirmed", true);
        notificationRepository.save(notification);

        // when
        notificationScheduler.deleteConfirmedNotifications();

        // then
        List<Notification> remaining = notificationRepository.findAll();
        assertThat(remaining).isEmpty();
    }
}