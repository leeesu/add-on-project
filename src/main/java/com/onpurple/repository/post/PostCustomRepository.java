package com.project.date.repository.post;

import com.project.date.dto.response.PostResponseDto;
import com.project.date.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PostCustomRepository {

    List<PostResponseDto> findAllByCategorySearch(String keyword);
//    Slice<PostResponseDto> findAllByCategory(String category, Pageable pageable);
//    Slice<PostResponseDto> findAllByCategorySearchScroll(String category, String keyword, Pageable pageable);
}
