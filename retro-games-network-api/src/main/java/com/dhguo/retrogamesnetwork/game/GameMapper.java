package com.dhguo.retrogamesnetwork.game;

import com.dhguo.retrogamesnetwork.history.GameTransactionHistory;
import org.springframework.stereotype.Service;

@Service
public class GameMapper {

  public Game toGame(GameRequest request) {
    return Game.builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .platform(request.getPlatform())
        .publisher(request.getPublisher())
        .archived(false)
        .shareable(false)
        .build();
  }

  public GameResponse toGameResponse(Game game) {
    return GameResponse.builder()
        .id(game.getId())
        .title(game.getTitle())
        .description(game.getDescription())
        .platform(game.getPlatform())
        .publisher(game.getPublisher())
        .rating(game.getRating())
        .archived(game.isArchived())
        .shareable(game.isShareable())
        .owner(game.getOwner().fullName())
        .imageUrl(game.getImageUrl())
        .build();
  }

  public BorrowedGameResponse toBorrowedGameResponse(GameTransactionHistory history) {
    return BorrowedGameResponse.builder()
        .id(history.getGame().getId())
        .title(history.getGame().getTitle())
        .description(history.getGame().getDescription())
        .platform(history.getGame().getPlatform())
        .publisher(history.getGame().getPublisher())
        .rating(history.getGame().getRating())
        .returned(history.isReturned())
        .returnApproved(history.isReturnApproved())
        .build();
  }
}