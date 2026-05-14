package com.lantranle.order.config;

import com.lantranle.order.entity.User;
import com.lantranle.order.entity.UserRole;
import com.lantranle.order.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    upsertDemoUser("admin", "admin@example.com", "admin123", "Administrator", UserRole.ADMIN);
    upsertDemoUser("user", "user@example.com", "user123", "Demo User", UserRole.USER);
  }

  private void upsertDemoUser(String username, String email, String password, String fullName, UserRole role) {
    User user = userRepository.findByUsername(username)
      .orElseGet(() -> User.builder().username(username).build());

    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    user.setFullName(fullName);
    user.setRole(role);
    user.setActive(true);
    userRepository.save(user);
  }
}
