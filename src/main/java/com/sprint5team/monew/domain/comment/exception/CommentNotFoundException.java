package com.sprint5team.monew.domain.comment.exception;

import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends CommentException {

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
