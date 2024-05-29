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
public class BorrowedGameResponse {

  private Integer id;

  private String title;

  private String description;

  private String platform;

  private String publisher;

  private double rating;

  private boolean returned;

  private boolean returnApproved;
}
