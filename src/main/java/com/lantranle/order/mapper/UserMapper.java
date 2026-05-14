package com.lantranle.order.mapper;

import com.lantranle.order.dto.UserCreateRequest;
import com.lantranle.order.dto.UserDetailResponse;
import com.lantranle.order.dto.UserListResponse;
import com.lantranle.order.dto.UserUpdateRequest;
import com.lantranle.order.entity.User;
import com.lantranle.order.entity.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserMapper {

  public User toUser(UserCreateRequest request) {
    return User.builder()
      .username(request.getUsername())
      .email(request.getEmail())
      .password(request.getPassword())
      .fullName(request.getFullName())
      .phoneNumber(request.getPhoneNumber())
      .role(request.getRole() != null ? request.getRole() : UserRole.USER)
      .active(request.getActive() != null ? request.getActive() : true)
      .build();
  }

  public void updateUser(User user, UserUpdateRequest request) {
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    if (StringUtils.hasText(request.getPassword())) {
      user.setPassword(request.getPassword());
    }
    user.setFullName(request.getFullName());
    user.setPhoneNumber(request.getPhoneNumber());
    user.setRole(request.getRole() != null ? request.getRole() : user.getRole());
    user.setActive(request.getActive() != null ? request.getActive() : user.getActive());
  }

  public UserListResponse toUserListResponse(User user) {
    return UserListResponse.builder()
      .id(user.getId())
      .username(user.getUsername())
      .email(user.getEmail())
      .fullName(user.getFullName())
      .phoneNumber(user.getPhoneNumber())
      .role(user.getRole())
      .active(user.getActive())
      .build();
  }

  public UserDetailResponse toUserDetailResponse(User user) {
    return UserDetailResponse.builder()
      .id(user.getId())
      .username(user.getUsername())
      .email(user.getEmail())
      .fullName(user.getFullName())
      .phoneNumber(user.getPhoneNumber())
      .role(user.getRole())
      .active(user.getActive())
      .createdAt(user.getCreatedAt())
      .updatedAt(user.getUpdatedAt())
      .build();
  }
}
