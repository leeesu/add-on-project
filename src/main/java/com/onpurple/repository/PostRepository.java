package com.onpurple.repository;


import com.onpurple.category.PostCategory;
import com.onpurple.model.Post;
import com.onpurple.repository.post.PostCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
    // , PostCustomRepository
    List<Post> findAllByCategoryOrderByCreatedAtDesc(PostCategory category);

//    List<Post> findAllByCategoryOrderByCreatedAtDesc(String category);
}
