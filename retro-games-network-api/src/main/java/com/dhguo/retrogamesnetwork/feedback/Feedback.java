package com.dhguo.retrogamesnetwork.feedback;

import com.dhguo.retrogamesnetwork.common.BaseEntity;
import com.dhguo.retrogamesnetwork.game.Game;
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
public class Feedback extends BaseEntity {

  private Double rating;

  private String comment;

  @ManyToOne
  @JoinColumn(name = "game_id")
  private Game game;
}
