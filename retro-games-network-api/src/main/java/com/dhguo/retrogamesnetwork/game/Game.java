package com.dhguo.retrogamesnetwork.game;

import com.dhguo.retrogamesnetwork.common.BaseEntity;
import com.dhguo.retrogamesnetwork.feedback.Feedback;
import com.dhguo.retrogamesnetwork.history.GameTransactionHistory;
import com.dhguo.retrogamesnetwork.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import java.util.List;
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
public class Game extends BaseEntity {

  private String title;

  private String description;

  private String imageUrl;

  private String platform;

  private String publisher;

  private boolean archived;

  private boolean shareable;

  @ManyToOne
  @JoinColumn(name = "owner_id")
  private User owner;

  @OneToMany(mappedBy = "game")
  private List<Feedback> feedbacks;

  @OneToMany(mappedBy = "game")
  private List<GameTransactionHistory> histories;

  @Transient
  public double getRating() {
    if (feedbacks == null || feedbacks.isEmpty()) {
      return 0.0;
    }

    var rating = feedbacks.stream().mapToDouble(Feedback::getRating).average().orElse(0.0);

    return Math.round(rating * 10.0) / 10.0;
  }
}
