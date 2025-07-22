package com.sprint5team.monew.domain.comment.exception;

import org.springframework.http.HttpStatus;

public class AlreadyLikedException extends CommentException {

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getMessage() {return "";}

    @Override
    public String getDetails() {
        return "이미 좋아요를 누른 댓글입니다.";
    }
}

