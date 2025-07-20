package com.sprint5team.monew.domain.notification.service;

import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;
    private final InterestRepository interestRepository;
    private final ArticleRepository articleRepository;
    private final UserInterestRepository userInterestRepository;
    private final NotificationService notificationService;

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

    /**
     * 매시 정각에 관심사별 기사 알림 생성
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void notifyNewArticles() {
        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        List<Interest> interests = interestRepository.findAll();

        for (Interest interest : interests) {
            long count = articleRepository.countRecentArticlesByInterestId(interest.getId(), oneHourAgo);
            if (count == 0) continue;

            List<User> users = userInterestRepository.findUsersByInterestId(interest.getId());
            if (users.isEmpty()) continue;

            for (User user : users) {
                boolean alreadyNotified = notificationRepository.existsByUserIdAndInterestIdAndCreatedAtAfter(
                        user.getId(), interest.getId(), oneHourAgo
                );
                if (alreadyNotified) continue;

                notificationService.notifyArticleForInterest(
                        user.getId(), interest.getId(), interest.getName(), count
                );
            }
        }

        log.info("알림 생성 완료");
    }
}
