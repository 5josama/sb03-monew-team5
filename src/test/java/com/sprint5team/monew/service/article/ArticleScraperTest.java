package com.sprint5team.monew.service.article;

import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("뉴스 기사 수집 서비스 단위 테스트")
public class ArticleScraperTest {

    @Mock private ArticleRepository articleRepository;

    @InjectMocks private ArticleScraper articleScraper;

    @Test
    void API_RSS_등으로_기사를_수집할_수_있다() {
        // when
        articleScraper.scrape();

        // then
        then(articleRepository).should().saveAll(anyList());

    }
}
