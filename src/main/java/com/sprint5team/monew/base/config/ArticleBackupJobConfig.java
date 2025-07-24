package com.sprint5team.monew.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.base.util.ArticleJsonBatchWriter;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class ArticleBackupJobConfig {

    private final ArticleRepository articleRepository;
    private final ObjectMapper objectMapper;
    private final ArticleJsonBatchWriter articleJsonBatchWriter;

    @Bean
    public Job articleBackupJob(JobRepository jobRepository, Step articleBackupChunkStep) {
        return new JobBuilder("articleBackupJob", jobRepository)
                .start(articleBackupChunkStep)
                .build();
    }

    @Bean
    public Step articleBackupChunkStep(JobRepository jobRepository,
                                       PlatformTransactionManager transactionManager,
                                       @Qualifier("articleReader") ItemReader<Article> articleReader) {
        return new StepBuilder("articleBackupChunkStep", jobRepository)
                .<Article, String>chunk(100, transactionManager)
                .reader(articleReader)
                .processor(articleToJsonProcessor())
                .writer(articleJsonBatchWriter)
                .listener(articleJsonBatchWriter)
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Article> articleReader(@Value("#{jobParameters['lastExecutedAt']}") String lastExecutedAt) {
        Instant from = Instant.parse(lastExecutedAt);

        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);

        return new RepositoryItemReaderBuilder<Article>()
                .name("articleReader")
                .repository(articleRepository)
                .methodName("findByCreatedAtAfterOrderByCreatedAtAsc")
                .arguments(List.of(from))
                .pageSize(100)
                .sorts(sorts)
                .build();
    }

    @Bean
    public ItemProcessor<Article, String> articleToJsonProcessor() {
        return article -> objectMapper.writeValueAsString(article);
    }
}
