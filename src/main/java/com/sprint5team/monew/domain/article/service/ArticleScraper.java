package com.sprint5team.monew.domain.article.service;

import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleScraper {

    private final ArticleRepository articleRepository;
    private final RestTemplate restTemplate;
//    private final KeywordRepository keywordRepository;

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    public void scrape() {
        // TODO: keywordRepository 개발 완료되면 키워드 중복 없이 get
        List<String> keywords = List.of("AI", "경제", "개발");

        for (String keyword : keywords) {
            String url = UriComponentsBuilder
                    .fromUriString("https://openapi.naver.com/v1/search/news.xml")
                    .queryParam("query", keyword)
                    .queryParam("display", 100)
                    .queryParam("sort", "sim")
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", clientId);
            headers.set("X-Naver-Client-Secret", clientSecret);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Void> request = new HttpEntity<>(headers);

            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        request,
                        String.class
                );

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    parseAndSaveArticles(response.getBody());
                } else {
                    log.warn("키워드 [{}] 에 대한 응답 실패: {}", keyword, response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("뉴스 수집 중 오류 발생 - 키워드: {}", keyword, e);
            }
        }
    }

    /**
     * API, RSS 요청을 통해 반환받은 xml을 파싱하여 DB에 저장하는 헬퍼 메서드
     *
     * @param xml
     */
    private void parseAndSaveArticles(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            NodeList items = doc.getElementsByTagName("item");

            List<Article> articles = new ArrayList<>();

            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);

                String title = getTagValue("title", item);
                String link = getTagValue("link", item);
                String description = getTagValue("description", item);
                Instant pubDate = parsePubDate(getTagValue("pubDate", item));
                String source = getTagValue("source", item);

                // TODO: articleRepository.save() 를 통해 저장할 도메인 객체 매핑 및 저장
                articles.add(new Article(source, link, title, description, false, pubDate));

                if (articleRepository.existsBySourceUrl(link)) continue;
                articleRepository.saveAll(articles);
            }
        } catch (Exception e) {
            log.error("XML 파싱 오류", e);
        }
    }

    /**
     * String으로 넘어오는 날짜 데이터를 Instant 타입으로 캐스팅 해주는 헬퍼 메서드
     *
     * @param pubDateString
     * @return
     */
    private Instant parsePubDate(String pubDateString) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("EEE, dd MMM yyyy HH:mm:ss Z")
                .toFormatter(Locale.ENGLISH);

        ZonedDateTime zdt = ZonedDateTime.parse(pubDateString, formatter);
        return zdt.toInstant();
    }

    /**
     * xml의 각 태그의 값을 파싱하는 헬퍼 메서드
     *
     * @param tag ex) <source />, <item />
     * @param element
     * @return
     */
    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0 && nodeList.item(0).getFirstChild() != null) {
            return nodeList.item(0).getFirstChild().getNodeValue();
        }
        return "";
    }
}
