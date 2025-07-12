package com.sprint5team.monew.base.exception;

import java.time.Instant;
import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {

    public abstract Instant getTimestamp();
    public abstract HttpStatus getHttpStatus();
    public abstract String getMessage();
    public abstract String getDetails();
}
