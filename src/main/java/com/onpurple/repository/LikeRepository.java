package com.onpurple.repository;




import com.onpurple.model.Likes;
import com.onpurple.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByUserAndPostId(User user, Long postId);

    Optional<Likes> findByUserAndCommentId(User user, Long commentId);

    Optional<Likes> findByUserAndTargetId(User user, Long targetId);

    //나를 좋아요 한 회원
    List<Likes> findByTargetId(Long userId);

    //내가 좋아요 한 회원
    List<Likes> findAllByUser(User user);
    //좋아요수 count
    int countByTargetId(Long targetId);

    @Query(value ="select l.user.id from Likes l where l.user.id in(select l.target.id from Likes l where l.user.id =:userId)")
    List<Integer> likeToLikeUserId(Long userId);
}