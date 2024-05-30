package com.dhguo.retrogamesnetwork.feedback;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackRequest {
  @Positive(message = "Rating has to be positive.")
  @Min(value = 0, message = "Rating has to be in the range 0 - 5")
  @Max(value = 5, message = "Rating has to be in the range 0 - 5")
  Double rating;

  @NotNull(message = "Comment cannot be null")
  @NotEmpty(message = "Comment cannot be empty")
  @NotBlank(message = "Comment cannot be blank")
  String comment;

  @NotNull(message = "Game ID cannot be null")
  Integer gameId;
}
