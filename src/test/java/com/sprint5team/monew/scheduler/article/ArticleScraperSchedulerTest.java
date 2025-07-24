package com.sprint5team.monew.scheduler.article;

import com.sprint5team.monew.domain.article.util.ArticleScraperScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArticleScraperSchedulerTest {
    @Mock private JobLauncher jobLauncher;

    @Mock private Job articleScraperJob;

    @InjectMocks private ArticleScraperScheduler scheduler;

    @Test
    void 뉴스기사_수집_배치가_정상적으로_실행된다() throws Exception {
        // given
        when(jobLauncher.run(eq(articleScraperJob), any(JobParameters.class)))
                .thenReturn(mock(JobExecution.class));

        // when
        scheduler.runArticleScraperJob();

        // then
        verify(jobLauncher, times(1)).run(eq(articleScraperJob), any(JobParameters.class));
    }

    @Test
    void 배치_실행중_예외가_발생해도_로그만_남기고_예외를_던지지_않는다() throws Exception {
        // given
        when(jobLauncher.run(eq(articleScraperJob), any(JobParameters.class)))
                .thenThrow(new RuntimeException("실패"));

        // when
        assertDoesNotThrow(() -> scheduler.runArticleScraperJob());

        // then
        verify(jobLauncher, times(1)).run(eq(articleScraperJob), any(JobParameters.class));
    }
}
