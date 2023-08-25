package com.onpurple.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RefreshToken {

  @Id
  @Column(nullable = false)
  private Long id;

  @JoinColumn(name = "user_id", nullable = false)
  @OneToOne(fetch = FetchType.LAZY)
  private User user;

  @Column(nullable = false)
  private String value;

  public void updateValue(String token) {
    this.value = token;
  }
}
