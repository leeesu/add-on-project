package com.onpurple.repository;


import com.onpurple.model.UnLike;
import com.onpurple.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnLikeRepository extends JpaRepository<UnLike, Long> {
    Optional<UnLike> findByUserAndTargetId(User user, Long targetId);

    List<UnLike> findAllByUser(User user);
    //싫어요수 count
    int countByTargetId(Long targetId);
}
