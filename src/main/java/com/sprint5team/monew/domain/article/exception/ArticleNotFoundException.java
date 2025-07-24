package com.sprint5team.monew.domain.article.exception;

import com.sprint5team.monew.base.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.time.Instant;

public class ArticleNotFoundException extends BaseException {

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
      return "뉴스 기사 데이터 없음.";
    }

    @Override
    public String getDetails() {
      return "존재하지 않는 기사 입니다.";
    }
}
