package com.sprint5team.monew.domain.user.exception;

import com.sprint5team.monew.base.exception.BaseException;
import java.time.Instant;
import org.springframework.http.HttpStatus;

public class UserException extends BaseException {

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
    return "이미 존재하는 이메일 입니다.";
  };

  @Override
  public String getDetails() {
    return null;
  };
}
