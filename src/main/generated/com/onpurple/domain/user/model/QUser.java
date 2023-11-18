package com.onpurple.domain.user.model;

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

    private static final long serialVersionUID = 1483155695L;

    public static final QUser user = new QUser("user");

    public final com.onpurple.global.dto.QTimestamped _super = new com.onpurple.global.dto.QTimestamped(this);

    public final NumberPath<Integer> age = createNumber("age", Integer.class);

    public final StringPath area = createString("area");

    //inherited
    public final StringPath createdAt = _super.createdAt;

    public final StringPath drink = createString("drink");

    public final StringPath gender = createString("gender");

    public final StringPath hobby = createString("hobby");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath idealType = createString("idealType");

    public final StringPath imageUrl = createString("imageUrl");

    public final StringPath introduction = createString("introduction");

    public final StringPath job = createString("job");

    public final NumberPath<Long> kakaoId = createNumber("kakaoId", Long.class);

    public final StringPath likeMovieType = createString("likeMovieType");

    public final NumberPath<Integer> likes = createNumber("likes", Integer.class);

    public final StringPath mbti = createString("mbti");

    //inherited
    public final StringPath modifiedAt = _super.modifiedAt;

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final StringPath pet = createString("pet");

    public final NumberPath<Integer> reportCount = createNumber("reportCount", Integer.class);

    public final EnumPath<com.onpurple.global.role.Authority> role = createEnum("role", com.onpurple.global.role.Authority.class);

    public final StringPath smoke = createString("smoke");

    public final NumberPath<Integer> unLike = createNumber("unLike", Integer.class);

    public final StringPath username = createString("username");

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

