package com.sprint5team.monew.domain.comment.exception;

import com.sprint5team.monew.base.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.time.Instant;

public class CommentNotFoundException extends BaseException {
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
    return "댓글 데이터 없음.";
  }

  @Override
  public String getDetails() {
    return "존재하지 않는 댓글 입니다.";
  }}
