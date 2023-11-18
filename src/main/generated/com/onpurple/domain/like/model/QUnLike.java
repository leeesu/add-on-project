package com.onpurple.domain.like.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUnLike is a Querydsl query type for UnLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUnLike extends EntityPathBase<UnLike> {

    private static final long serialVersionUID = -668694104L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUnLike unLike = new QUnLike("unLike");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.onpurple.domain.user.model.QUser target;

    public final com.onpurple.domain.user.model.QUser user;

    public QUnLike(String variable) {
        this(UnLike.class, forVariable(variable), INITS);
    }

    public QUnLike(Path<? extends UnLike> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUnLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUnLike(PathMetadata metadata, PathInits inits) {
        this(UnLike.class, metadata, inits);
    }

    public QUnLike(Class<? extends UnLike> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.target = inits.isInitialized("target") ? new com.onpurple.domain.user.model.QUser(forProperty("target")) : null;
        this.user = inits.isInitialized("user") ? new com.onpurple.domain.user.model.QUser(forProperty("user")) : null;
    }

}

