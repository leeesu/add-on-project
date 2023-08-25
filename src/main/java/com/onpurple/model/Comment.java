package com.project.date.model;

import javax.persistence.*;

import com.project.date.dto.request.CommentRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment {

  @Id
  @JoinColumn(name = "commentId", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "userId", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @JoinColumn(name = "postId", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Post post;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ReComment> reComments;
  @Column(nullable = false)
  private String comment;

  @Column
  private int likes;

  @Column // 생성일자임을 나타냅니다.
  private String createdAt;

  @Column // 마지막 수정일자임을 나타냅니다.
  private String modifiedAt;

  public void update(CommentRequestDto commentRequestDto) {
    this.comment = commentRequestDto.getComment();
  }

  public void updateModified(String getTime){
    this.modifiedAt = getTime;
  }

  public boolean validateUser(User user) {
    return !this.user.equals(user);
  }

  public void addLike(){
    this.likes+=1;
  }

  public void minusLike(){
    this.likes-=1;
  }
}
