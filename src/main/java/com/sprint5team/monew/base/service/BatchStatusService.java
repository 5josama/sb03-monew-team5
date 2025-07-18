package com.sprint5team.monew.base.service;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class BatchStatusService {

    private final JobExplorer jobExplorer;

    private static final String JOB_NAME = "articleScraperJob";

    public boolean wasLastJobSuccessful() {
        JobInstance lastInstance = getLastJobInstance();
        if (lastInstance == null) return false;

        JobExecution lastExecution = jobExplorer.getJobExecutions(lastInstance)
                .stream()
                .max(Comparator.comparing(JobExecution::getStartTime))
                .orElse(null);

        return lastExecution != null && lastExecution.getStatus() == BatchStatus.COMPLETED;
    }


    public Instant getLastJobTime() {
        JobInstance lastInstance = getLastJobInstance();
        if (lastInstance == null) return null;

        return jobExplorer.getJobExecutions(lastInstance)
                .stream()
                .max(Comparator.comparing(JobExecution::getStartTime))
                .map(exec -> exec.getStartTime().atZone(ZoneId.systemDefault()).toInstant())
                .orElse(null);
    }

    public long getSuccessCount() {
        return jobExplorer.getJobInstances(JOB_NAME, 0, Integer.MAX_VALUE).stream()
                .flatMap(instance -> jobExplorer.getJobExecutions(instance).stream())
                .filter(exec -> exec.getStatus() == BatchStatus.COMPLETED)
                .count();
    }

    public long getFailureCount() {
        return jobExplorer.getJobInstances(JOB_NAME, 0, Integer.MAX_VALUE).stream()
                .flatMap(instance -> jobExplorer.getJobExecutions(instance).stream())
                .filter(exec -> exec.getStatus() == BatchStatus.FAILED)
                .count();
    }

    public Duration getTotalExecutionTime() {
        return jobExplorer.getJobInstances(JOB_NAME, 0, Integer.MAX_VALUE).stream()
                .flatMap(instance -> jobExplorer.getJobExecutions(instance).stream())
                .filter(exec -> exec.getStatus() == BatchStatus.COMPLETED)
                .map(exec -> Duration.between(
                        exec.getStartTime().atZone(ZoneId.systemDefault()).toInstant(),
                        exec.getEndTime().atZone(ZoneId.systemDefault()).toInstant()))
                .reduce(Duration.ZERO, Duration::plus);
    }

    public String getLastFailureReason() {
        return jobExplorer.getJobInstances(JOB_NAME, 0, Integer.MAX_VALUE).stream()
                .flatMap(instance -> jobExplorer.getJobExecutions(instance).stream())
                .filter(exec -> exec.getStatus() == BatchStatus.FAILED)
                .max(Comparator.comparing(JobExecution::getStartTime))
                .flatMap(exec -> exec.getAllFailureExceptions().stream().findFirst())
                .map(Throwable::getMessage)
                .orElse("No failure reason available");
    }

    private JobInstance getLastJobInstance() {
        return jobExplorer.getJobInstances(JOB_NAME, 0, 1)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
