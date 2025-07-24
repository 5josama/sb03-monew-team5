package com.sprint5team.monew.domain.article.util;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class ArticleQueueManager {

    private final BlockingQueue<List<String>> urlChunks = new LinkedBlockingQueue<>();
    @Getter
    private final Set<String> existingUrls = ConcurrentHashMap.newKeySet();

    public void enqueue(List<String> urls) {
        urlChunks.offer(urls);
    }

    public List<String> dequeue() {
        return urlChunks.poll();
    }

    public int size() {
        return urlChunks.size();
    }

    public void addAll(List<String> urls) {
        existingUrls.addAll(urls);
    }
}
