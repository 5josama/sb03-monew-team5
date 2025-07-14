package com.sprint5team.monew.domain.article.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QArticle is a Querydsl query type for Article
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticle extends EntityPathBase<Article> {

    private static final long serialVersionUID = 70838746L;

    public static final QArticle article = new QArticle("article");

    public final com.sprint5team.monew.base.entity.QBaseEntity _super = new com.sprint5team.monew.base.entity.QBaseEntity(this);

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    //inherited
    public final ComparablePath<java.util.UUID> id = _super.id;

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final DateTimePath<java.time.Instant> originalDateTime = createDateTime("originalDateTime", java.time.Instant.class);

    public final StringPath source = createString("source");

    public final StringPath sourceUrl = createString("sourceUrl");

    public final StringPath summary = createString("summary");

    public final StringPath title = createString("title");

    public QArticle(String variable) {
        super(Article.class, forVariable(variable));
    }

    public QArticle(Path<? extends Article> path) {
        super(path.getType(), path.getMetadata());
    }

    public QArticle(PathMetadata metadata) {
        super(Article.class, metadata);
    }

}

