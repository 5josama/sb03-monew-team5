package com.sprint5team.monew.domain.article.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint5team.monew.domain.article.dto.CursorPageFilter;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.entity.QArticle;
import com.sprint5team.monew.domain.article.entity.QArticleCount;
import com.sprint5team.monew.domain.comment.entity.QComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ArticleCustomRepositoryImpl implements ArticleCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Article> findByCursorFilter(CursorPageFilter filter, List<String> interestKeyword) {
        QArticle article = QArticle.article;
        QComment comment = QComment.comment;
        QArticleCount articleCount = QArticleCount.articleCount;

        Order direction = "ASC".equalsIgnoreCase(filter.direction()) ? Order.ASC : Order.DESC;

        BooleanBuilder builder = new BooleanBuilder();

        // 검색어 필터 (제목 또는 요약)
        if (filter.keyword() != null) {
            builder.andAnyOf(
                    article.title.containsIgnoreCase(filter.keyword()),
                    article.summary.containsIgnoreCase(filter.keyword())
            );
        }

        // 출처 필터
        if (filter.sourceIn() != null) {
            builder.and(article.source.in(filter.sourceIn()));
        }

        // 날짜 필터
        if (filter.publishDateFrom() != null && filter.publishDateTo() != null) {
            builder.and(article.createdAt.between(filter.publishDateFrom(), filter.publishDateTo()));
        } else {
            if (filter.publishDateFrom() != null) {
                builder.and(article.createdAt.after(filter.publishDateFrom()));
            }
            if (filter.publishDateTo() != null) {
                builder.and(article.createdAt.before(filter.publishDateTo()));
            }
        }

        // 관심 키워드 (제목 또는 요약 포함)
        if (interestKeyword != null && !interestKeyword.isEmpty()) {
            BooleanBuilder keywordGroup = new BooleanBuilder();
            for (String keyword : interestKeyword) {
                keywordGroup.or(article.title.containsIgnoreCase(keyword));
                keywordGroup.or(article.summary.containsIgnoreCase(keyword));
            }
            builder.and(keywordGroup);
        }

        // 정렬 기준 설정
        OrderSpecifier<?> orderSpecifier;
        switch (filter.orderBy()) {
            case "viewCount":
                return queryFactory
                        .select(article)
                        .from(article)
                        .leftJoin(articleCount).on(articleCount.article.eq(article))
                        .where(builder)
                        .groupBy(article.id)
                        .orderBy(direction == Order.ASC
                                ? articleCount.count().asc()
                                : articleCount.count().desc())
                        .limit(filter.limit() + 1)
                        .fetch();

            case "commentCount":
                return queryFactory
                        .select(article)
                        .from(article)
                        .leftJoin(comment).on(comment.article.eq(article))
                        .where(builder)
                        .groupBy(article.id)
                        .orderBy(direction == Order.ASC
                                ? comment.count().asc()
                                : comment.count().desc())
                        .limit(filter.limit() + 1)
                        .fetch();

            case "publishDate":
            default:
                if (filter.after() != null) {
                    builder.and(article.createdAt.gt(filter.after()));
                }

                orderSpecifier = direction == Order.ASC
                        ? article.createdAt.asc()
                        : article.createdAt.desc();
                return queryFactory
                        .select(article)
                        .from(article)
                        .leftJoin(articleCount).on(articleCount.article.eq(article))
                        .leftJoin(comment).on(comment.article.eq(article))
                        .where(builder)
                        .groupBy(article.id)
                        .orderBy(orderSpecifier)
                        .limit(filter.limit() + 1)
                        .fetch();

        }
    }

    @Override
    public long countByCursorFilter(CursorPageFilter filter, List<String> interestKeyword) {
        QArticle article = QArticle.article;

        BooleanBuilder builder = new BooleanBuilder();

        // 검색어 필터 (제목 또는 요약)
        if (filter.keyword() != null) {
            builder.andAnyOf(
                    article.title.containsIgnoreCase(filter.keyword()),
                    article.summary.containsIgnoreCase(filter.keyword())
            );
        }

        // 출처 필터
        if (filter.sourceIn() != null) {
            builder.and(article.source.in(filter.sourceIn()));
        }

        // 날짜 필터
        if (filter.publishDateFrom() != null && filter.publishDateTo() != null) {
            builder.and(article.createdAt.between(filter.publishDateFrom(), filter.publishDateTo()));
        } else {
            if (filter.publishDateFrom() != null) {
                builder.and(article.createdAt.after(filter.publishDateFrom()));
            }
            if (filter.publishDateTo() != null) {
                builder.and(article.createdAt.before(filter.publishDateTo()));
            }
        }

        // 관심 키워드 (제목 또는 요약 포함)
        if (interestKeyword != null && !interestKeyword.isEmpty()) {
            BooleanBuilder keywordGroup = new BooleanBuilder();
            for (String keyword : interestKeyword) {
                keywordGroup.or(article.title.containsIgnoreCase(keyword));
                keywordGroup.or(article.summary.containsIgnoreCase(keyword));
            }
            builder.and(keywordGroup);
        }

        return queryFactory
                .select(article.count())
                .from(article)
                .where(builder)
                .fetchOne();
    }
}
