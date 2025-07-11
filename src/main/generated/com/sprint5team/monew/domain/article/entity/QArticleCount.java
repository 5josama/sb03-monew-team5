package com.sprint5team.monew.domain.article.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticleCount is a Querydsl query type for ArticleCount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticleCount extends EntityPathBase<ArticleCount> {

    private static final long serialVersionUID = -271216811L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArticleCount articleCount = new QArticleCount("articleCount");

    public final QArticle article;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final com.sprint5team.monew.domain.user.entity.QUser user;

    public QArticleCount(String variable) {
        this(ArticleCount.class, forVariable(variable), INITS);
    }

    public QArticleCount(Path<? extends ArticleCount> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArticleCount(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArticleCount(PathMetadata metadata, PathInits inits) {
        this(ArticleCount.class, metadata, inits);
    }

    public QArticleCount(Class<? extends ArticleCount> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.article = inits.isInitialized("article") ? new QArticle(forProperty("article")) : null;
        this.user = inits.isInitialized("user") ? new com.sprint5team.monew.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

