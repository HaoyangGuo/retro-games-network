package com.dhguo.retrogamesnetwork.game;

import com.dhguo.retrogamesnetwork.common.PageResponse;
import com.dhguo.retrogamesnetwork.exception.OperationNotPermittedException;
import com.dhguo.retrogamesnetwork.history.GameTransactionHistory;
import com.dhguo.retrogamesnetwork.history.GameTransactionHistoryRepository;
import com.dhguo.retrogamesnetwork.user.User;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class GameService {

  private final GameMapper gameMapper;
  private final GameRepository gameRepository;
  private final GameTransactionHistoryRepository gameTransactionHistoryRepository;
  private final S3Client s3Client;

  public Integer save(GameRequest request, Authentication connectedUser) {
    User user = ((User) connectedUser.getPrincipal());

    Game game = gameMapper.toGame(request);
    game.setOwner(user);

    return gameRepository.save(game).getId();
  }

  public GameResponse findById(Integer gameId) {
    return gameRepository
        .findById(gameId)
        .map(gameMapper::toGameResponse)
        .orElseThrow(
            () -> new EntityNotFoundException("No game with the ID: " + gameId + " was found."));
  }

  public PageResponse<GameResponse> findAllGames(int page, int size, Authentication connectedUser) {
    User user = ((User) connectedUser.getPrincipal());
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    Page<Game> games = gameRepository.findAllDisplayableGames(pageable, user.getId());
    List<GameResponse> gameResponseList = games.stream().map(gameMapper::toGameResponse).toList();

    return new PageResponse<>(
        gameResponseList,
        games.getNumber(),
        games.getSize(),
        games.getTotalElements(),
        games.getTotalPages(),
        games.isFirst(),
        games.isLast());
  }

  public PageResponse<GameResponse> findAllGamesByOwner(
      int page, int size, Authentication connectedUser) {
    User user = ((User) connectedUser.getPrincipal());
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    Page<Game> games = gameRepository.findAllDisplayableGamesByOwner(pageable, user.getId());
    List<GameResponse> gameResponseList = games.stream().map(gameMapper::toGameResponse).toList();
    return new PageResponse<>(
        gameResponseList,
        games.getNumber(),
        games.getSize(),
        games.getTotalElements(),
        games.getTotalPages(),
        games.isFirst(),
        games.isLast());
  }

  public PageResponse<BorrowedGameResponse> findAllBorrowedGames(
      int page, int size, Authentication connectedUser) {
    User user = ((User) connectedUser.getPrincipal());
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    Page<GameTransactionHistory> borrowedBooks =
        gameTransactionHistoryRepository.findAllBorrowedGames(pageable, user.getId());
    List<BorrowedGameResponse> borrowedGameResponseList =
        borrowedBooks.stream().map(gameMapper::toBorrowedGameResponse).toList();
    return new PageResponse<>(
        borrowedGameResponseList,
        borrowedBooks.getNumber(),
        borrowedBooks.getSize(),
        borrowedBooks.getTotalElements(),
        borrowedBooks.getTotalPages(),
        borrowedBooks.isFirst(),
        borrowedBooks.isLast());
  }

  public PageResponse<BorrowedGameResponse> findAllReturnedGames(
      int page, int size, Authentication connectedUser) {
    User user = ((User) connectedUser.getPrincipal());
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    Page<GameTransactionHistory> borrowedBooks =
        gameTransactionHistoryRepository.findAllReturnedGames(pageable, user.getId());
    List<BorrowedGameResponse> borrowedGameResponseList =
        borrowedBooks.stream().map(gameMapper::toBorrowedGameResponse).toList();
    return new PageResponse<>(
        borrowedGameResponseList,
        borrowedBooks.getNumber(),
        borrowedBooks.getSize(),
        borrowedBooks.getTotalElements(),
        borrowedBooks.getTotalPages(),
        borrowedBooks.isFirst(),
        borrowedBooks.isLast());
  }

  public Integer updateShareableStatus(Integer gameId, Authentication connectedUser) {
    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException("No game with the ID: " + gameId + " was found."));
    User user = ((User) connectedUser.getPrincipal());

    if (!Objects.equals(game.getOwner().getId(), user.getId())) {
      throw new OperationNotPermittedException("You are not authorized.");
    }

    game.setShareable(!game.isShareable());
    gameRepository.save(game);

    return game.getId();
  }

  public Integer updateArchivedStatus(Integer gameId, Authentication connectedUser) {
    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException("No game with the ID: " + gameId + " was found."));
    User user = ((User) connectedUser.getPrincipal());

    if (!Objects.equals(game.getOwner().getId(), user.getId())) {
      throw new OperationNotPermittedException("You are not authorized.");
    }

    game.setArchived(!game.isArchived());
    gameRepository.save(game);

    return game.getId();
  }

  public Integer borrowGame(Integer gameId, Authentication connectedUser) {
    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException("No game with the ID: " + gameId + " was found."));
    if (game.isArchived() || !game.isShareable()) {
      throw new OperationNotPermittedException("Game is not available.");
    }
    User user = (User) connectedUser.getPrincipal();

    if (Objects.equals(game.getOwner().getId(), user.getId())) {
      throw new OperationNotPermittedException("You cannot borrow your own game.");
    }

    final boolean isAlreadyBorrowed =
        gameTransactionHistoryRepository.isAlreadyBorrowedByUser(gameId, user.getId());
    if (isAlreadyBorrowed) {
      throw new OperationNotPermittedException("Game is not available.");
    }

    GameTransactionHistory gameTransactionHistory =
        GameTransactionHistory.builder()
            .user(user)
            .game(game)
            .returnApproved(false)
            .returnApproved(false)
            .build();

    // TODO: test this part
    game.setShareable(false);
    gameRepository.save(game);

    return gameTransactionHistoryRepository.save(gameTransactionHistory).getId();
  }

  public Integer returnGame(Integer gameId, Authentication connectedUser) {
    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException("No game with the ID: " + gameId + " was found."));

    // TODO: test if !game.isShareable() is needed
    if (game.isArchived()) {
      throw new OperationNotPermittedException("Game is not available.");
    }

    User user = (User) connectedUser.getPrincipal();

    if (Objects.equals(game.getOwner().getId(), user.getId())) {
      throw new OperationNotPermittedException("You cannot return your own game.");
    }

    GameTransactionHistory gameTransactionHistory =
        gameTransactionHistoryRepository
            .findByGameIdAndUserId(gameId, user.getId())
            .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book."));

    gameTransactionHistory.setReturned(true);

    return gameTransactionHistoryRepository.save(gameTransactionHistory).getId();
  }

  public Integer approveGameReturn(Integer gameId, Authentication connectedUser) {
    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException("No game with the ID: " + gameId + " was found."));

    // TODO: test if !game.isShareable() is needed
    if (game.isArchived()) {
      throw new OperationNotPermittedException("Game is not available.");
    }

    User user = (User) connectedUser.getPrincipal();

    if (Objects.equals(game.getOwner().getId(), user.getId())) {
      throw new OperationNotPermittedException("You cannot return your own game.");
    }

    GameTransactionHistory gameTransactionHistory =
        gameTransactionHistoryRepository
            .findByGameIdAndOwnerId(gameId, user.getId())
            .orElseThrow(() -> new OperationNotPermittedException("You cannot"));
    gameTransactionHistory.setReturnApproved(true);

    return gameTransactionHistoryRepository.save(gameTransactionHistory).getId();
  }

  public void uploadImage(MultipartFile image, Authentication connectedUser, Integer gameId) {
    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException("No game with the ID: " + gameId + " was found."));

    User user = (User) connectedUser.getPrincipal();

    if (!Objects.equals(game.getOwner().getId(), user.getId())) {
      throw new OperationNotPermittedException("You cannot modify this game.");
    }

    String uniqueFilename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
    PutObjectRequest objectRequest =
        PutObjectRequest.builder()
            .bucket("dhguo-personal-projects-bucket")
            .key("retro-games-network-images/" + uniqueFilename)
            .build();

    byte[] imageBytes;
    try (InputStream inputStream = image.getInputStream()) {
      imageBytes = inputStream.readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException("Error reading image file", e);
    }
    s3Client.putObject(objectRequest, RequestBody.fromBytes(imageBytes));

    if (game.getImageUrl() != null) {
      int imageNameIndex = game.getImageUrl().lastIndexOf('/');
      String imageName = game.getImageUrl().substring(imageNameIndex + 1);

      DeleteObjectRequest deleteObjectRequest =
          DeleteObjectRequest.builder()
              .bucket("dhguo-personal-projects-bucket")
              .key("retro-games-network-images/" + imageName)
              .build();
      s3Client.deleteObject(deleteObjectRequest);
    }

    game.setImageUrl("d2f5vme40jc1ky.cloudfront.net/retro-games-network-images/" + uniqueFilename);
    gameRepository.save(game);
  }
}
