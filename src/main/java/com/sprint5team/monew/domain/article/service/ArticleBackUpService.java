package com.sprint5team.monew.domain.article.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.base.util.S3Uploader;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ArticleBackUpService {

    private final ArticleRepository articleRepository;
    private final ObjectMapper objectMapper;
    private final S3Uploader s3Uploader;

    public void backupArticles(LocalDate date) {
        Instant start = date.atStartOfDay(ZoneId.of("UTC")).toInstant();
        Instant end = date.plusDays(1).atStartOfDay(ZoneId.of("UTC")).toInstant();

        List<Article> articles = articleRepository.findByOriginalDateTimeBetween(start, end);

        try {
            String json = objectMapper.writeValueAsString(articles);
            String fileName = "backup/news_" + date + ".json";
            s3Uploader.upload(fileName, json);
        } catch (Exception e) {
            throw new RuntimeException("백업 중 오류", e);
        }
    }
}
