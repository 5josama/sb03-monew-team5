package com.sprint5team.monew.service.article;

import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.article.service.ArticleService;
import com.sprint5team.monew.domain.article.util.NaverNewsApiClient;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("뉴스 기사 수집 서비스 단위 테스트")
public class NaverArticleScraperTest {

    @Mock private ArticleRepository articleRepository;
    @Mock private RestTemplate restTemplate;
    @Mock private KeywordRepository keywordRepository;
    @Mock private ArticleService articleService;

    @InjectMocks private NaverNewsApiClient naverNewsApiClient;

    @Captor
    private ArgumentCaptor<String> urlCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        naverNewsApiClient = new NaverNewsApiClient(restTemplate, articleRepository, articleService);
        ReflectionTestUtils.setField(naverNewsApiClient, "clientId", "fake-client-id");
        ReflectionTestUtils.setField(naverNewsApiClient, "clientSecret", "fake-client-secret");
    }

    @Test
    void 정상적으로_API를_호출하고_XML을_파싱한다() {
        // given
        String keyword = "AI";
        String xml = """
            <rss>
              <channel>
                <item>
                  <title>AI 뉴스</title>
                  <link>http://example.com/ai-news</link>
                  <description>AI 관련 설명</description>
                  <pubDate>Wed, 10 Jul 2024 10:00:00 +0900</pubDate>
                </item>
              </channel>
            </rss>
        """;

        given(articleRepository.existsBySourceUrl(any())).willReturn(false);
        willDoNothing().given(articleService).saveArticle(any(Article.class));

        given(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).willReturn(ResponseEntity.ok(xml));

        // when
        naverNewsApiClient.scrape(keyword);

        // then
        verify(restTemplate, times(1)).exchange(
                urlCaptor.capture(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        );
        String requestedUrl = urlCaptor.getValue();
        assert requestedUrl.contains("query=AI");
        assert requestedUrl.contains("display=100");

        verify(articleService, times(1)).saveArticle(any(Article.class));
    }

    @Test
    void API_요청이_실패하면_예외를_던진다() {
        // given
        given(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .willReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

        // when & then
        assertThatThrownBy(() -> naverNewsApiClient.scrape("AI"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("응답 실패");
    }
}
