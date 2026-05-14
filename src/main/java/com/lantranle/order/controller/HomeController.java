package com.lantranle.order.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @GetMapping("/")
  public String home(Authentication authentication) {
    boolean isAdmin = authentication.getAuthorities().stream()
      .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

    return isAdmin ? "redirect:/admin/products" : "redirect:/shop/products";
  }

  @GetMapping("/login")
  public String login() {
    return "login";
  }
}
