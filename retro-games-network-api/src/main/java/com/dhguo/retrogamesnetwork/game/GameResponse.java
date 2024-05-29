package com.dhguo.retrogamesnetwork.game;

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
public class GameResponse {
  private Integer id;

  private String title;

  private String description;

  private String platform;

  private String publisher;

  private String owner;

  private String imageUrl;

  private double rating;

  private boolean archived;

  private boolean shareable;
}
