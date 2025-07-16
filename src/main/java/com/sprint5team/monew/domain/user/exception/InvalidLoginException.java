package com.sprint5team.monew.domain.user.exception;

import com.sprint5team.monew.base.exception.BaseException;
import java.time.Instant;
import org.springframework.http.HttpStatus;

public class InvalidLoginException extends BaseException {

  @Override
  public Instant getTimestamp() {
    return Instant.now();
  };

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.UNAUTHORIZED;
  };

  @Override
  public String getMessage() {
    return "이메일 또는 비밀번호가 올바르지 않습니다.";
  };

  @Override
  public String getDetails() {
    return null;
  };
}
