package com.sprint5team.monew.base.util;

import com.google.common.util.concurrent.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeywordConsumer {

    private final KeywordQueueManager keywordQueueManager;
    private final NaverNewsApiClient apiClient;
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final RateLimiter rateLimiter = RateLimiter.create(5.0);

    @Scheduled(fixedRate = 1000)
    public void consume() {
        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                String keyword = keywordQueueManager.dequeue();
                if (keyword == null) return;

                rateLimiter.acquire();
                try {
                    apiClient.scrape(keyword);
                    log.info("[Naver API] 키워드 '{}' 처리 성공", keyword);
                } catch (Exception e) {
                    log.error("[Naver API] 키워드 '{}' 처리 실패 - {}", keyword, e.getMessage());
                }
            });
        }
    }
}
