package com.onpurple.repository.post;


import com.onpurple.dto.response.PostResponseDto;

import java.util.List;

public interface PostCustomRepository {

    List<PostResponseDto> findAllByCategorySearch(String keyword);
//    Slice<PostResponseDto> findAllByCategory(String category, Pageable pageable);
//    Slice<PostResponseDto> findAllByCategorySearchScroll(String category, String keyword, Pageable pageable);
}
