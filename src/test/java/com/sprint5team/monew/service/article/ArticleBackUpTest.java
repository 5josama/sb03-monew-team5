package com.sprint5team.monew.service.article;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("뉴스 기사 백업 단위 테스트")
public class ArticleBackUpTest {

    @Mock private ArticleRepository articleRepository;
    @Mock private ObjectMapper objectMapper;
    @Mock private S3Uploader s3Uploader;

    @InjectMocks private ArticleBackUpService articleBackUpService;

    @Test
    void 주어진날짜의_뉴스기사를_조회하여_JSON으로_직렬화하여_S3에_업로드할_수_있다() throws JsonProcessingException {
        LocalDate date = LocalDate.of(2025, 7, 12);
        Instant start = date.atStartOfDay(ZoneId.of("UTC")).toInstant();
        Instant end = date.plusDays(1).atStartOfDay(ZoneId.of("UTC")).toInstant();

        List<Article> articles = Arrays.asList(
                new Article("NAVER", "link1", "title1", "요약1", Instant.now()),
                new Article("NAVER", "link2", "title2", "요약2", Instant.now()),
                new Article("NAVER", "link3", "title3", "요약3", Instant.now()),
                new Article("NAVER", "link4", "title4", "요약4", Instant.now())
        );

        Mockito.when(articleRepository.findByOriginalDateTimeBetween(start, end))
                .thenReturn(articles);
        Mockito.when(objectMapper.writeValueAsString(articles))
                .thenReturn("json-문자열");

        // when
        articleBackUpService.backupArticles(date);

        // then
        String fileName = "backup/news_2025-07-10.json";
        Mockito.verify(s3Uploader).upload(fileName, "json-문자열");
    }
}
