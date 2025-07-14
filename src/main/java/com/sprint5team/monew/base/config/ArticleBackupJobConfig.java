package com.sprint5team.monew.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
                .writer(s3JsonWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<Article> articleReader() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Instant start = yesterday.atStartOfDay(ZoneId.of("UTC")).toInstant();
        Instant end = yesterday.plusDays(1).atStartOfDay(ZoneId.of("UTC")).toInstant();

        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);

        return new RepositoryItemReaderBuilder<Article>()
                .name("articleReader")
                .repository(articleRepository)
                .methodName("findByOriginalDateTimeBetween")
                .arguments(List.of(start, end))
                .pageSize(100)
                .sorts(sorts)
                .build();
    }

    @Bean
    public ItemProcessor<Article, String> articleToJsonProcessor() {
        return article -> objectMapper.writeValueAsString(article);
    }

    @Bean
    public ItemWriter<String> s3JsonWriter() {
        return new ItemWriter<>() {
            @Override
            public void write(Chunk<? extends String> items) throws Exception {
                String json = "[" + String.join(",", items.getItems()) + "]";
                String fileName = "backup/news_" + LocalDate.now().minusDays(1) + ".json";
                s3Uploader.upload(fileName, json);
            }
        };
    }
}
