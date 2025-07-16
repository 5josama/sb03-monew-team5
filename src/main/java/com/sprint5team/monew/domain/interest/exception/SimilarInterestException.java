package com.sprint5team.monew.domain.interest.exception;

import com.sprint5team.monew.base.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * PackageName  : com.sprint5team.monew.domain.exception
 * FileName     : SimilarInterestException
 * Author       : dounguk
 * Date         : 2025. 7. 15.
 */
public class SimilarInterestException extends BaseException {

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
        return "관심사 80% 이상 일치";
    }

    @Override
    public String getDetails() {
        return "이미 유사한 이름의 관심사가 있습니다.";
    }
}
