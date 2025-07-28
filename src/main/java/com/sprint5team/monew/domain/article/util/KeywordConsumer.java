package com.sprint5team.monew.domain.article.util;

import com.google.common.util.concurrent.RateLimiter;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostConstruct
    public void consume() {
        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                while (true) {
                    try {
                        String keyword = keywordQueueManager.take();
                        rateLimiter.acquire();

                        apiClient.scrape(keyword);
                        log.info("[Naver API] 키워드 '{}' 처리 성공", keyword);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("키워드 소비 스레드 인터럽트됨");
                        break;
                    } catch (Exception e) {
                        log.error("[Naver API] 키워드 처리 중 오류 발생", e);
                    }
                }
            });
        }
    }
    
    @PreDestroy
    public void destroy() {
        executor.shutdown();
    }
}
