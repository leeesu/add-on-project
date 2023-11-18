package com.onpurple.global.img.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QImg is a Querydsl query type for Img
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QImg extends EntityPathBase<Img> {

    private static final long serialVersionUID = 1186824686L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QImg img = new QImg("img");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final com.onpurple.domain.post.model.QPost post;

    public final com.onpurple.domain.report.model.QReport report;

    public final com.onpurple.domain.user.model.QUser user;

    public QImg(String variable) {
        this(Img.class, forVariable(variable), INITS);
    }

    public QImg(Path<? extends Img> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QImg(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QImg(PathMetadata metadata, PathInits inits) {
        this(Img.class, metadata, inits);
    }

    public QImg(Class<? extends Img> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new com.onpurple.domain.post.model.QPost(forProperty("post"), inits.get("post")) : null;
        this.report = inits.isInitialized("report") ? new com.onpurple.domain.report.model.QReport(forProperty("report"), inits.get("report")) : null;
        this.user = inits.isInitialized("user") ? new com.onpurple.domain.user.model.QUser(forProperty("user")) : null;
    }

}

