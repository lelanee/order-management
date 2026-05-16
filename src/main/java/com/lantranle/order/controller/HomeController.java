package com.lantranle.order.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @Value("${app.demo.admin.username}")
  private String demoAdminUsername;

  @Value("${app.demo.admin.password}")
  private String demoAdminPassword;

  @Value("${app.demo.user.username}")
  private String demoUserUsername;

  @Value("${app.demo.user.password}")
  private String demoUserPassword;

  @GetMapping("/")
  public String home(Authentication authentication) {
    boolean isAdmin = authentication.getAuthorities().stream()
      .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

    return isAdmin ? "redirect:/admin/products" : "redirect:/shop/products";
  }

  @GetMapping("/login")
  public String login(Model model) {
    model.addAttribute("demoAdminUsername", demoAdminUsername);
    model.addAttribute("demoAdminPassword", demoAdminPassword);
    model.addAttribute("demoUserUsername", demoUserUsername);
    model.addAttribute("demoUserPassword", demoUserPassword);
    return "login";
  }
}
