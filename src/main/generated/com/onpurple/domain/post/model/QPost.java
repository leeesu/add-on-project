package com.onpurple.domain.post.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = 707936719L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final com.onpurple.global.dto.QTimestamped _super = new com.onpurple.global.dto.QTimestamped(this);

    public final EnumPath<com.onpurple.domain.post.category.PostCategory> category = createEnum("category", com.onpurple.domain.post.category.PostCategory.class);

    public final ListPath<com.onpurple.domain.comment.model.Comment, com.onpurple.domain.comment.model.QComment> comments = this.<com.onpurple.domain.comment.model.Comment, com.onpurple.domain.comment.model.QComment>createList("comments", com.onpurple.domain.comment.model.Comment.class, com.onpurple.domain.comment.model.QComment.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    //inherited
    public final StringPath createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final NumberPath<Integer> likes = createNumber("likes", Integer.class);

    //inherited
    public final StringPath modifiedAt = _super.modifiedAt;

    public final StringPath title = createString("title");

    public final com.onpurple.domain.user.model.QUser user;

    public final NumberPath<Integer> view = createNumber("view", Integer.class);

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.onpurple.domain.user.model.QUser(forProperty("user")) : null;
    }

}

