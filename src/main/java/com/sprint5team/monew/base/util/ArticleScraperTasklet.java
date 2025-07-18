package com.sprint5team.monew.base.util;

import com.sprint5team.monew.domain.article.service.ArticleScraper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleScraperTasklet implements Tasklet {

    private final ArticleScraper articleScraper;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        articleScraper.scrapeAll();
        return RepeatStatus.FINISHED;
    }
}
