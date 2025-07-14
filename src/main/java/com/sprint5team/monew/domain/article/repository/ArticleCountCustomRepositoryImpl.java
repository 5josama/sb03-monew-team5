package com.sprint5team.monew.domain.article.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint5team.monew.domain.article.entity.QArticleCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ArticleCountCustomRepositoryImpl implements ArticleCountCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<UUID, Long> countViewByArticleIds(List<UUID> articleIds) {
        QArticleCount articleCount = QArticleCount.articleCount;

        return queryFactory
                .select(articleCount.article.id, articleCount.count())
                .from(articleCount)
                .where(articleCount.article.id.in(articleIds))
                .groupBy(articleCount.article.id)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(articleCount.article.id),
                        tuple -> tuple.get(articleCount.count())
                ));
    }

    @Override
    public Set<UUID> findViewedArticleIdsByUserId(UUID userId, List<UUID> articleIds) {
        QArticleCount articleCount = QArticleCount.articleCount;

        return new HashSet<>(
                queryFactory
                        .select(articleCount.article.id)
                        .from(articleCount)
                        .where(articleCount.user.id.eq(userId)
                                .and(articleCount.article.id.in(articleIds)))
                        .fetch()
        );
    }
}
