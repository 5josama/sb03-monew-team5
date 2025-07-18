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
public class UserInterestAlreadyExistsException extends BaseException {
    @Override
    public Instant getTimestamp() {
        return Instant.now();
    };

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    };

    @Override
    public String getMessage() {
        return "이미 구독중";
    };

    @Override
    public String getDetails() {
        return "사용자는 이미 관심사를 구독중입니다.";
    };
}
