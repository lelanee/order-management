package com.lantranle.order.controller;

import com.lantranle.order.cart.CartService;
import com.lantranle.order.dto.OrderCreateRequest;
import com.lantranle.order.dto.OrderItemCreateRequest;
import com.lantranle.order.entity.Order;
import com.lantranle.order.service.OrderService;
import com.lantranle.order.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

  private final ProductService productService;
  private final OrderService orderService;
  private final CartService cartService;

  @GetMapping("/products")
  public String getProductsPage(
    @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
    Model model
  ) {
    Page<com.lantranle.order.entity.Product> page = productService.listActiveProductsForShop(pageable);
    model.addAttribute("page", page);
    model.addAttribute("products", page.getContent());
    return "shop/products";
  }

  @PostMapping("/cart/add")
  public String addToCart(
    @RequestParam Long productId,
    @RequestParam(required = false) Integer quantity,
    RedirectAttributes redirectAttributes
  ) {
    try {
      cartService.addItem(productId, quantity == null ? 1 : quantity);
      redirectAttributes.addFlashAttribute("successMessage", "Added to cart");
    } catch (IllegalArgumentException exception) {
      redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
    }

    return "redirect:/shop/products";
  }

  @GetMapping("/cart")
  public String getCartPage(Model model) {
    model.addAttribute("cart", cartService.view());
    return "shop/cart";
  }

  @PostMapping("/cart/update")
  public String updateCart(
    @RequestParam Long productId,
    @RequestParam(required = false) Integer quantity,
    RedirectAttributes redirectAttributes
  ) {
    try {
      cartService.updateQuantity(productId, quantity == null ? 0 : quantity);
      redirectAttributes.addFlashAttribute("successMessage", "Cart updated");
    } catch (IllegalArgumentException exception) {
      redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
    }

    return "redirect:/shop/cart";
  }

  @PostMapping("/cart/remove")
  public String removeFromCart(@RequestParam Long productId, RedirectAttributes redirectAttributes) {
    cartService.removeItem(productId);
    redirectAttributes.addFlashAttribute("successMessage", "Item removed");
    return "redirect:/shop/cart";
  }

  @GetMapping("/checkout")
  public String getCheckoutPage(Model model, RedirectAttributes redirectAttributes) {
    if (cartService.isEmpty()) {
      redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty");
      return "redirect:/shop/cart";
    }

    if (!model.containsAttribute("orderRequest")) {
      model.addAttribute("orderRequest", new OrderCreateRequest());
    }

    model.addAttribute("cart", cartService.view());
    return "shop/checkout";
  }

  @PostMapping("/checkout")
  public String createOrder(
    @Valid @ModelAttribute("orderRequest") OrderCreateRequest request,
    BindingResult bindingResult,
    Model model,
    RedirectAttributes redirectAttributes
  ) {
    if (cartService.isEmpty()) {
      redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty");
      return "redirect:/shop/cart";
    }

    if (bindingResult.hasErrors()) {
      model.addAttribute("cart", cartService.view());
      return "shop/checkout";
    }

    try {
      request.setItems(cartService.view().getItems().stream()
        .map(item -> OrderItemCreateRequest.builder()
          .productId(item.getProductId())
          .quantity(item.getQuantity())
          .build())
        .toList());
      Order order = orderService.createOrder(request);
      cartService.clear();
      redirectAttributes.addFlashAttribute("successMessage", "Order created successfully");
      return "redirect:/shop/orders/" + order.getId() + "/success";
    } catch (IllegalArgumentException exception) {
      model.addAttribute("errorMessage", exception.getMessage());
      model.addAttribute("cart", cartService.view());
      return "shop/checkout";
    }
  }

  @GetMapping("/orders/{id}/success")
  public String getOrderSuccessPage(@PathVariable Long id, Model model) {
    model.addAttribute("order", orderService.getOrderById(id));
    return "shop/order-success";
  }
}
