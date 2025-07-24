package com.sprint5team.monew.domain.notification.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 알림 삭제 Spring Batch 구성 클래스
 * 배치 실행 시 NotificationDeleteTasklet을 통해 7일 이상 지난 확인된 알림들을 삭제한다
 */
@Configuration
@RequiredArgsConstructor
public class NotificationDeleteBatchConfig {

    private final NotificationDeleteTasklet notificationDeleteTasklet;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    /**
     * 알림 삭제 배치 잡 정의
     *
     * @return 잡 인스턴스
     */
    @Bean
    public Job deleteOldNotificationsJob() {
        return new JobBuilder("deleteOldNotificationsJob", jobRepository)
                .start(deleteOldNotificationsStep())
                .build();
    }

    /**
     * 알림 삭제 처리 스텝 정의
     *
     * @return 스텝 인스턴스
     */
    @Bean
    public Step deleteOldNotificationsStep() {
        return new StepBuilder("deleteOldNotificationsStep", jobRepository)
                .tasklet(notificationDeleteTasklet, transactionManager)
                .build();
    }
}