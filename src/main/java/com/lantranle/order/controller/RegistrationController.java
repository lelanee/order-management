package com.lantranle.order.controller;

import com.lantranle.order.dto.RegisterRequest;
import com.lantranle.order.entity.User;
import com.lantranle.order.entity.UserRole;
import com.lantranle.order.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserDetailsService userDetailsService;

  @GetMapping
  public String getRegisterPage(Model model) {
    if (!model.containsAttribute("registerRequest")) {
      model.addAttribute("registerRequest", new RegisterRequest());
    }

    return "register";
  }

  @PostMapping
  public String register(
    @Valid @ModelAttribute("registerRequest") RegisterRequest request,
    BindingResult bindingResult,
    HttpServletRequest httpRequest
  ) {
    if (userRepository.existsByUsername(request.getUsername())) {
      bindingResult.rejectValue("username", "username.exists", "This username is already taken.");
    }
    if (userRepository.existsByEmail(request.getEmail())) {
      bindingResult.rejectValue("email", "email.exists", "This email is already registered.");
    }
    if (bindingResult.hasErrors()) {
      return "register";
    }

    User user = User.builder()
      .username(request.getUsername())
      .email(request.getEmail())
      .password(passwordEncoder.encode(request.getPassword()))
      .fullName(request.getFullName())
      .phoneNumber(request.getPhoneNumber())
      .role(UserRole.USER)
      .active(true)
      .build();
    userRepository.save(user);
    signIn(request.getUsername(), httpRequest);

    return "redirect:/";
  }

  private void signIn(String username, HttpServletRequest request) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
      userDetails,
      null,
      userDetails.getAuthorities()
    );
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);
    request.getSession(true)
      .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
  }
}
