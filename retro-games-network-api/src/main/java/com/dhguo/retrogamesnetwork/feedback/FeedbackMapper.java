package com.dhguo.retrogamesnetwork.feedback;

import com.dhguo.retrogamesnetwork.game.Game;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class FeedbackMapper {

  public Feedback toFeedback(FeedbackRequest request) {
    return Feedback.builder()
        .rating(request.getRating())
        .comment(request.getComment())
        .game(Game.builder().id(request.gameId).shareable(false).archived(false).build())
        .build();
  }

  public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer userId) {
    return FeedbackResponse.builder()
        .rating(feedback.getRating())
        .comment(feedback.getComment())
        .ownFeedback(Objects.equals(feedback.getCreatedBy(), userId))
        .build();
  }
}
