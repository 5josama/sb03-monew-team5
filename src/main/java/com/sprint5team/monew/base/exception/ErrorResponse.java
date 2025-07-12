package com.sprint5team.monew.base.exception;

import java.time.Instant;
import org.springframework.http.HttpStatus;


public record ErrorResponse(
        Instant timestamp,
        int status,
        String message,
        String details
) {

    public static ErrorResponse of(HttpStatus status, String details) {
        return new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                details
        );
    }
}