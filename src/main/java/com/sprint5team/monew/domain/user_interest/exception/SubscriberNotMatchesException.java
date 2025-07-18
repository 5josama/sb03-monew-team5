package com.sprint5team.monew.domain.user_interest.exception;

import com.sprint5team.monew.base.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * PackageName  : com.sprint5team.monew.domain.user_interest.exception
 * FileName     : SubscriberNotMatchesException
 * Author       : dounguk
 * Date         : 2025. 7. 19.
 */
public class SubscriberNotMatchesException extends BaseException {
    @Override
    public Instant getTimestamp() {
        return Instant.now();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getMessage() {
        return "구독자 수 불일치";
    }

    @Override
    public String getDetails() {
        return "구독수와 구독자의 수가 일치하지 않습니다.";
    }
}
