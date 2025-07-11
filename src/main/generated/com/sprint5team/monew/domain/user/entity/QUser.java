package com.sprint5team.monew.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1490700598L;

    public static final QUser user = new QUser("user");

    public final com.sprint5team.monew.base.entity.QBaseEntity _super = new com.sprint5team.monew.base.entity.QBaseEntity(this);

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final StringPath email = createString("email");

    //inherited
    public final ComparablePath<java.util.UUID> id = _super.id;

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

