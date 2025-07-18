package com.sprint5team.monew.domain.keyword.exception;

import com.sprint5team.monew.base.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * PackageName  : com.sprint5team.monew.domain.keyword.exception
 * FileName     : NoKeywordsToUpdateException
 * Author       : dounguk
 * Date         : 2025. 7. 17.
 */
public class NoKeywordsToUpdateException extends BaseException {
    @Override
    public Instant getTimestamp() {
        return Instant.now();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getMessage() {
        return "변경할 키워드 없음";
    }

    @Override
    public String getDetails() {
        return "관심사가 변경할 내용을 가지고 있지 않습니다.";
    }
}
