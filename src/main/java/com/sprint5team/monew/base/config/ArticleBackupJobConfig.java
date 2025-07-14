package com.sprint5team.monew.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.base.util.ArticleJsonBatchWriter;
import com.sprint5team.monew.base.util.S3Uploader;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class ArticleBackupJobConfig {

    private final ArticleRepository articleRepository;
    private final ObjectMapper objectMapper;
    private final S3Uploader s3Uploader;
    private final ArticleJsonBatchWriter articleJsonBatchWriter;

    @Bean
    public Job articleBackupJob(JobRepository jobRepository, Step articleBackupChunkStep) {
        return new JobBuilder("articleBackupJob", jobRepository)
                .start(articleBackupChunkStep)
                .build();
    }

    @Bean
    public Step articleBackupChunkStep(JobRepository jobRepository,
                                       PlatformTransactionManager transactionManager) {
        return new StepBuilder("articleBackupChunkStep", jobRepository)
                .<Article, String>chunk(100, transactionManager)
                .reader(articleReader())
                .processor(articleToJsonProcessor())
                .writer(articleJsonBatchWriter)
                .listener(articleJsonBatchWriter)
                .build();
    }

    @Bean
    public RepositoryItemReader<Article> articleReader() {
        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);

        return new RepositoryItemReaderBuilder<Article>()
                .name("articleReader")
                .repository(articleRepository)
                .methodName("findAllByOrderByIdAsc")
                .arguments(List.of())
                .pageSize(100)
                .sorts(sorts)
                .build();
    }

    @Bean
    public ItemProcessor<Article, String> articleToJsonProcessor() {
        return article -> objectMapper.writeValueAsString(article);
    }
}
