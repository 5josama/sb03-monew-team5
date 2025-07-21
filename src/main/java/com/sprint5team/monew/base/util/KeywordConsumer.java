package com.sprint5team.monew.base.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeywordConsumer {

    private final KeywordQueueManager keywordQueueManager;
    private final NaverNewsApiClient apiClient;

    @Scheduled(fixedRate = 550)
    public void consume() {
        String keyword = keywordQueueManager.dequeue();
        if (keyword == null) return;

        try {
            apiClient.scrape(keyword);
            log.info("[Naver API] 키워드 '{}' 처리 성공", keyword);
        } catch (Exception e) {
            log.error("[Naver API] 키워드 '{}' 처리 실패 - {}", keyword, e.getMessage());
        }
    }
}
