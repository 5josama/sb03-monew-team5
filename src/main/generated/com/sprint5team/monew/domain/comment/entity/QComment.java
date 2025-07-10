package com.sprint5team.monew.domain.comment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QComment is a Querydsl query type for Comment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QComment extends EntityPathBase<Comment> {

    private static final long serialVersionUID = -1828271430L;

    public static final QComment comment = new QComment("comment");

    public final com.sprint5team.monew.base.entity.QBaseUpdatableEntity _super = new com.sprint5team.monew.base.entity.QBaseUpdatableEntity(this);

    public final NumberPath<Article> article = createNumber("article", Article.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    //inherited
    public final ComparablePath<java.util.UUID> id = _super.id;

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final NumberPath<Long> likeCount = createNumber("likeCount", Long.class);

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    public final NumberPath<User> user = createNumber("user", User.class);

    public QComment(String variable) {
        super(Comment.class, forVariable(variable));
    }

    public QComment(Path<? extends Comment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QComment(PathMetadata metadata) {
        super(Comment.class, metadata);
    }

}

