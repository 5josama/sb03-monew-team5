package com.sprint5team.monew.service.article;

import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.article.service.ArticleScraper;
import com.sprint5team.monew.domain.article.service.ArticleService;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("뉴스 기사 수집 서비스 단위 테스트")
public class ArticleScraperTest {

    @Mock private ArticleRepository articleRepository;
    @Mock private RestTemplate restTemplate;
    @Mock private KeywordRepository keywordRepository;
    @Mock private ArticleService articleService;

    @InjectMocks private ArticleScraper articleScraper;

    @Test
    void API_RSS_등으로_기사를_수집할_수_있다() {
        // given
        String dummyXml = """
            <rss>
              <channel>
                <item>
                  <title>AI 관련 기사 제목</title>
                  <link>http://example.com/article</link>
                  <description>경제 설명</description>
                  <pubDate>Wed, 10 Jul 2024 10:00:00 +0900</pubDate>
                  <source>네이버뉴스</source>
                </item>
              </channel>
            </rss>
        """;

        // Naver OpenAPI 요청 mocking
        ResponseEntity<String> responseEntity = new ResponseEntity<>(dummyXml, HttpStatus.OK);
        given(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).willReturn(responseEntity);

        doAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            articleRepository.save(article);
            return article;
        }).when(articleService).saveArticle(any(Article.class));

        given(keywordRepository.findAll()).willReturn(List.of(
                new Keyword(Instant.now(), "AI", null)
        ));

        // when
        articleScraper.scrapeAll();

        // then
        verify(articleRepository, atLeastOnce()).save(any(Article.class));
    }
}
