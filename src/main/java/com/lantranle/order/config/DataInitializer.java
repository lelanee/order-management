package com.lantranle.order.config;

import com.lantranle.order.entity.User;
import com.lantranle.order.entity.UserRole;
import com.lantranle.order.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${DEMO_ADMIN_USERNAME:admin}")
  private String demoAdminUsername;

  @Value("${DEMO_ADMIN_EMAIL:admin@example.com}")
  private String demoAdminEmail;

  @Value("${DEMO_ADMIN_PASSWORD:admin123}")
  private String demoAdminPassword;

  @Value("${DEMO_ADMIN_FULL_NAME:Administrator}")
  private String demoAdminFullName;

  @Value("${DEMO_USER_USERNAME:user}")
  private String demoUserUsername;

  @Value("${DEMO_USER_EMAIL:user@example.com}")
  private String demoUserEmail;

  @Value("${DEMO_USER_PASSWORD:user123}")
  private String demoUserPassword;

  @Value("${DEMO_USER_FULL_NAME:Demo User}")
  private String demoUserFullName;

  @Override
  public void run(String... args) {
    upsertDemoUser(demoAdminUsername, demoAdminEmail, demoAdminPassword, demoAdminFullName, UserRole.ADMIN);
    upsertDemoUser(demoUserUsername, demoUserEmail, demoUserPassword, demoUserFullName, UserRole.USER);
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
