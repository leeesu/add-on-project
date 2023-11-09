package com.onpurple.domain.report.model;


import com.onpurple.domain.report.dto.ReportRequestDto;
import com.onpurple.domain.report.category.ReportCategory;
import com.onpurple.domain.user.model.User;
import com.onpurple.global.dto.Timestamped;
import jakarta.persistence.*;
import lombok.*;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Report extends Timestamped {

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

    @Column(nullable = false)
    private String reportNickname;

    @Enumerated(EnumType.STRING)
    private ReportCategory category;


    @Column
    private String imageUrl;



    @JoinColumn(name = "userId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;


    // 회원정보 검증
    public boolean validateUser(User user) {

        return !this.user.equals(user);
    }

    //리스트 첫번째 이미지 저장
    public void imageSave(String imageUrl){
        this.imageUrl = imageUrl;
    }


    public void update(ReportRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.category= requestDto.getCategory();
    }
}
