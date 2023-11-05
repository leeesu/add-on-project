package com.onpurple.global.img.model;

import com.onpurple.domain.post.model.Post;
import com.onpurple.domain.report.model.Report;
import com.onpurple.domain.user.model.User;
import com.onpurple.global.img.dto.ImageUpdateRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Img {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reportId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Report report;

    public Img(String imageUrl, Post post) {
        this.imageUrl = imageUrl;
        this.post = post;
    }

    public Img(String imageUrl, User user) {
        this.imageUrl = imageUrl;
        this.user = user;
    }

    public Img(String imageUrl, Report report) {
        this.imageUrl = imageUrl;
        this.report = report;
    }

    public void update(ImageUpdateRequestDto requestDto) {
        this.imageUrl = requestDto.getImageUrl();
    }
}