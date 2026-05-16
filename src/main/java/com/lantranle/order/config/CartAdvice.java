package com.lantranle.order.config;

import com.lantranle.order.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class CartAdvice {

  private final CartService cartService;

  @ModelAttribute("cartItemCount")
  public int cartItemCount() {
    return cartService.totalItems();
  }
}
