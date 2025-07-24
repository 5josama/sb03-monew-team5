package com.sprint5team.monew.scheduler.article;

import com.sprint5team.monew.base.service.BatchMetadataService;
import com.sprint5team.monew.domain.article.util.ArticleBackUpScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArticleBackupSchedulerTest {

    @Mock private JobLauncher jobLauncher;
    @Mock private BatchMetadataService batchMetadataService;
    @Mock private Job articleBackupJob;

    @InjectMocks private ArticleBackUpScheduler scheduler;

    @Test
    void 뉴스기사_백업_배치가_정상적으로_실행된다() throws Exception {
        // given
        when(batchMetadataService.findLastSuccessExecutionTime(anyString()))
                .thenReturn(Optional.of(Instant.parse("2025-07-23T00:00:00Z")));
        when(jobLauncher.run(eq(articleBackupJob), any(JobParameters.class)))
                .thenReturn(mock(JobExecution.class));

        // when
        scheduler.runArticleBackupJob();

        // then
        verify(jobLauncher, times(1)).run(eq(articleBackupJob), any(JobParameters.class));
    }

    @Test
    void 배치_실행중_예외가_발생해도_로그만_남기고_예외를_던지지_않는다() throws Exception {
        // given
        when(batchMetadataService.findLastSuccessExecutionTime(anyString()))
                .thenReturn(Optional.of(Instant.now()));
        when(jobLauncher.run(eq(articleBackupJob), any(JobParameters.class)))
                .thenThrow(new RuntimeException("실패"));

        // when
        assertDoesNotThrow(() -> scheduler.runArticleBackupJob());

        // then
        verify(jobLauncher, times(1)).run(eq(articleBackupJob), any(JobParameters.class));
    }
}
