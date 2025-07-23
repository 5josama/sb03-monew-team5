package com.sprint5team.monew.domain.notification.exception;

import com.sprint5team.monew.base.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.time.Instant;

public class InvalidRequestParameterException extends BaseException {
    @Override
    public Instant getTimestamp() {
        return Instant.now();
    };

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    };

    @Override
    public String getMessage() {
        return "userId는 null일 수 없습니다.";
    };

    @Override
    public String getDetails() {
        return null;
    };
}

