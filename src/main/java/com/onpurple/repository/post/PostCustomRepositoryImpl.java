package com.project.date.repository.post;


import com.project.date.dto.response.PostResponseDto;
import com.project.date.dto.response.ResponseDto;
import com.project.date.model.Img;
import com.project.date.model.Post;
import com.project.date.model.QPost;
import com.project.date.repository.ImgRepository;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {

    private final ImgRepository imgRepository;
    private final JPAQueryFactory jpaQueryFactory;
    QPost post = QPost.post;

    // 카테고리 검색
    @Override
    public List<PostResponseDto> findAllByCategorySearch(String keyword) {
        List<Post> postResult = jpaQueryFactory
                .selectFrom(post)
                .orderBy(post.createdAt.desc())
                .where(keywordEq(keyword))
                .fetch();

        List<PostResponseDto> responseDtoList = new ArrayList<>();

        for(Post post : postResult){
            List<Img> findImgList = imgRepository.findByPost_Id(post.getId());
            List<String> imgList = new ArrayList<>();
            for (Img img : findImgList) {
                imgList.add(img.getImageUrl());
            }
            responseDtoList.add(PostResponseDto.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .view(post.getView())
                    .likes(post.getLikes())
                    .category(post.getCategory())
                    .nickname(post.getUser().getNickname())
                    .imageUrl(imgList.get(0))
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build());
        }

        return responseDtoList;

    }

//    @Override
//    public Slice<PostResponseDto> findAllByCategory(String category,Pageable pageable) {
//        List<Post> postResult = jpaQueryFactory
//                .selectFrom(post)
//                .offset(pageable.getOffset())
//                .orderBy(post.createdAt.desc())
//                .where(categoryEq(category))
//                .limit(pageable.getPageSize() + 1)
//                .fetch();
//
//        List<PostResponseDto> responseDtoList = new ArrayList<>();
//
//        for(Post post : postResult){
//            responseDtoList.add(PostResponseDto.builder()
//                    .postId(post.getId())
//                    .title(post.getTitle())
//                    .content(post.getContent())
//                    .view(post.getView())
//                    .likes(post.getLikes())
//                    .category(post.getCategory())
//                    .nickname(post.getUser().getNickname())
//                    .imageUrl(post.getImageUrl())
//                    .createdAt(post.getCreatedAt())
//                    .modifiedAt(post.getModifiedAt())
//                    .build());
//        }
//
//        boolean hasNext = false;
//        if(responseDtoList.size() >pageable.getPageSize()) {
//            responseDtoList.remove(pageable.getPageSize());
//            hasNext = true;
//        }
//        return new SliceImpl<>(responseDtoList, pageable, hasNext);
//
//    }
//
//    // 카테고리 검색
//    @Override
//    public Slice<PostResponseDto> findAllByCategorySearchScroll(String category,String keyword, Pageable pageable) {
//        List<Post> postResult = jpaQueryFactory
//                .selectFrom(post)
//                .offset(pageable.getOffset())
//                .orderBy(post.createdAt.desc())
//                .where(categoryEq(category),keywordEq(keyword))
//                .limit(pageable.getPageSize() + 1)
//                .fetch();
//
//        List<PostResponseDto> responseDtoList = new ArrayList<>();
//
//        for(Post post : postResult){
//            responseDtoList.add(PostResponseDto.builder()
//                    .postId(post.getId())
//                    .title(post.getTitle())
//                    .content(post.getContent())
//                    .view(post.getView())
//                    .likes(post.getLikes())
//                    .category(post.getCategory())
//                    .nickname(post.getUser().getNickname())
//                    .imageUrl(post.getImageUrl())
//                    .createdAt(post.getCreatedAt())
//                    .modifiedAt(post.getModifiedAt())
//                    .build());
//        }
//
//        boolean hasNext = false;
//        if(responseDtoList.size() >pageable.getPageSize()) {
//            responseDtoList.remove(pageable.getPageSize());
//            hasNext = true;
//        }
//        return new SliceImpl<>(responseDtoList, pageable, hasNext);
//
//    }


        private BooleanExpression categoryEq(String category) {
            if (category == null) {
                return null;
            } else if (category == "") {
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
