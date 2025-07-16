package com.sprint5team.monew.controller.article;

import com.sprint5team.monew.domain.article.controller.ArticleController;
import com.sprint5team.monew.domain.article.dto.ArticleDto;
import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.dto.CursorPageResponseArticleDto;
import com.sprint5team.monew.domain.article.service.ArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ArticleController.class)
@DisplayName("ArticleController 단위 테스트")
public class ArticleControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private ArticleService articleService;

    @Test
    void 뉴스기사_VIEW_생성_API가_정상적으로_동작한다() throws Exception {
        // given
        UUID articleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ArticleViewDto articleViewDto = new ArticleViewDto(
                UUID.randomUUID(),
                userId,
                Instant.now(),
                articleId,
                "NAVER",
                "https://naver.com/news/12333",
                "test",
                Instant.now(),
                "요약",
                10,
                10
        );

        given(articleService.saveArticleView(articleId, userId)).willReturn(articleViewDto);

        // when & then
        mockMvc.perform(post("/api/articles/{articleId}/article-views", articleId)
                .header("MoNew-Request-User-ID", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(articleId.toString()))
                .andExpect(jsonPath("$.viewedBy").value(userId.toString()));
    }

    @Test
    void 뉴스기사_목록_조회_API가_정상적으로_동작한다() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        List<ArticleDto> articleDtos = Arrays.asList(
                new ArticleDto(UUID.randomUUID(), "NAVER", "https://naver.com/news/123", "title1", "sum1", Instant.now(), 12L, 10L, false),
                new ArticleDto(UUID.randomUUID(), "NAVER", "https://naver.com/news/123", "title2", "sum2", Instant.now(), 12L, 10L, false),
                new ArticleDto(UUID.randomUUID(), "NAVER", "https://naver.com/news/123", "title3", "sum3", Instant.now(), 12L, 10L, false)
        );

        CursorPageResponseArticleDto articles = new CursorPageResponseArticleDto(
                articleDtos,
                null,
                Instant.now(),
                3,
                3,
                false
        );

        given(articleService.getArticles(any(), any())).willReturn(articles);

        // when & then
        mockMvc.perform(get("/api/articles")
                .param("orderBy", "publishDate")
                .param("direction", "DESC")
                .param("limit", "50")
                .header("MoNew-Request-User-ID", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].title").value("title1"))
                .andExpect(jsonPath("$.content[1].title").value("title2"));
    }
}
