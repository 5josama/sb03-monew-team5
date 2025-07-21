package com.sprint5team.monew.domain.notification.service;

import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;

    /**
     * 매일 새벽 2시에 확인된 알림 중 1주일이 지난 알림 삭제
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void deleteConfirmedNotifications() {
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        log.info("1주일이 지난 확인된 알림을 삭제합니다.");
        notificationRepository.deleteByConfirmedIsTrueAndCreatedAtBefore(sevenDaysAgo);
    }

}