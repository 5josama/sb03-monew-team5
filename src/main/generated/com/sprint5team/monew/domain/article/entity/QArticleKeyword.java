package com.sprint5team.monew.domain.article.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticleKeyword is a Querydsl query type for ArticleKeyword
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticleKeyword extends EntityPathBase<ArticleKeyword> {

    private static final long serialVersionUID = -418585937L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArticleKeyword articleKeyword1 = new QArticleKeyword("articleKeyword1");

    public final QArticle article;

    public final QArticleKeyword articleKeyword;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public QArticleKeyword(String variable) {
        this(ArticleKeyword.class, forVariable(variable), INITS);
    }

    public QArticleKeyword(Path<? extends ArticleKeyword> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArticleKeyword(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArticleKeyword(PathMetadata metadata, PathInits inits) {
        this(ArticleKeyword.class, metadata, inits);
    }

    public QArticleKeyword(Class<? extends ArticleKeyword> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.article = inits.isInitialized("article") ? new QArticle(forProperty("article")) : null;
        this.articleKeyword = inits.isInitialized("articleKeyword") ? new QArticleKeyword(forProperty("articleKeyword"), inits.get("articleKeyword")) : null;
    }

}

