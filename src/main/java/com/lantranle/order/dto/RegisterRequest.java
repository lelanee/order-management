package com.lantranle.order.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
  private String username;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Size(max = 150, message = "Email must not exceed 150 characters")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
  private String password;

  @NotBlank(message = "Full name is required")
  @Size(max = 150, message = "Full name must not exceed 150 characters")
  private String fullName;

  @Size(max = 20, message = "Phone number must not exceed 20 characters")
  private String phoneNumber;
}
