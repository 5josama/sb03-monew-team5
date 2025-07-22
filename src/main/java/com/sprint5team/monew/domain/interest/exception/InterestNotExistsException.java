package com.sprint5team.monew.domain.interest.exception;

import com.sprint5team.monew.base.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * PackageName  : com.sprint5team.monew.domain.interest.exception
 * FileName     : InterestNotExistException
 * Author       : dounguk
 * Date         : 2025. 7. 16.
 */
public class InterestNotExistsException extends BaseException {
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
    return "일치하는 관심사 없음";
  }

  @Override
  public String getDetails() {
    return "입력된 관심사 아이디와 일치하는 관심사가 없습니다.";
  }
}
