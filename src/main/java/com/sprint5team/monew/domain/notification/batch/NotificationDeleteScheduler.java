package com.sprint5team.monew.domain.notification.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 알림 관련 배치 잡을 실행하는 스케줄러 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationDeleteScheduler {

    private final JobLauncher jobLauncher;
    private final Job deleteOldNotificationsJob;

    /**
     * 스프링 스케줄링을 통해 매일 2시에 알림 삭제 배치 잡을 실행한다
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void runDeleteOldNotificationsJob() {
        try {
            log.info("알림 삭제 배치 작업 시작");

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(deleteOldNotificationsJob, jobParameters);

            log.info("알림 삭제 배치 작업 실행 완료");
        } catch (Exception e) {
            log.error("알림 삭제 배치 실행 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}