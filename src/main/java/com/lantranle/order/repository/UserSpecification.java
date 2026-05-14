package com.lantranle.order.repository;

import com.lantranle.order.dto.UserListRequest;
import com.lantranle.order.entity.User;
import com.lantranle.order.entity.UserRole;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class UserSpecification {

  private UserSpecification() {
  }

  public static Specification<User> filterBy(UserListRequest request) {
    return hasUsername(request.getUsername())
      .and(hasEmail(request.getEmail()))
      .and(hasFullName(request.getFullName()))
      .and(hasRole(request.getRole()))
      .and(hasActive(request.getActive()));
  }

  private static Specification<User> hasUsername(String username) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(username)) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.like(
        criteriaBuilder.lower(root.get("username")),
        "%" + username.trim().toLowerCase() + "%"
      );
    };
  }

  private static Specification<User> hasEmail(String email) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(email)) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.like(
        criteriaBuilder.lower(root.get("email")),
        "%" + email.trim().toLowerCase() + "%"
      );
    };
  }

  private static Specification<User> hasFullName(String fullName) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(fullName)) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.like(
        criteriaBuilder.lower(root.get("fullName")),
        "%" + fullName.trim().toLowerCase() + "%"
      );
    };
  }

  private static Specification<User> hasActive(Boolean active) {
    return (root, query, criteriaBuilder) -> {
      if (active == null) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.equal(root.get("active"), active);
    };
  }

  private static Specification<User> hasRole(UserRole role) {
    return (root, query, criteriaBuilder) -> {
      if (role == null) {
        return criteriaBuilder.conjunction();
      }

      return criteriaBuilder.equal(root.get("role"), role);
    };
  }
}
