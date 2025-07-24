package com.sprint5team.monew.service.batch;

import com.sprint5team.monew.base.service.BatchStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class BatchStatusServiceTest {

    @Mock private JobExplorer jobExplorer;
    @InjectMocks private BatchStatusService batchStatusService;

    private static final String JOB_NAME = "articleScraperJob";

    @Test
    void 마지막_배치가_성공했을때_true_반환() {
        JobInstance instance = mock(JobInstance.class);
        JobExecution execution = mock(JobExecution.class);

        when(jobExplorer.getJobInstances(JOB_NAME, 0, 1)).thenReturn(List.of(instance));
        when(jobExplorer.getJobExecutions(instance)).thenReturn(List.of(execution));
        when(execution.getStartTime()).thenReturn(LocalDateTime.of(2024, 1, 1, 10, 0));
        when(execution.getStatus()).thenReturn(BatchStatus.COMPLETED);

        assertTrue(batchStatusService.wasLastJobSuccessful());
    }

    @Test
    void 배치인스턴스가_없을때_false_반환() {
        when(jobExplorer.getJobInstances(JOB_NAME, 0, 1)).thenReturn(Collections.emptyList());
        assertFalse(batchStatusService.wasLastJobSuccessful());
    }

    @Test
    void 마지막_배치의_시작시간을_Instant로_반환() {
        JobInstance instance = mock(JobInstance.class);
        JobExecution execution = mock(JobExecution.class);
        LocalDateTime startDateTime = LocalDateTime.of(2024, 1, 1, 10, 0);

        when(jobExplorer.getJobInstances(JOB_NAME, 0, 1)).thenReturn(List.of(instance));
        when(jobExplorer.getJobExecutions(instance)).thenReturn(List.of(execution));
        when(execution.getStartTime()).thenReturn(startDateTime);

        Instant result = batchStatusService.getLastJobTime();
        assertEquals(startDateTime.atZone(ZoneId.systemDefault()).toInstant(), result);
    }

    @Test
    void 성공한_배치_개수를_반환() {
        JobInstance instance = mock(JobInstance.class);
        JobExecution exec1 = mock(JobExecution.class);
        JobExecution exec2 = mock(JobExecution.class);

        when(jobExplorer.getJobInstances(JOB_NAME, 0, Integer.MAX_VALUE)).thenReturn(List.of(instance));
        when(jobExplorer.getJobExecutions(instance)).thenReturn(List.of(exec1, exec2));
        when(exec1.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(exec2.getStatus()).thenReturn(BatchStatus.FAILED);

        assertEquals(1, batchStatusService.getSuccessCount());
    }

    @Test
    void 실패한_배치_개수를_반환() {
        JobInstance instance = mock(JobInstance.class);
        JobExecution exec1 = mock(JobExecution.class);
        JobExecution exec2 = mock(JobExecution.class);

        when(jobExplorer.getJobInstances(JOB_NAME, 0, Integer.MAX_VALUE)).thenReturn(List.of(instance));
        when(jobExplorer.getJobExecutions(instance)).thenReturn(List.of(exec1, exec2));
        when(exec1.getStatus()).thenReturn(BatchStatus.FAILED);
        when(exec2.getStatus()).thenReturn(BatchStatus.COMPLETED);

        assertEquals(1, batchStatusService.getFailureCount());
    }

    @Test
    void 전체_성공한_배치의_실행시간_총합을_반환() {
        JobInstance instance = mock(JobInstance.class);
        JobExecution exec1 = mock(JobExecution.class);
        JobExecution exec2 = mock(JobExecution.class);

        LocalDateTime s1 = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime e1 = LocalDateTime.of(2024, 1, 1, 0, 1);
        LocalDateTime s2 = LocalDateTime.of(2024, 1, 1, 1, 0);
        LocalDateTime e2 = LocalDateTime.of(2024, 1, 1, 1, 3);

        when(jobExplorer.getJobInstances(JOB_NAME, 0, Integer.MAX_VALUE)).thenReturn(List.of(instance));
        when(jobExplorer.getJobExecutions(instance)).thenReturn(List.of(exec1, exec2));
        when(exec1.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(exec2.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(exec1.getStartTime()).thenReturn(s1);
        when(exec1.getEndTime()).thenReturn(e1);
        when(exec2.getStartTime()).thenReturn(s2);
        when(exec2.getEndTime()).thenReturn(e2);

        Duration result = batchStatusService.getTotalExecutionTime();
        assertEquals(Duration.ofMinutes(4), result);
    }

    @Test
    void 마지막_실패한_배치의_에러메시지를_반환() {
        JobInstance instance = mock(JobInstance.class);
        JobExecution failedExec = mock(JobExecution.class);
        RuntimeException error = new RuntimeException("Something failed");

        LocalDateTime failTime = LocalDateTime.of(2024, 1, 1, 12, 0);

        when(jobExplorer.getJobInstances(JOB_NAME, 0, Integer.MAX_VALUE)).thenReturn(List.of(instance));
        when(jobExplorer.getJobExecutions(instance)).thenReturn(List.of(failedExec));
        when(failedExec.getStatus()).thenReturn(BatchStatus.FAILED);
        when(failedExec.getStartTime()).thenReturn(failTime);
        when(failedExec.getAllFailureExceptions()).thenReturn(List.of(error));

        String reason = batchStatusService.getLastFailureReason();
        assertEquals("Something failed", reason);
    }
    @Test
    void 실패한_배치가_없을때_기본_메시지를_반환() {
        JobInstance instance = mock(JobInstance.class);
        JobExecution exec = mock(JobExecution.class);

        when(jobExplorer.getJobInstances(JOB_NAME, 0, Integer.MAX_VALUE)).thenReturn(List.of(instance));
        when(jobExplorer.getJobExecutions(instance)).thenReturn(List.of(exec));
        when(exec.getStatus()).thenReturn(BatchStatus.COMPLETED);

        String reason = batchStatusService.getLastFailureReason();
        assertEquals("No failure reason available", reason);
    }
}
