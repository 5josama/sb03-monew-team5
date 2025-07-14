package com.sprint5team.monew.base.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleBackUpScheduler {
    private final JobLauncher jobLauncher;
    private final Job articleBackupJob;

    @Scheduled(cron = "0 0 2 * * ?")
    public void runArticleBackupJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(articleBackupJob, params);
            log.info("뉴스 기사 백업 배치 실행 완료");

        } catch (Exception e) {
            log.error("뉴스 기사 백업 배치 실행 중 오류 발생", e);
        }
    }
}
