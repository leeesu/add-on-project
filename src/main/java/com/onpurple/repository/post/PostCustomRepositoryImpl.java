package com.onpurple.repository.post;


import com.onpurple.category.PostCategory;
import com.onpurple.dto.response.PostResponseDto;
import com.onpurple.model.Post;
import com.onpurple.model.QPost;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    QPost post = QPost.post;

    @Override
    public Slice<PostResponseDto> findAllByCategory(PostCategory category, Pageable pageable) {
        List<Post> postResult = jpaQueryFactory
                .selectFrom(post)
                .offset(pageable.getOffset())
                .orderBy(post.createdAt.desc())
                .where(categoryEq(category))
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<PostResponseDto> responseDtoList = postResult.stream()
                .map(PostResponseDto::GetAllFromEntity)
                .collect(Collectors.toList());

        boolean hasNext = false;
        if(responseDtoList.size() >pageable.getPageSize()) {
            responseDtoList.remove(pageable.getPageSize()-1); // 마지막 항목 제거
            hasNext = true;
        }
        return new SliceImpl<>(responseDtoList, pageable, hasNext);

    }

    // 카테고리 검색
    @Override
    public Slice<PostResponseDto> findAllByCategorySearchScroll(PostCategory category,String keyword, Pageable pageable) {
        List<Post> postResult = jpaQueryFactory
                .selectFrom(post)
                .offset(pageable.getOffset())
                .orderBy(post.createdAt.desc())
                .where(categoryEq(category),keywordEq(keyword))
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<PostResponseDto> responseDtoList = postResult.stream()
                .map(PostResponseDto::GetAllFromEntity)
                .collect(Collectors.toList());

        boolean hasNext = false;
        if(responseDtoList.size() >pageable.getPageSize()) {
            responseDtoList.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(responseDtoList, pageable, hasNext);

    }


    private BooleanExpression categoryEq(PostCategory category) {
        if (category == null) {
            return null;
        }
        return post.category.eq(category);
    }

        private BooleanExpression keywordEq(String keyword) {
            if(keyword == null) {
                return null;
            }else if (keyword == ""){
                return null;
            }
            return (post.title.contains(keyword));
            //.or(post.content.contains(keyword)
        }
}
