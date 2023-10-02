package com.onpurple.repository;


import com.onpurple.category.PostCategory;
import com.onpurple.model.Post;
import com.onpurple.repository.post.PostCustomRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
    // , PostCustomRepository
    List<Post> findAllByCategoryOrderByCreatedAtDesc(PostCategory category);

//    List<Post> findAllByCategoryOrderByCreatedAtDesc(String category);

}
