package com.onpurple.domain.comment.repository;


import com.onpurple.domain.comment.model.Comment;
import com.onpurple.domain.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findAllByPost(Post post);
}
