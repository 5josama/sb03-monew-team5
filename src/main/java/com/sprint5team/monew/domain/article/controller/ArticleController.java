package com.sprint5team.monew.domain.article.controller;

import com.sprint5team.monew.domain.article.dto.ArticleRestoreResultDto;
import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.dto.CursorPageFilter;
import com.sprint5team.monew.domain.article.dto.CursorPageResponseArticleDto;
import com.sprint5team.monew.domain.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController implements ArticleApi{

    private final ArticleService articleService;

    @Override
    @PostMapping("/{articleId}/article-views")
    public ResponseEntity<ArticleViewDto> createArticleView(
            @PathVariable("articleId") UUID articleId,
            @RequestHeader("MoNew-Request-User-ID") UUID userId
    ) {
        ArticleViewDto articleViewDto = articleService.saveArticleView(articleId, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(articleViewDto);
    }

    @Override
    @GetMapping
    public ResponseEntity<CursorPageResponseArticleDto> getArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID interestId,
            @RequestParam(required = false) List<String> sourceIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime publishDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime publishDateTo,
            @RequestParam String orderBy,
            @RequestParam String direction,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Instant after,
            @RequestParam int limit,
            @RequestHeader("MoNew-Request-User-ID") UUID userId
    ) {
        CursorPageFilter filter = new CursorPageFilter(
                keyword,
                interestId,
                sourceIn,
                publishDateFrom,
                publishDateTo,
                orderBy,
                direction,
                cursor,
                after,
                limit
        );
        CursorPageResponseArticleDto articles = articleService.getArticles(filter, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(articles);
    }

    @Override
    @GetMapping("/sources")
    public ResponseEntity<List<String>> getSources() {
        List<String> sources = articleService.getSources();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(sources);
    }

    @Override
    @GetMapping("/restore")
    public ResponseEntity<List<ArticleRestoreResultDto>> restoreArticle(
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to
    ) {
        List<ArticleRestoreResultDto> articleRestoreResultDto = articleService.restoreArticle(from.atZone(ZoneOffset.UTC).toInstant(), to.atZone(ZoneOffset.UTC).toInstant());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(articleRestoreResultDto);
    }

    @Override
    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> softDeleteArticle(
            @PathVariable UUID articleId
    ) {
        articleService.softDeleteArticle(articleId);

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/{articleId}/hard")
    public ResponseEntity<Void> hardDeleteArticle(
            @PathVariable UUID articleId
    ) {
        articleService.hardDeleteArticle(articleId);
        return ResponseEntity.noContent().build();
    }

}
