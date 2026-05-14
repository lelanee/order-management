package com.lantranle.order.controller;

import com.lantranle.order.dto.PageResponse;
import com.lantranle.order.dto.UserCreateRequest;
import com.lantranle.order.dto.UserDetailResponse;
import com.lantranle.order.dto.UserListRequest;
import com.lantranle.order.dto.UserListResponse;
import com.lantranle.order.dto.UserUpdateRequest;
import com.lantranle.order.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping
  public PageResponse<UserListResponse> listUsers(@Valid @ModelAttribute UserListRequest request) {
    return userService.listUsers(request);
  }

  @GetMapping("/{id}")
  public UserDetailResponse getUserById(@PathVariable Long id) {
    return userService.getUserById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserDetailResponse createUser(@Valid @RequestBody UserCreateRequest request) {
    return userService.createUser(request);
  }

  @PutMapping("/{id}")
  public UserDetailResponse updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
    return userService.updateUser(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
  }
}
