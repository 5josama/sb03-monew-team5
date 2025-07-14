package com.sprint5team.monew.domain.user.exception;

import com.sprint5team.monew.base.exception.BaseException;
import java.time.Instant;
import org.springframework.http.HttpStatus;

public class InvalidInputValueException extends BaseException {

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
    return "닉네임은 1자 이상 20자 이하로 작성해주세요";
  };

  @Override
  public String getDetails() {
    return null;
  };
}
