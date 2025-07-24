package com.sprint5team.monew.base.util;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ArticleJsonBatchWriter implements ItemWriter<String> {

    private final S3Storage s3Storage;
    private final List<String> articles = new ArrayList<>();

    @Override
    public void write(Chunk<? extends String> items) throws Exception {
        articles.addAll(items.getItems());
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        articles.clear();
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution) {
        String json = "[" + String.join(",", articles) + "]";
        String fileName = "backup/news_" + LocalDate.now().minusDays(1) + ".json";
        s3Storage.upload(fileName, json);
    }
}
