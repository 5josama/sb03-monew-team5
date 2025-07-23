package com.sprint5team.monew.base.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BatchMetadataService {

    private final JdbcTemplate jdbcTemplate;

    public Optional<Instant> findLastSuccessExecutionTime(String jobName) {
        String sql = """
                    SELECT je.end_time
                    FROM batch_job_execution je
                    JOIN batch_job_instance ji ON je.job_instance_id = ji.job_instance_id
                    WHERE LOWER(ji.job_name) = LOWER(?)
                      AND je.status = 'COMPLETED'
                    ORDER BY je.end_time DESC
                    LIMIT 1
                """;

        try {
            Timestamp timestamp = jdbcTemplate.queryForObject(sql, new Object[]{jobName}, Timestamp.class);
            return Optional.ofNullable(timestamp).map(Timestamp::toInstant);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
