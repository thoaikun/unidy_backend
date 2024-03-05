package com.unidy.backend.domains.entity;

import com.unidy.backend.domains.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "token")

public class Token {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  public Integer id;

  @Column(name = "token", unique = true)
  public String token;

  @Column(name = "refresh_token", unique = true)
  public String refreshToken;

  @Enumerated(EnumType.STRING)
  public TokenType tokenType = TokenType.BEARER;

  @Column(name = "revoked")
  public boolean revoked;

  @Column(name = "expired")
  public boolean expired;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;
}
