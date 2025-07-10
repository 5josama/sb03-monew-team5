package com.sprint5team.monew.domain.user_interest.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserInterest is a Querydsl query type for UserInterest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserInterest extends EntityPathBase<UserInterest> {

    private static final long serialVersionUID = 1015825177L;

    public static final QUserInterest userInterest = new QUserInterest("userInterest");

    public final com.sprint5team.monew.base.entity.QBaseEntity _super = new com.sprint5team.monew.base.entity.QBaseEntity(this);

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    //inherited
    public final ComparablePath<java.util.UUID> id = _super.id;

    public QUserInterest(String variable) {
        super(UserInterest.class, forVariable(variable));
    }

    public QUserInterest(Path<? extends UserInterest> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserInterest(PathMetadata metadata) {
        super(UserInterest.class, metadata);
    }

}

