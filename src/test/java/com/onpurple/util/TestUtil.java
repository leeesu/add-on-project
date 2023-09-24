package com.onpurple.util;

import com.onpurple.category.PostCategory;
import com.onpurple.model.Comment;
import com.onpurple.model.Post;
import com.onpurple.model.User;
import org.springframework.stereotype.Component;

@Component
public class TestUtil {
    public static Post createPost(Long id, String title, String content, User user) {
        return Post.builder()
                .id(id)
                .title(title)
                .category(PostCategory.MEET)
                .content(content)
                .user(user)
                .build();
    }

    public static User createUser(Long id, String name, String nickname) {
        return User.builder()
                .id(id)
                .username(name)
                .imageUrl("https://purple.com")
                .nickname(nickname)
                .likes(0)
                .build();
    }

    public static Comment createComment(String content, User user, Post post) {
        return Comment.builder()
                .comment(content)
                .user(user)
                .post(post)
                .build();
    }
}
