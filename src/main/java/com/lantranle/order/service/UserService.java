package com.lantranle.order.service;

import com.lantranle.order.dto.PageResponse;
import com.lantranle.order.dto.UserCreateRequest;
import com.lantranle.order.dto.UserDetailResponse;
import com.lantranle.order.dto.UserListRequest;
import com.lantranle.order.dto.UserListResponse;
import com.lantranle.order.dto.UserUpdateRequest;
import com.lantranle.order.entity.User;
import com.lantranle.order.mapper.UserMapper;
import com.lantranle.order.repository.UserRepository;
import com.lantranle.order.repository.UserSpecification;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional(readOnly = true)
  public PageResponse<UserListResponse> listUsers(UserListRequest request) {
    Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("id").ascending());
    Page<UserListResponse> users = userRepository.findAll(UserSpecification.filterBy(request), pageable)
      .map(userMapper::toUserListResponse);

    return PageResponse.from(users);
  }

  @Transactional(readOnly = true)
  public UserDetailResponse getUserById(Long id) {
    return userMapper.toUserDetailResponse(findUserById(id));
  }

  @Transactional
  public UserDetailResponse createUser(UserCreateRequest request) {
    validateUniqueUsername(request.getUsername(), null);
    validateUniqueEmail(request.getEmail(), null);

    User user = userMapper.toUser(request);

    return userMapper.toUserDetailResponse(userRepository.save(user));
  }

  @Transactional
  public UserDetailResponse updateUser(Long id, UserUpdateRequest request) {
    User existingUser = findUserById(id);

    validateUniqueUsername(request.getUsername(), id);
    validateUniqueEmail(request.getEmail(), id);
    userMapper.updateUser(existingUser, request);

    return userMapper.toUserDetailResponse(userRepository.save(existingUser));
  }

  @Transactional
  public void deleteUser(Long id) {
    User user = findUserById(id);
    user.setActive(false);
    userRepository.save(user);
  }

  private User findUserById(Long id) {
    return userRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
  }

  private void validateUniqueUsername(String username, Long currentUserId) {
    userRepository.findByUsername(username)
      .filter(user -> !user.getId().equals(currentUserId))
      .ifPresent(user -> {
        throw new EntityExistsException("Username already exists: " + username);
      });
  }

  private void validateUniqueEmail(String email, Long currentUserId) {
    userRepository.findByEmail(email)
      .filter(user -> !user.getId().equals(currentUserId))
      .ifPresent(user -> {
        throw new EntityExistsException("Email already exists: " + email);
      });
  }
}
