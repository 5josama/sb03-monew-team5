package com.sprint5team.monew.base.util;

import com.sprint5team.monew.domain.article.service.ArticleScraper;
import com.sprint5team.monew.domain.article.util.KeywordQueueManager;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ArticleScraperTasklet implements Tasklet {

    private final ArticleScraper articleScraper;
    private final KeywordRepository keywordRepository;
    private final KeywordQueueManager keywordQueueManager;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        articleScraper.scrapeAll();

        List<String> keywords = keywordRepository.findAll().stream()
                .map(Keyword::getName)
                .distinct()
                .toList();
        keywords.forEach(keywordQueueManager::enqueue);

        return RepeatStatus.FINISHED;
    }
}
