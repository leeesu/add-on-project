package com.onpurple.repository;


import com.onpurple.model.Comment;
import com.onpurple.model.ReComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReCommentRepository extends JpaRepository<ReComment,Long> {
    List<ReComment> findAllByComment(Comment comment);
}
