package com.onpurple.domain.like.model;

import com.onpurple.domain.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UnLike {

    @Id
    @Column(name = "unLikeId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "targetId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User target;



}
