package com.sprint5team.monew.domain.user_interest.exception;

import com.sprint5team.monew.base.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * PackageName  : com.sprint5team.monew.domain.userinterest
 * FileName     : UserInterestAlreadyExistsException
 * Author       : dounguk
 * Date         : 2025. 7. 18.
 */
public class InvalidSubscriptionRequestException extends BaseException {
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
        return "구독 요청에 문제 있음";
    };

    @Override
    public String getDetails() {
        return "잘못된 구독요청 입니다";
    };
}
