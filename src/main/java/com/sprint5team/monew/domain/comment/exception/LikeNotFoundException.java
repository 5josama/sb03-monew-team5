package com.sprint5team.monew.domain.comment.exception;

import org.springframework.http.HttpStatus;

public class LikeNotFoundException extends CommentException{
    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getMessage() {
        return "좋아요 정보를 찾을 수 없습니다.";
    }

    @Override
    public String getDetails() {
        return "";
    }}