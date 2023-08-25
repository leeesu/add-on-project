package com.project.date.repository;

import java.util.List;

import com.project.date.model.Comment;
import com.project.date.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findAllByPost(Post post);
}
