package com.sprint5team.monew.domain.article.service;

import com.sprint5team.monew.domain.article.dto.ArticleDto;
import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.dto.CursorPageFilter;
import com.sprint5team.monew.domain.article.dto.CursorPageResponseArticleDto;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.entity.ArticleCount;
import com.sprint5team.monew.domain.article.exception.ArticleNotFoundException;
import com.sprint5team.monew.domain.article.mapper.ArticleMapper;
import com.sprint5team.monew.domain.article.mapper.ArticleViewMapper;
import com.sprint5team.monew.domain.article.repository.ArticleCountRepository;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleCountRepository articleCountRepository;
    private final UserRepository userRepository;
    private final ArticleViewMapper articleViewMapper;
    private final InterestRepository interestRepository;
    private final KeywordRepository keywordRepository;
    private final ArticleMapper articleMapper;

    @Override
    public ArticleViewDto saveArticleView(UUID articleId, UUID userId) {
        Article article = articleRepository.findById(articleId).orElseThrow(ArticleNotFoundException::new);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User id " + userId + " not found")
        );

        Optional<ArticleCount> articleCount = articleCountRepository.findByUserIdAndArticleId(userId, articleId);
        if (articleCount.isEmpty()) {
            articleCountRepository.save(new ArticleCount(article,  user));
        }

        return articleViewMapper.toDto(article, user, articleCount.orElse(null));
    }

    @Override
    public CursorPageResponseArticleDto getArticles(CursorPageFilter filter, UUID userId) {
        Interest interest = interestRepository.findById(filter.interestId()).orElseThrow(
                () -> new IllegalArgumentException("Interest id " + filter.interestId() + " not found")
        );

        List<Keyword> keywords = keywordRepository.findAllByInterestIn(List.of(interest));

        List<String> keywordList = keywords.stream().map(Keyword::getName).toList();

        List<Article> articles = articleRepository.findByCursorFilter(filter, keywordList);

        List<Article> content = articles.size() > filter.limit() ? articles.subList(0, filter.limit()) : articles;

        String nextCursor = null;

        Instant nextAfter = content.isEmpty() ? null : content.get(content.size() -1).getCreatedAt();

        boolean hasNext = articles.size() > filter.limit();

        long totalElements = articles.size();

        List<UUID> articleIds = articles.stream().map(Article::getId).toList();

        Map<UUID, Long> viewCountMap = articleCountRepository.countViewByArticleIds(articleIds);
        Set<UUID> viewedByMeSet = articleCountRepository.findViewedArticleIdsByUserId(userId, articleIds);

        List<ArticleDto> result = content.stream()
                .map(article -> articleMapper.toDto(
                        article,
                        0L, // commentCount - 추후 구현
                        viewCountMap.getOrDefault(article.getId(), 0L),
                        viewedByMeSet.contains(article.getId())
                ))
                .toList();

        return new CursorPageResponseArticleDto(
                result,
                nextCursor,
                nextAfter,
                filter.limit(),
                totalElements,
                hasNext
        );
    }
}
