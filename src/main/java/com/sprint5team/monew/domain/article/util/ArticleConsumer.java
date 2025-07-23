package com.sprint5team.monew.base.util;

import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
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

    public void consume(int threadCount, CountDownLatch latch) {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    List<String> urls;
                    while ((urls = articleQueueManager.dequeue()) != null) {
                        List<Article> existingArticles = articleRepository.findAllBySourceUrlIn(urls);
                        articleQueueManager.addAll(existingArticles.stream().map(Article::getSourceUrl).toList());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        executor.shutdown();
    }
}
