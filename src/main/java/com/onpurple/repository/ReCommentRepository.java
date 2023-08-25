package com.project.date.repository;

import com.project.date.model.Comment;
import com.project.date.model.Post;
import com.project.date.model.ReComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReCommentRepository extends JpaRepository<ReComment,Long> {
    List<ReComment> findAllByComment(Comment comment);
}
