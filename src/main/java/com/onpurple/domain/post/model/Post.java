package com.onpurple.domain.post.model;

import com.onpurple.domain.comment.model.Comment;
import com.onpurple.domain.post.dto.PostRequestDto;
import com.onpurple.domain.post.category.PostCategory;
import com.onpurple.domain.user.model.User;
import com.onpurple.global.dto.Timestamped;
import com.onpurple.global.img.model.Img;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Post", indexes = {
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_title_category", columnList = "title,category")
})
public class Post extends Timestamped {

    @Id
    @Column(name = "postId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //게시글제목
    @Column(nullable = false)
    private String title;
    //게시글내용
    @Column(nullable = false)
    private String content;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;


    @Transient
    @OneToMany(fetch = FetchType.LAZY)
    private final List<Img> imageList = new ArrayList<>();

    @JoinColumn(name = "userId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    //좋아요 count
    @Column(nullable = false)
    private int likes;
    //조회수 count
    @Column(nullable = false)
    private int view;
    /* 카테고리
    맛집 taste,
    데이트 dateCourse,
    번개 meet,
    술집 bar,
    드라이브 drive
     */
    @Enumerated(EnumType.STRING)
    private PostCategory category;
    //단일 게시글 리스트 이미지 처리를 위한 컬럼
    @Column
    private String imageUrl;


    public void update(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.category= requestDto.getCategory();
    }

    // 회원정보 검증
    public boolean validateUser(User user) {

        return !this.user.equals(user);
    }
    //좋아요
    public void increasePostLike(){

        this.likes +=1;
    }
    //좋아요 취소
    public void cancelPostLike(){
        this.likes -=1;
    }
    //조회수 증가
    public void increasePostView(){
        this.view +=1;
    }

    public void saveImage(String s) {
        this.imageUrl = s;
    }
}


