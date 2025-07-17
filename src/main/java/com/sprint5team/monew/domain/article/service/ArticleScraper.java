package com.sprint5team.monew.domain.article.service;

import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleScraper {

    private final ArticleRepository articleRepository;
    private final RestTemplate restTemplate;
    private final KeywordRepository keywordRepository;

    @Value("${naver.client_id}")
    private String clientId;

    @Value("${naver.client_secret}")
    private String clientSecret;

    private List<String> keywords = new ArrayList<>();

    private static final List<String> RSS_FEEDS = List.of(
            "https://www.hankyung.com/feed/all-news",            // 한국경제
            "https://www.chosun.com/arc/outboundfeeds/rss/?outputType=xml", // 조선일보
            "https://www.yonhapnewstv.co.kr/browse/feed/"                     // 연합뉴스
    );

    private static final List<String> SOURCES = List.of(
            "한국경제",
            "조선일보",
            "연합뉴스"
    );

    public void scrapeAll() {
        List<String> keywords = keywordRepository.findAll().stream()
                .map(Keyword::getName)
                .distinct()
                .toList();
        this.keywords = keywords;
        scrapeNaverApi(); // 기존 OpenAPI 수집
        scrapeRssFeeds(); // RSS 기반 수집
    }

    /**
     * RSS 요청 전용 메서드
     */
    private void scrapeRssFeeds() {
        for (int i = 0; i < RSS_FEEDS.size(); i++) {
            String feedUrl = RSS_FEEDS.get(i);
            String source = SOURCES.get(i);

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("User-Agent", "Mozilla/5.0");
                headers.set("Accept", "application/rss+xml");

                HttpEntity<Void> request = new HttpEntity<>(headers);

                ResponseEntity<String> response = restTemplate.exchange(
                        feedUrl,
                        HttpMethod.GET,
                        request,
                        String.class
                );

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    parseAndSaveArticles(response.getBody(), source);
                } else {
                    log.warn("RSS 수집 실패 - URL: {} 상태: {}", feedUrl, response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("RSS 수집 중 오류 발생 - URL: {}", feedUrl, e);
            }
        }
    }

    /**
     * Naver OpenAPI 요청 전용 메서드
     */
    private void scrapeNaverApi() {
        for (String keyword : keywords) {
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

            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        request,
                        String.class
                );

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    parseAndSaveArticles(response.getBody(), "NAVER");
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
    private void parseAndSaveArticles(String xml, String source) {
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
