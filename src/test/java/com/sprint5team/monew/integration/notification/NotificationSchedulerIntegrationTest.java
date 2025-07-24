package com.sprint5team.monew.integration.notification;

import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.notification.batch.NotificationDeleteScheduler;
import com.sprint5team.monew.domain.notification.batch.NotificationDeleteTasklet;
import com.sprint5team.monew.domain.notification.dto.NotificationDto;
import com.sprint5team.monew.domain.notification.entity.Notification;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import com.sprint5team.monew.domain.notification.service.NotificationService;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationSchedulerIntegrationTest {

    @Autowired private NotificationRepository notificationRepository;
    @Autowired private InterestRepository interestRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private UserRepository userRepository;
    @Autowired private NotificationDeleteTasklet notificationDeleteTasklet;
    @Autowired private NotificationDeleteScheduler scheduler;
    @Autowired private Job deleteOldNotificationsJob;

    private Interest interest;

    @Test
    void 확인된_알림이_1주일_경과시_스케줄러_자동으로_삭제된다() throws Exception {
        // given
        User user = userRepository.save(new User("test@abc.com", "testuser", "1234"));
        interest = interestRepository.save(Interest.builder().name("경제").subscriberCount(0).build());

        NotificationDto notificationDto = notificationService.notifyArticleForInterest(
                user.getId(), interest.getId(), interest.getName(), 9);

        Notification notification = notificationRepository.findById(notificationDto.id()).orElseThrow();

        ReflectionTestUtils.setField(notification, "confirmed", true);
        notificationRepository.save(notification);

        // when
        notificationDeleteTasklet.deleteWithCutoffTime(Instant.now().plus(7, ChronoUnit.DAYS));

        // then
        List<Notification> remaining = notificationRepository.findAll();
        assertThat(remaining).isEmpty();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void 스케줄러_Job_직접_실행_성공() throws Exception {
        // given (스케줄러 빈이 정상 주입되어 있다고 가정)

        // when
        scheduler.runDeleteOldNotificationsJob();

        // then
        assertThatCode(() -> scheduler.runDeleteOldNotificationsJob())
                .doesNotThrowAnyException();
    }

    @Test
    void Batch_Job_정상등록된다() {
        // given
        String expectedJobName = "deleteOldNotificationsJob";

        // when
        String actualJobName = deleteOldNotificationsJob.getName();

        // then
        assertThat(actualJobName).isEqualTo(expectedJobName);
    }
}