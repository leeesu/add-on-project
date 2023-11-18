package com.onpurple.domain.like.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLikes is a Querydsl query type for Likes
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLikes extends EntityPathBase<Likes> {

    private static final long serialVersionUID = 1909660900L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLikes likes = new QLikes("likes");

    public final com.onpurple.domain.comment.model.QComment comment;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.onpurple.domain.post.model.QPost post;

    public final com.onpurple.domain.user.model.QUser target;

    public final com.onpurple.domain.user.model.QUser user;

    public QLikes(String variable) {
        this(Likes.class, forVariable(variable), INITS);
    }

    public QLikes(Path<? extends Likes> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLikes(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLikes(PathMetadata metadata, PathInits inits) {
        this(Likes.class, metadata, inits);
    }

    public QLikes(Class<? extends Likes> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.comment = inits.isInitialized("comment") ? new com.onpurple.domain.comment.model.QComment(forProperty("comment"), inits.get("comment")) : null;
        this.post = inits.isInitialized("post") ? new com.onpurple.domain.post.model.QPost(forProperty("post"), inits.get("post")) : null;
        this.target = inits.isInitialized("target") ? new com.onpurple.domain.user.model.QUser(forProperty("target")) : null;
        this.user = inits.isInitialized("user") ? new com.onpurple.domain.user.model.QUser(forProperty("user")) : null;
    }

}

