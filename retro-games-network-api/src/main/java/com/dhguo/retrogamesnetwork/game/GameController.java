package com.dhguo.retrogamesnetwork.game;

import com.dhguo.retrogamesnetwork.common.PageResponse;
import com.dhguo.retrogamesnetwork.exception.InvalidImageException;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("games")
@RequiredArgsConstructor
@Tag(name = "Game")
public class GameController {
  private final GameService gameService;

  @PostMapping
  public ResponseEntity<Integer> saveGame(
      @Valid @RequestBody GameRequest request, Authentication connectedUser) {
    return ResponseEntity.ok(gameService.save(request, connectedUser));
  }

  @GetMapping("{game-id}")
  public ResponseEntity<GameResponse> findGameById(@PathVariable("game-id") Integer gameId) {
    return ResponseEntity.ok(gameService.findById(gameId));
  }

  @GetMapping
  public ResponseEntity<PageResponse<GameResponse>> findAllGames(
      @RequestParam(name = "page", defaultValue = "0", required = false) int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) int size,
      Authentication connectedUser) {
    return ResponseEntity.ok(gameService.findAllGames(page, size, connectedUser));
  }

  @GetMapping("/my-games")
  public ResponseEntity<PageResponse<GameResponse>> findAllOwnedGames(
      @RequestParam(name = "page", defaultValue = "0", required = false) int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) int size,
      Authentication connectedUser) {
    return ResponseEntity.ok(gameService.findAllGamesByOwner(page, size, connectedUser));
  }

  @GetMapping("/borrowed-games")
  public ResponseEntity<PageResponse<BorrowedGameResponse>> findAllBorrowedGames(
      @RequestParam(name = "page", defaultValue = "0", required = false) int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) int size,
      Authentication connectedUser) {
    return ResponseEntity.ok(gameService.findAllBorrowedGames(page, size, connectedUser));
  }

  @GetMapping("/returned-games")
  public ResponseEntity<PageResponse<BorrowedGameResponse>> findAllReturnedGames(
      @RequestParam(name = "page", defaultValue = "0", required = false) int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) int size,
      Authentication connectedUser) {
    return ResponseEntity.ok(gameService.findAllReturnedGames(page, size, connectedUser));
  }

  @PatchMapping("/shareable/{game-id}")
  public ResponseEntity<Integer> updateShareableStatus(
      @PathVariable("game-id") Integer gameId, Authentication connectedUser) {
    return ResponseEntity.ok(gameService.updateShareableStatus(gameId, connectedUser));
  }

  @PatchMapping("/archived/{game-id}")
  public ResponseEntity<Integer> updateArchivedStatus(
      @PathVariable("game-id") Integer gameId, Authentication connectedUser) {
    return ResponseEntity.ok(gameService.updateArchivedStatus(gameId, connectedUser));
  }

  @PostMapping("/borrow/{game-id}")
  public ResponseEntity<Integer> borrowBook(
      @PathVariable("game-id") Integer gameId, Authentication connectedUser) {
    return ResponseEntity.ok(gameService.borrowGame(gameId, connectedUser));
  }

  @PatchMapping("/borrow/return/{game-id}")
  public ResponseEntity<Integer> returnGame(
      @PathVariable("game-id") Integer gameId, Authentication connectedUser) {
    return ResponseEntity.ok(gameService.returnGame(gameId, connectedUser));
  }

  @PatchMapping("/borrow/return/approve/{game-id}")
  public ResponseEntity<Integer> approveGameReturn(
      @PathVariable("game-id") Integer gameId, Authentication connectedUser) {
    return ResponseEntity.ok(gameService.approveGameReturn(gameId, connectedUser));
  }

  @PostMapping(value = "/image/{game-id}", consumes = "multipart/form-data")
  public ResponseEntity<?> uploadGameImage(
      @Parameter @RequestPart("image") MultipartFile image,
      @PathVariable("game-id") Integer gameId,
      Authentication connectedUser)
      throws MethodArgumentNotValidException {
    // Validate image
    if (image.getSize() > 2000000) {
      throw new InvalidImageException("Image size exceeds 2MB limit");
    }
    String[] allowedTypes = {"image/jpg", "image/jpeg", "image/png", "image/webp"};
    if (!Arrays.asList(allowedTypes).contains(image.getContentType())) {
      throw new InvalidImageException("Unsupported image type");
    }

    gameService.uploadImage(image, connectedUser, gameId);
    return ResponseEntity.accepted().build();
  }
}
