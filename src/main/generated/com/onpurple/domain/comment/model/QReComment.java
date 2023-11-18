package com.onpurple.domain.comment.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReComment is a Querydsl query type for ReComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReComment extends EntityPathBase<ReComment> {

    private static final long serialVersionUID = 1589153236L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReComment reComment1 = new QReComment("reComment1");

    public final com.onpurple.global.dto.QTimestamped _super = new com.onpurple.global.dto.QTimestamped(this);

    public final QComment comment;

    //inherited
    public final StringPath createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final StringPath modifiedAt = _super.modifiedAt;

    public final StringPath reComment = createString("reComment");

    public final com.onpurple.domain.user.model.QUser user;

    public QReComment(String variable) {
        this(ReComment.class, forVariable(variable), INITS);
    }

    public QReComment(Path<? extends ReComment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReComment(PathMetadata metadata, PathInits inits) {
        this(ReComment.class, metadata, inits);
    }

    public QReComment(Class<? extends ReComment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.comment = inits.isInitialized("comment") ? new QComment(forProperty("comment"), inits.get("comment")) : null;
        this.user = inits.isInitialized("user") ? new com.onpurple.domain.user.model.QUser(forProperty("user")) : null;
    }

}

