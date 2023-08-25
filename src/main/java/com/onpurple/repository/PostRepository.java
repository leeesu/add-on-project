package com.project.date.repository;


import com.project.date.model.Post;
import com.project.date.repository.post.PostCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository{
    // , PostCustomRepository
    List<Post> findAllByCategoryOrderByCreatedAtDesc(String category);

//    List<Post> findAllByCategoryOrderByCreatedAtDesc(String category);
}
