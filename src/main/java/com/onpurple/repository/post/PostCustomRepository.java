package com.onpurple.repository.post;


import com.onpurple.category.PostCategory;
import com.onpurple.dto.response.PostResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PostCustomRepository {

    Slice<PostResponseDto> findAllByCategory(PostCategory category, Pageable pageable);
    Slice<PostResponseDto> findAllByCategorySearchScroll(PostCategory category, String keyword, Pageable pageable);
}
