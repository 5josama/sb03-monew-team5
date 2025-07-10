package com.sprint5team.monew.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserEntity is a Querydsl query type for UserEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserEntity extends EntityPathBase<User> {

    private static final long serialVersionUID = -550320307L;

    public static final QUserEntity userEntity = new QUserEntity("userEntity");

    public final com.sprint5team.monew.base.entity.QBaseEntity _super = new com.sprint5team.monew.base.entity.QBaseEntity(this);

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final StringPath email = createString("email");

    //inherited
    public final ComparablePath<java.util.UUID> id = _super.id;

    public final BooleanPath is_deleted = createBoolean("is_deleted");

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public QUserEntity(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUserEntity(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserEntity(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

