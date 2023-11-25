package com.onpurple.domain.comment.repository;


import com.onpurple.domain.comment.model.Comment;
import com.onpurple.domain.post.model.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findAllByPost(Post post);

  @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.post = :post")
  List<Comment> findAllByPostWithUser(@Param("post") Post post);
}
