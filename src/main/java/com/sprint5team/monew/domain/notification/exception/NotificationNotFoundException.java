package com.sprint5team.monew.domain.notification.exception;

import com.sprint5team.monew.base.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * 요청한 알림 ID에 해당하는 알림이 존재하지 않을 때 발생하는 예외
 * 사용자가 확인하려는 알림이 삭제되었거나 존재하지 않을 경우에 발생한다
 */
public class NotificationNotFoundException extends BaseException {

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
        return "알림 데이터 없음.";
    }

    @Override
    public String getDetails() {
        return "존재하지 않는 알림 입니다.";
    }
}
