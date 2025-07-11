package com.sprint5team.monew.domain.notification.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotification is a Querydsl query type for Notification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotification extends EntityPathBase<Notification> {

    private static final long serialVersionUID = -255628534L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotification notification = new QNotification("notification");

    public final com.sprint5team.monew.base.entity.QBaseUpdatableEntity _super = new com.sprint5team.monew.base.entity.QBaseUpdatableEntity(this);

    public final com.sprint5team.monew.domain.comment.entity.QComment comment;

    public final BooleanPath confirmed = createBoolean("confirmed");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    //inherited
    public final ComparablePath<java.util.UUID> id = _super.id;

    public final com.sprint5team.monew.domain.interest.entity.QInterest interest;

    public final EnumPath<ResourceType> resourceType = createEnum("resourceType", ResourceType.class);

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    public final com.sprint5team.monew.domain.user.entity.QUser user;

    public QNotification(String variable) {
        this(Notification.class, forVariable(variable), INITS);
    }

    public QNotification(Path<? extends Notification> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotification(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotification(PathMetadata metadata, PathInits inits) {
        this(Notification.class, metadata, inits);
    }

    public QNotification(Class<? extends Notification> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.comment = inits.isInitialized("comment") ? new com.sprint5team.monew.domain.comment.entity.QComment(forProperty("comment"), inits.get("comment")) : null;
        this.interest = inits.isInitialized("interest") ? new com.sprint5team.monew.domain.interest.entity.QInterest(forProperty("interest")) : null;
        this.user = inits.isInitialized("user") ? new com.sprint5team.monew.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

