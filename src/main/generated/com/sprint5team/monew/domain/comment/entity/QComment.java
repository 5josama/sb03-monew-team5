package com.sprint5team.monew.domain.comment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QComment is a Querydsl query type for Comment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QComment extends EntityPathBase<Comment> {

    private static final long serialVersionUID = -1828271430L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QComment comment = new QComment("comment");

    public final com.sprint5team.monew.base.entity.QBaseUpdatableEntity _super = new com.sprint5team.monew.base.entity.QBaseUpdatableEntity(this);

    public final com.sprint5team.monew.domain.article.entity.QArticle article;

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    //inherited
    public final ComparablePath<java.util.UUID> id = _super.id;

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final NumberPath<Long> likeCount = createNumber("likeCount", Long.class);

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    public final com.sprint5team.monew.domain.user.entity.QUser user;

    public QComment(String variable) {
        this(Comment.class, forVariable(variable), INITS);
    }

    public QComment(Path<? extends Comment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QComment(PathMetadata metadata, PathInits inits) {
        this(Comment.class, metadata, inits);
    }

    public QComment(Class<? extends Comment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.article = inits.isInitialized("article") ? new com.sprint5team.monew.domain.article.entity.QArticle(forProperty("article")) : null;
        this.user = inits.isInitialized("user") ? new com.sprint5team.monew.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

