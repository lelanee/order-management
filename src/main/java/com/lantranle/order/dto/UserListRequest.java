package com.lantranle.order.dto;

import com.lantranle.order.entity.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserListRequest {

  private String username;

  private String email;

  private String fullName;

  private UserRole role;

  private Boolean active;

  private int page = 0;

  private int size = 20;
}
