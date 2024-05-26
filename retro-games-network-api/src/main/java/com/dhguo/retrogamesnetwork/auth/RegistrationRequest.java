package com.dhguo.retrogamesnetwork.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {

  @NotEmpty(message = "First name can not be empty")
  @NotBlank(message = "First name can not be blank")
  private String firstName;

  @NotEmpty(message = "Last name can not be empty")
  @NotBlank(message = "Last name can not be blank")
  private String lastName;

  @NotEmpty(message = "Email can not be empty")
  @NotBlank(message = "Email can not be blank")
  @Email(message = "Email address is invalid")
  private String email;

  @NotEmpty(message = "Password can not be empty")
  @NotBlank(message = "Password name can not be blank")
  @Size(min = 8, message = "Password must contain at least 8 characters")
  @Size(max = 128, message = "Password length should not exceed 128 characters")
  private String password;
}
