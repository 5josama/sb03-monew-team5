package com.sprint5team.monew.domain.user.exception;

import com.sprint5team.monew.base.exception.BaseException;
import java.time.Instant;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {

  @Override
  public Instant getTimestamp() {
    return Instant.now();
  };

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.NOT_FOUND;
  };

  @Override
  public String getMessage() {
    return "존재하지 않는 사용자입니다.";
  };

  @Override
  public String getDetails() {
    return null;
  };
}
