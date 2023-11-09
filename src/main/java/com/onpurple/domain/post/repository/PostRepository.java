package com.onpurple.domain.post.repository;


import com.onpurple.domain.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {

}
