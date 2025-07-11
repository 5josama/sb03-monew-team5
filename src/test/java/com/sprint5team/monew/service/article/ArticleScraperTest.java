package com.sprint5team.monew.service.article;

import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.article.service.ArticleScraper;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("뉴스 기사 수집 서비스 단위 테스트")
public class ArticleScraperTest {

    @Mock private ArticleRepository articleRepository;
    @Mock private RestTemplate restTemplate;

    @InjectMocks private ArticleScraper articleScraper;

    @Test
    void API_RSS_등으로_기사를_수집할_수_있다() {
        // given
        String dummyXml = """
            <rss>
              <channel>
                <item>
                  <title>테스트 제목</title>
                  <link>http://example.com/article</link>
                  <description>기사 설명</description>
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

        // RSS 피드 요청 mocking (3개 정도 필요)
        given(restTemplate.getForEntity(anyString(), eq(String.class)))
                .willReturn(responseEntity);

        // when
        articleScraper.scrapeAll();

        // then
        then(articleRepository).should(atLeastOnce()).saveAll(anyList());
    }
}
