package com.lantranle.order.dto;

import com.lantranle.order.entity.UserRole;
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
public class UserUpdateRequest {

  @NotBlank(message = "Username is required")
  @Size(max = 100, message = "Username must not exceed 100 characters")
  private String username;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Size(max = 150, message = "Email must not exceed 150 characters")
  private String email;

  @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
  private String password;

  @NotBlank(message = "Full name is required")
  @Size(max = 150, message = "Full name must not exceed 150 characters")
  private String fullName;

  @Size(max = 20, message = "Phone number must not exceed 20 characters")
  private String phoneNumber;

  private UserRole role;

  private Boolean active;
}
