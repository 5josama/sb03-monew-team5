package com.sprint5team.monew.base.util;

import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class NaverNewsApiClient {

    private final RestTemplate restTemplate;
    private final ArticleRepository articleRepository;

    @Value("${naver.client_id}")
    private String clientId;

    @Value("${naver.client_secret}")
    private String clientSecret;

    public void scrape(String keyword) {
        String url = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/news.xml")
                .queryParam("query", keyword)
                .queryParam("display", 100)
                .queryParam("sort", "sim")
                .encode()
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("응답 실패 - 상태: " + response.getStatusCode());
        }

        // 기존의 XML 파싱 로직 그대로 재사용
        parseAndSaveArticles(response.getBody(), "NAVER", List.of(keyword));
    }

    private void parseAndSaveArticles(String xml, String source, List<String> keywords) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            NodeList items = doc.getElementsByTagName("item");

            List<Article> articles = new ArrayList<>();

            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);

                String title = getTagValue("title", item);
                String description = getTagValue("description", item);

                boolean hasKeyword = keywords.stream()
                        .anyMatch(keyword -> title.contains(keyword) || description.contains(keyword));
                if (!hasKeyword) continue;

                String link = getTagValue("link", item);
                Instant pubDate = parsePubDate(getTagValue("pubDate", item));

                if (articleRepository.existsBySourceUrl(link)) continue;

                Article article = new Article(source, link, title, description, pubDate);
                articles.add(article);
            }

            if (!articles.isEmpty()) {
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
