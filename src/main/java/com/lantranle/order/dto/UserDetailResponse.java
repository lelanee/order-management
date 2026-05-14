package com.lantranle.order.dto;

import com.lantranle.order.entity.UserRole;
import java.time.LocalDateTime;
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
public class UserDetailResponse {

  private Long id;

  private String username;

  private String email;

  private String fullName;

  private String phoneNumber;

  private UserRole role;

  private Boolean active;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
