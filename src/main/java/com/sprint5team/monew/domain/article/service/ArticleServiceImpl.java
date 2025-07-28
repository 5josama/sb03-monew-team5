package com.sprint5team.monew.domain.article.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.domain.article.util.ArticleConsumer;
import com.sprint5team.monew.domain.article.util.ArticleQueueManager;
import com.sprint5team.monew.base.util.InterestMatcher;
import com.sprint5team.monew.base.util.S3Storage;
import com.sprint5team.monew.domain.article.dto.*;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.entity.ArticleCount;
import com.sprint5team.monew.domain.article.entity.ArticleKeyword;
import com.sprint5team.monew.domain.article.exception.ArticleNotFoundException;
import com.sprint5team.monew.domain.article.mapper.ArticleMapper;
import com.sprint5team.monew.domain.article.mapper.ArticleViewMapper;
import com.sprint5team.monew.domain.article.repository.ArticleCountRepository;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleCountRepository articleCountRepository;
    private final UserRepository userRepository;
    private final ArticleViewMapper articleViewMapper;
    private final InterestRepository interestRepository;
    private final KeywordRepository keywordRepository;
    private final ArticleMapper articleMapper;
    private final S3Storage s3Storage;
    private final ObjectMapper objectMapper;
    private final CommentRepository commentRepository;
    private final InterestMatcher interestMatcher;
    private final ArticleQueueManager  articleQueueManager;
    private final ArticleConsumer  articleConsumer;

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
        Map<UUID, Long> viewCount = articleCountRepository.countViewByArticleIds(List.of(articleId));
        List<ArticleCommentCount> counts = Optional.ofNullable(
                commentRepository.countByArticleIds(List.of(articleId))
        ).orElse(Collections.emptyList());

        Map<UUID, Long> commentCount = counts.stream()
                .collect(Collectors.toMap(ArticleCommentCount::getArticleId, ArticleCommentCount::getCount));
        return articleViewMapper.toDto(article, user, articleCount.orElse(null), viewCount.getOrDefault(articleId, 0L), commentCount.getOrDefault(articleId, 0L));
    }

    @Override
    public CursorPageResponseArticleDto getArticles(CursorPageFilter filter, UUID userId) {
        List<String> keywordList = new ArrayList<>();
        if (filter.interestId() != null) {
            Interest interest = interestRepository.findById(filter.interestId()).orElseThrow(
                    () -> new IllegalArgumentException("Interest id " + filter.interestId() + " not found")
            );
            List<Keyword> keywords = keywordRepository.findAllByInterestIn(List.of(interest));

            keywordList = keywords.stream().map(Keyword::getName).toList();
        }

        List<Article> articles = articleRepository.findByCursorFilter(filter, keywordList);

        List<Article> content = articles.size() > filter.limit() ? articles.subList(0, filter.limit()) : articles;

        Instant nextAfter = content.isEmpty() ? null : content.get(content.size() -1).getCreatedAt();

        boolean hasNext = articles.size() > filter.limit();

        long totalElements = articleRepository.countByCursorFilter(filter, keywordList);

        List<UUID> articleIds = articles.stream().map(Article::getId).toList();

        Map<UUID, Long> viewCountMap = articleCountRepository.countViewByArticleIds(articleIds);
        Set<UUID> viewedByMeSet = articleCountRepository.findViewedArticleIdsByUserId(userId, articleIds);
        Map<UUID, Long> commentCount = commentRepository.countByArticleIds(articleIds).stream()
                .collect(Collectors.toMap(ArticleCommentCount::getArticleId, ArticleCommentCount::getCount));

        String nextCursor = null;

        if (!content.isEmpty()) {
            Article last = content.get(content.size() - 1);

            switch (filter.orderBy()) {
                case "publishDate" -> nextCursor = last.getCreatedAt().toString();
                case "commentCount" -> nextCursor = String.valueOf(
                        commentCount.getOrDefault(last.getId(), 0L)
                );
                case "viewCount" -> nextCursor = String.valueOf(
                        viewCountMap.getOrDefault(last.getId(), 0L)
                );
            }
        }

        List<ArticleDto> result = content.stream()
                .map(article -> articleMapper.toDto(
                        article,
                        commentCount.getOrDefault(article.getId(), 0L),
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
  
    @Override
    public List<String> getSources() {
        return articleRepository.findDistinctSources();
    }

    @Override
    public List<ArticleRestoreResultDto> restoreArticle(Instant from, Instant to) {
        List<String> articleJson = s3Storage.readArticlesFromBackup(from, to);

        List<Article> restoredArticles = articleJson.stream()
                .flatMap(json -> {
                    try {
                        return Arrays.stream(objectMapper.readValue(json, Article[].class));
                    } catch (Exception e) {
                        throw new RuntimeException("복구 실패", e);
                    }
                }).toList();

        List<String> sourceUrls = restoredArticles.stream()
                .map(Article::getSourceUrl)
                .distinct()
                .toList();

        articleQueueManager.enqueue(sourceUrls);

        // CountDownLatch로 병렬 처리 완료 대기
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);

        articleConsumer.consume(threadCount, latch);

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("큐 소비 중 오류 발생", e);
        }

        Set<String> existingSourceUrls = articleQueueManager.getExistingUrls();
        Set<String> seen = new HashSet<>();
        List<Article> lostArticles = restoredArticles.stream()
                .filter(article -> !existingSourceUrls.contains(article.getSourceUrl()))
                .filter(article -> seen.add(article.getSourceUrl()))
                .map(article -> new Article(
                        article.getSource(),
                        article.getSourceUrl(),
                        article.getTitle(),
                        article.getSummary(),
                        article.getOriginalDateTime()
                ))
                .toList();

        List<Article> savedArticle;
        try {
            savedArticle = articleRepository.saveAll(lostArticles);
            log.info("실제 저장된 기사 수: {}", savedArticle.size());
        } catch (Exception e) {
            log.info("기사 저장 실패: {}", lostArticles, e);
            throw new RuntimeException("기사 저장 실패", e);
        }

        List<String> restoredIds = savedArticle.stream()
                .map(article -> article.getId().toString())
                .toList();

        return List.of(new ArticleRestoreResultDto(
                Instant.now(),
                restoredIds,
                restoredIds.size()
        ));
    }

    @Override
    public void softDeleteArticle(UUID articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(
                ArticleNotFoundException::new
        );

        if (!article.isDeleted()) {
            article.softDelete();
            articleRepository.save(article);
        }
    }

    @Override
    public void hardDeleteArticle(UUID articleId) {
        articleRepository.findById(articleId).orElseThrow(ArticleNotFoundException::new);

        List<Comment> comments = commentRepository.findByArticleId(articleId);

        List<ArticleCount> articleViews = articleCountRepository.findByArticleId(articleId);

        commentRepository.deleteAll(comments);
        articleCountRepository.deleteAll(articleViews);
        articleRepository.deleteById(articleId);
    }

    @Override
    @Transactional
    public void saveArticle(Article article) {
        // 1. 중복 체크
        if (articleRepository.existsBySourceUrl(article.getSourceUrl())) return;

        // 2. 관심사 매칭
        log.debug("관심사 매칭 시작");
        List<Interest> matchedInterests = interestMatcher.match(article)
                .stream()
                .distinct()
                .toList();
        log.debug("관심사 매칭 결과: {}개 -> {}", matchedInterests.size(),
                matchedInterests.stream().map(Interest::getName).toList());

        // 3. 중간 테이블 관계 설정
        for (Interest interest : matchedInterests) {
            article.addArticleKeyword(ArticleKeyword.of(article, interest));
        }

        // 4. 저장
        articleRepository.save(article);
        log.info("기사 저장 완료: {}", article.getTitle());

    }
}
