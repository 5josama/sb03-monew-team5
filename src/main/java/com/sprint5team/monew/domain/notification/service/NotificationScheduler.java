package com.sprint5team.monew.domain.notification.service;

import com.google.common.annotations.VisibleForTesting;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * 알림 관련 배치 작업을 담당하는 스케줄러 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;

    /**
     * 매일 새벽 2시에 실행되어, 확인된 알림 중 7일 이상 지난 알림을 모두 삭제한다
     * 대상: confirmed = true && updatedAt > 7일
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void deleteConfirmedNotifications() {
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        log.info("1주일이 지난 확인된 알림을 삭제합니다.");
        notificationRepository.deleteByConfirmedIsTrueAndUpdatedAtBefore(sevenDaysAgo);
    }

    @VisibleForTesting
    public void deleteConfirmedNotifications(Instant cutoffTime) {
        notificationRepository.deleteByConfirmedIsTrueAndUpdatedAtBefore(cutoffTime);
    }
}