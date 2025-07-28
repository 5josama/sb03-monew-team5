package com.sprint5team.monew.domain.article.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class ArticleQueueManager {

    private final BlockingQueue<String> urlChunks = new LinkedBlockingQueue<>();
    private final Set<String> existingUrls = ConcurrentHashMap.newKeySet();

    public void enqueue(List<String> urls) {
        urlChunks.addAll(urls);
    }

    public String dequeue() {
        return urlChunks.poll();
    }

    public int size() {
        return urlChunks.size();
    }

    public void addExisting(String url) {
        existingUrls.add(url);
    }

    public Set<String> getExistingUrls() {
        return existingUrls;
    }
}
