package com.onpurple.domain.reComment.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.onpurple.domain.comment.model.Comment;
import com.onpurple.domain.reComment.dto.ReCommentRequestDto;
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

public class ReComment extends Timestamped {

    @Id
    @JoinColumn(name = "reCommentId", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "userId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "commentId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Comment comment;

    @Column(nullable = false)
    private String reComment;


    public void update(ReCommentRequestDto reCommentRequestDto) {

        this.reComment = reCommentRequestDto.getReComment();
    }
    public boolean validateUser(User user) {

        return !this.user.equals(user);
    }
}