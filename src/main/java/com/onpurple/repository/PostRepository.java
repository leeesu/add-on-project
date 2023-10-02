package com.onpurple.repository;


import com.onpurple.category.PostCategory;
import com.onpurple.model.Post;
import com.onpurple.repository.post.PostCustomRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Post i where i.id = :id")
    Post findByLockId(long id);


}
