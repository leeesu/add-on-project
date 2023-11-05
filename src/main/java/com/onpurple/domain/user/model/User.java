package com.onpurple.domain.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onpurple.domain.profile.dto.ProfileUpdateRequestDto;
import com.onpurple.domain.user.dto.UserUpdateRequestDto;

import com.onpurple.global.dto.Timestamped;
import com.onpurple.global.img.dto.ImageUpdateRequestDto;
import com.onpurple.global.img.model.Img;
import com.onpurple.global.role.Authority;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Table(name = "USER_TABLE")
public class User extends Timestamped {

    @Id
    @Column(name = "userId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column
    private String imageUrl;

    @Enumerated(value = EnumType.STRING)
    private Authority role;

    @Column(nullable = false)
    private String gender;

    @Column (unique = true)
    private Long kakaoId;

    //img
    @Transient
    @OneToMany(fetch = FetchType.LAZY)
    private final List<Img> imgList = new ArrayList<>();

    //좋아요 count
    @Column(nullable = false)
    private int likes;

    //싫어요 count
    @Column(nullable = false)
    private int unLike;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private String mbti;

    @Column(nullable = false)
    private String introduction;

    @Column
    private String idealType;

    @Column
    private String job;

    @Column
    private String hobby;

    @Column
    private String drink;

    @Column
    private String pet;

    @Column
    private String smoke;

    @Column
    private String likeMovieType;

    @Column
    private String area;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean validatePassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.matches(password, this.password);
    }

    public void update(UserUpdateRequestDto requestDto) {
        this.password = requestDto.getPassword();
    }

    public void update(ImageUpdateRequestDto requestDto) {
        this.imageUrl = requestDto.getImageUrl();
    }

    //프로필 업데이트
    public void update(ProfileUpdateRequestDto requestDto) {
        this.introduction = requestDto.getIntroduction();
        this.idealType = requestDto.getIdealType();
        this.job = requestDto.getJob();
        this.hobby = requestDto.getHobby();
        this.drink = requestDto.getDrink();
        this.pet = requestDto.getPet();
        this.smoke = requestDto.getSmoke();
        this.likeMovieType = requestDto.getLikeMovieType();
        this.area = requestDto.getArea();

    }
    //리스트 첫번째 이미지 저장
    public void imageSave(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public void increaseUserLike(){

        this.likes +=1;
    }

    public void cancelUserLike(){

        this.likes -=1;
    }
    public void increaseUserUnLike(){
        this.unLike +=1;
    }

    public void cancelUserUnLike(){

        this.unLike -=1;
    }


}