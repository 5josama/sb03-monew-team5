package com.sprint5team.monew.base.config;

import com.sprint5team.monew.base.util.ArticleScraperTasklet;
import com.sprint5team.monew.base.util.NotificationTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ArticleScraperJobConfig {

    private final ArticleScraperTasklet articleScraperTasklet;
    private final NotificationTasklet notificationTasklet;

    @Bean
    public Job articleScraperJob(JobRepository jobRepository, Step articleScraperStep, Step notificationStep) {
        return new JobBuilder("articleScraperJob", jobRepository)
                .start(articleScraperStep)
                .next(notificationStep)
                .build();
    }

    @Bean
    public Step articleScraperStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager) {
        return new StepBuilder("articleScraperStep", jobRepository)
                .tasklet(articleScraperTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step notificationStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager) {
        return new StepBuilder("notificationStep", jobRepository)
                .tasklet(notificationTasklet, transactionManager)
                .build();
    }
}
