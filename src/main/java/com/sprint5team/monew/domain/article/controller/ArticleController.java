package com.sprint5team.monew.domain.article.controller;

import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.dto.CursorPageFilter;
import com.sprint5team.monew.domain.article.dto.CursorPageResponseArticleDto;
import com.sprint5team.monew.domain.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

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

    @GetMapping
    public ResponseEntity<CursorPageResponseArticleDto> getArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID interestId,
            @RequestParam(required = false) List<String> sourceIn,
            @RequestParam(required = false) Instant publishDateFrom,
            @RequestParam(required = false) Instant publishDateTo,
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
}
