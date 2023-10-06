package com.onpurple.external;

import com.onpurple.category.PostCategory;
import com.onpurple.dto.request.PostRequestDto;
import com.onpurple.model.Comment;
import com.onpurple.model.Likes;
import com.onpurple.model.Post;
import com.onpurple.model.User;
import org.springframework.stereotype.Component;

@Component
public class TestUtil {

    public static User createUser(String name, String nickname) {
        return User.builder()
                .password("1234567890")
                .username(name)
                .imageUrl("https://purple.com")
                .nickname(nickname)
                .age(22)
                .introduction("안녕하세요")
                .gender("남자")
                .mbti("IIII")
                .likes(0)
                .unLike(0)
                .build();
    }
    public static PostRequestDto createPost(String title, String content) {
        return PostRequestDto.builder()
                .title(title)
                .category(PostCategory.MEET)
                .content(content)
                .build();
    }

    public static Post createPosts(String title, String content, User author) {
        return Post.builder()
                .title(title)
                .likes(0)
                .user(author) // 실제 DB에 저장된 user 사용
                .content(content)
                .imageUrl("이미지")
                .build();
    }

    public static Comment createComment(String content, User user, Post post) {
        return Comment.builder()
                .comment(content)
                .user(user)
                .post(post)
                .build();
    }

    public static Likes createLikes(User user, Post post) {
        return Likes.builder()
                .user(user)
                .post(post)
                .build();
    }
}
