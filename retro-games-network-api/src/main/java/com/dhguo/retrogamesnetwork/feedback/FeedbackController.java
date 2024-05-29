package com.dhguo.retrogamesnetwork.feedback;

import com.dhguo.retrogamesnetwork.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {

  private final FeedbackService feedbackService;

  @PostMapping
  public ResponseEntity<Integer> saveFeedback(
      @Valid @RequestBody FeedbackRequest request, Authentication connectedUser) {
    return ResponseEntity.ok(feedbackService.save(request, connectedUser));
  }

  @GetMapping("game/{game-id}")
  public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbacksByGame(
      @PathVariable("game-id") Integer gameId,
      @RequestParam(name = "page", defaultValue = "0", required = false) int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) int size,
      Authentication connectedUser) {
    return ResponseEntity.ok(
        feedbackService.findAllFeedbacksByGame(gameId, page, size, connectedUser));
  }
}
