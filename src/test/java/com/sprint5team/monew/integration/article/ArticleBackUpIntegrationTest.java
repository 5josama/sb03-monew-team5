package com.sprint5team.monew.integration.article;

import com.sprint5team.monew.base.util.S3Uploader;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;

@ActiveProfiles("test")
@SpringBootTest
@SpringBatchTest
@DisplayName("ArticleBackup 통합 테스트")
public class ArticleBackUpIntegrationTest {

    @Autowired private JobLauncher jobLauncher;
    @Autowired private Job articleBackupJob;
    @Autowired private ArticleRepository articleRepository;
    @MockitoBean private S3Uploader s3Uploader;

    @Test
    void 주어진날짜의_뉴스기사를_조회하여_JSON으로_직렬화하여_S3에_업로드할_수_있다() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        // given
        LocalDate date = LocalDate.now();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Instant now = yesterday.atTime(12, 0).atZone(ZoneId.of("UTC")).toInstant();

        List<Article> articles = Arrays.asList(
                new Article("NAVER", "link1", "title1", "요약1", now),
                new Article("NAVER", "link2", "title2", "요약2", now),
                new Article("NAVER", "link3", "title3", "요약3", now),
                new Article("NAVER", "link4", "title4", "요약4", now)
        );

        articleRepository.saveAll(articles);

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncher.run(articleBackupJob, jobParameters);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(s3Uploader, times(1)).upload(fileNameCaptor.capture(), jsonCaptor.capture());

        String expectedFileName = "backup/news_" + date.minusDays(1) + ".json";
        assertThat(fileNameCaptor.getValue()).isEqualTo(expectedFileName);
        assertThat(jsonCaptor.getValue()).contains("title1", "title2");
    }
}
