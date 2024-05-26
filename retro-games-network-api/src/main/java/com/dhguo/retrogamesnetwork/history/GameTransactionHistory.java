package com.dhguo.retrogamesnetwork.history;

import com.dhguo.retrogamesnetwork.common.BaseEntity;
import com.dhguo.retrogamesnetwork.game.Game;
import com.dhguo.retrogamesnetwork.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GameTransactionHistory extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "game_id")
  private Game game;

  private boolean returned;

  private boolean returnApproved;
}
