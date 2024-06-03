package com.dhguo.retrogamesnetwork.game;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class GameRequest {
  @NotNull(message = "Title cannot be null")
  @NotEmpty(message = "Title cannot be empty")
  @NotBlank(message = "Title cannot be blank")
  String title;

  @NotNull(message = "Description cannot be null")
  @NotEmpty(message = "Description cannot be empty")
  @NotBlank(message = "Description cannot be blank")
  String description;

  @NotNull(message = "Description cannot be null")
  @NotEmpty(message = "Description cannot be empty")
  @NotBlank(message = "Description cannot be blank")
  String platform;

  @NotNull(message = "Platform cannot be null")
  @NotEmpty(message = "Publisher cannot be empty")
  @NotBlank(message = "Publisher cannot be blank")
  String publisher;
}
