package com.sprint5team.monew.base.util;

import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class ArticleConsumer {

    private final ArticleRepository articleRepository;
    private final ArticleQueueManager articleQueueManager;
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    public void consume(int threadCount, CountDownLatch latch) {
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    List<String> urls;
                    while ((urls = articleQueueManager.dequeue()) != null) {
                        List<Article> existingArticles = articleRepository.findAllBySourceUrlIn(urls);
                        articleQueueManager.addAll(existingArticles.stream().map(Article::getSourceUrl).toList());
                    }
                } finally {
                    latch.countDown(); // 스레드 작업 완료 시 카운트 감소
                }
            });
        }
    }

    @PreDestroy
    public void destroy() {
        executor.shutdown();
    }
}
