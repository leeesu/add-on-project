package com.onpurple.domain.comment.repository;


import com.onpurple.domain.comment.model.Comment;
import com.onpurple.domain.comment.model.ReComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReCommentRepository extends JpaRepository<ReComment,Long> {
    List<ReComment> findAllByComment(Comment comment);
}
