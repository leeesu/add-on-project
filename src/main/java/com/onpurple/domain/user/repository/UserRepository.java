package com.onpurple.domain.user.repository;


import com.onpurple.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long userId);
    Optional<User> findByUsername(String username);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByKakaoId(Long kakaoId);

    //매칭된 회원 정보 가져오기
    @Query(value = "SELECT * FROM user_table WHERE user_id IN (:likeList)", nativeQuery = true)
    List<User> matchingUser(List<Integer> likeList);

}