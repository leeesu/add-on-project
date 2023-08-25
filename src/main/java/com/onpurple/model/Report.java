package com.project.date.model;

import com.project.date.dto.request.ReportRequestDto;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Report extends Timestamped{

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

    @Column
    private String category;


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
