package com.onpurple.domain.post.repository;


import com.onpurple.domain.post.category.PostCategory;
import com.onpurple.domain.post.dto.PostResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PostCustomRepository {

    Slice<PostResponseDto> findAllByCategory(PostCategory category, Pageable pageable);
    Slice<PostResponseDto> findAllByCategorySearchScroll(PostCategory category, String keyword, Pageable pageable);
}
