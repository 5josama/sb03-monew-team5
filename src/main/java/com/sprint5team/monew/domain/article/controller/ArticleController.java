package com.sprint5team.monew.domain.article.controller;

import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
