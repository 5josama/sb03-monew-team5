package com.sprint5team.monew.domain.article.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class KeywordQueueManager {

    private final BlockingQueue<String> keywordQueue = new LinkedBlockingQueue<>();

    public void enqueue(String keyword) {
        keywordQueue.offer(keyword);
    }

    public String dequeue() {
        return keywordQueue.poll();
    }

    public int size() {
        return keywordQueue.size();
    }
}
