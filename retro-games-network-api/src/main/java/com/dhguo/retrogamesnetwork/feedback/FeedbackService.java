package com.dhguo.retrogamesnetwork.feedback;

import com.dhguo.retrogamesnetwork.common.PageResponse;
import com.dhguo.retrogamesnetwork.exception.OperationNotPermittedException;
import com.dhguo.retrogamesnetwork.game.Game;
import com.dhguo.retrogamesnetwork.game.GameRepository;
import com.dhguo.retrogamesnetwork.user.User;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {

  private final FeedbackRepository feedbackRepository;
  private final GameRepository gameRepository;
  private final FeedbackMapper feedbackMapper;

  public Integer save(FeedbackRequest request, Authentication connectedUser) {
    Game game =
        gameRepository
            .findById(request.getGameId())
            .orElseThrow(
                () ->
                    new EntityNotFoundException("No game with the ID: " + request.gameId + " was found."));

    // TODO: test if !game.isShareable() is needed
    if (game.isArchived()) {
      throw new OperationNotPermittedException("You cannot give feedback to an archived game");
    }

    User user = (User) connectedUser.getPrincipal();
    if (Objects.equals(game.getOwner().getId(), user.getId())) {
      throw new OperationNotPermittedException("You cannot give feedback to a game you own.");
    }

    Feedback feedback = feedbackMapper.toFeedback(request);
    return feedbackRepository.save(feedback).getId();
  }

  public PageResponse<FeedbackResponse> findAllFeedbacksByGame(Integer gameId, int page, int size, Authentication connectedUser) {
    Pageable pageable = PageRequest.of(page, size);
    User user = (User) connectedUser.getPrincipal();
    Page<Feedback> feedbacks = feedbackRepository.findAllByGameId(gameId, pageable);
    List<FeedbackResponse> feedbackResponseList = feedbacks.stream().map(
        f -> feedbackMapper.toFeedbackResponse(f, user.getId())
    ).toList();

    return new PageResponse<>(
        feedbackResponseList,
        feedbacks.getNumber(),
        feedbacks.getSize(),
        feedbacks.getTotalElements(),
        feedbacks.getTotalPages(),
        feedbacks.isFirst(),
        feedbacks.isLast()
    );
  }
}
