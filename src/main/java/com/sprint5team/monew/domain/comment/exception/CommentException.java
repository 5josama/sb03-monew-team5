package com.sprint5team.monew.domain.comment.exception;

import com.sprint5team.monew.base.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.time.Instant;

public class CommentException extends BaseException {
    @Override
    public Instant getTimestamp() {
        return Instant.now();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getMessage() {return "알수없는 오류가 발생했습니다.";}

    @Override
    public String getDetails() {
        return "";
    }
}
