package com.dhguo.retrogamesnetwork.history;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GameTransactionHistoryRepository
    extends JpaRepository<GameTransactionHistory, Integer> {

  @Query(
      """
      SELECT history
      FROM GameTransactionHistory history
      WHERE history.user.id = :userId
      """)
  Page<GameTransactionHistory> findAllBorrowedGames(Pageable pageable, Integer userId);

  @Query(
      """
      SELECT history
      FROM GameTransactionHistory history
      WHERE history.game.owner.id = :userId
      """)
  Page<GameTransactionHistory> findAllReturnedGames(Pageable pageable, Integer userId);

  @Query(
      """
      SELECT (COUNT(*) > 0) AS isBorrowed
      FROM GameTransactionHistory gameTransactionHistory
      WHERE gameTransactionHistory.user.id = :userId
      AND gameTransactionHistory.game.id = :gameId
      AND gameTransactionHistory.returnApproved = false
      AND gameTransactionHistory.returned = false
      """)
  boolean isAlreadyBorrowedByUser(Integer gameId, Integer userId);

  @Query(
      """
      SELECT history
      FROM GameTransactionHistory history
      WHERE history.game.id = :gameId
      AND history.user.id = :userId
      AND history.returned = false
      AND history.returnApproved = false
      """)
  Optional<GameTransactionHistory> findByGameIdAndUserId(Integer gameId, Integer userId);

  @Query(
      """
      SELECT history
      FROM GameTransactionHistory history
      WHERE history.game.id = :gameId
      AND history.game.owner.id = :ownerId
      AND history.returned = true
      AND history.returnApproved = false
      """)
  Optional<GameTransactionHistory> findByGameIdAndOwnerId(Integer gameId, Integer ownerId);
}
