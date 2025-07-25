package com.sprint5team.monew.domain.article.util;

import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ArticleConsumer {

    private final ArticleRepository articleRepository;
    private final ArticleQueueManager articleQueueManager;
    private final ExecutorService executorService;

    public ArticleConsumer(ArticleRepository articleRepository,
                           ArticleQueueManager articleQueueManager) {
        this.articleRepository = articleRepository;
        this.articleQueueManager = articleQueueManager;
        this.executorService = Executors.newFixedThreadPool(12);
    }

    public void consume(int threadCount, CountDownLatch latch) {
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    List<String> batch = new ArrayList<>();
                    String url;
                    while ((url = articleQueueManager.dequeue()) != null) {
                        batch.add(url);

                        if (batch.size() >= 100) {
                            checkAndMark(batch);
                            batch.clear();
                        }
                    }

                    if (!batch.isEmpty()) checkAndMark(batch);
                } finally {
                    latch.countDown();
                }
            });
        }
    }

    private void checkAndMark(List<String> urls) {
        List<Article> existingArticles = articleRepository.findAllBySourceUrlIn(urls);
        existingArticles.stream()
                .map(Article::getSourceUrl)
                .forEach(articleQueueManager::addExisting);
    }

    @PreDestroy
    public void shutdownExecutor() {
        executorService.shutdown();
    }
}
