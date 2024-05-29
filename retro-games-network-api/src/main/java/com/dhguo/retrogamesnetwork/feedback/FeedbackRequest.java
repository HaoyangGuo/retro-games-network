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
  @Positive(message = "200")
  @Min(value = 0, message = "201")
  @Max(value = 5, message = "202")
  Double note;

  @NotNull(message = "203")
  @NotEmpty(message = "203")
  @NotBlank(message = "203")
  String comment;

  @NotNull(message = "204")
  Integer bookId;
}
