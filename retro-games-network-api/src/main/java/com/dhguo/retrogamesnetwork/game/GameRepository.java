package com.dhguo.retrogamesnetwork.game;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GameRepository extends JpaRepository<Game, Integer> {

  @Query(
      """
      SELECT game
      FROM Game game
      WHERE game.archived = false
      AND game.shareable = true
      AND game.owner.id != :userId
      """)
  Page<Game> findAllDisplayableGames(Pageable pageable, Integer userId);

  @Query(
      """
      SELECT game
      FROM Game game
      WHERE game.owner.id = :userId
      """)
  Page<Game> findAllDisplayableGamesByOwner(Pageable pageable, Integer userId);
}
