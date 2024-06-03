package com.dhguo.retrogamesnetwork.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<Token, Integer> {
  Optional<Token> findByToken(String token);

  @Modifying
  @Query(
      """
      DELETE FROM Token token
      WHERE token.user.id = :userId
      """)
  void deleteByUserId(Integer userId);
}
