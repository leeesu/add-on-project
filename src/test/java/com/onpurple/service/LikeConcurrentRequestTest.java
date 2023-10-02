package com.onpurple.service;

import com.onpurple.model.Post;
import com.onpurple.model.User;
import com.onpurple.repository.PostRepository;
import com.onpurple.repository.UserRepository;
import com.onpurple.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@SpringBootTest
// 클래스 선언 및 필요한 어노테이션 추가
public class LikeConcurrentRequestTest {

    @Autowired
    private PostRepository postRepository; // Post Repository 추가

    @Autowired
private LikeService likeService; // Like Service 추가

    @Autowired
    private UserRepository userRepository; // User Repository 추가

    @Test
    @DisplayName("10명이 동시에 좋아요 누르는 경우")
    void multi_post_like_two() throws InterruptedException {
        // User 객체 생성 및 저장
        User author = TestUtil.createUser("작성자4", "작성자입니다4");
        author = userRepository.save(author);

        // given
        final Post post = TestUtil.createPosts("제목", "내용", author); // 실제 DB에 저장된 user 사용

        Post savepost = postRepository.save(post);

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            executorService.submit(() -> {
                        try {
                            // 각 스레드별로 새로운 user 생성 및 저장
                            User user = TestUtil.createUser("user3"+ finalI, "user3입니다"+ finalI);
                            user = userRepository.save(user);
                            likeService.postLike(savepost.getId(), user);
                        } finally {
                            latch.countDown();
                        }
                    }
            );
        }
        latch.await();
        Post resultPost = postRepository.findById(post.getId()).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        assertEquals(threadCount, resultPost.getLikes());
    }
}

