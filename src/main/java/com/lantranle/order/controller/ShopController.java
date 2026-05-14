package com.lantranle.order.controller;

import com.lantranle.order.dto.OrderCreateRequest;
import com.lantranle.order.dto.OrderDetailResponse;
import com.lantranle.order.dto.OrderItemCreateRequest;
import com.lantranle.order.dto.ProductListResponse;
import com.lantranle.order.service.OrderService;
import com.lantranle.order.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

  private final ProductService productService;
  private final OrderService orderService;

  @GetMapping("/products")
  public String getProductsPage(Model model) {
    List<ProductListResponse> products = productService.listActiveProductsForShop();
    model.addAttribute("products", products);
    if (!model.containsAttribute("orderRequest")) {
      model.addAttribute("orderRequest", buildOrderRequest(products));
    }

    return "shop/products";
  }

  @PostMapping("/orders")
  public String createOrder(
    @Valid @ModelAttribute("orderRequest") OrderCreateRequest request,
    BindingResult bindingResult,
    Model model,
    RedirectAttributes redirectAttributes
  ) {
    List<ProductListResponse> products = productService.listActiveProductsForShop();
    if (bindingResult.hasErrors()) {
      model.addAttribute("products", products);
      return "shop/products";
    }

    try {
      OrderDetailResponse order = orderService.createOrder(request);
      redirectAttributes.addFlashAttribute("successMessage", "Order created successfully");
      return "redirect:/shop/orders/" + order.getId() + "/success";
    } catch (IllegalArgumentException exception) {
      model.addAttribute("products", products);
      model.addAttribute("errorMessage", exception.getMessage());
      return "shop/products";
    }
  }

  @GetMapping("/orders/{id}/success")
  public String getOrderSuccessPage(@PathVariable Long id, Model model) {
    model.addAttribute("order", orderService.getOrderById(id));
    return "shop/order-success";
  }

  private OrderCreateRequest buildOrderRequest(List<ProductListResponse> products) {
    return OrderCreateRequest.builder()
      .items(products.stream()
        .map(product -> OrderItemCreateRequest.builder()
          .productId(product.getId())
          .quantity(0)
          .build())
        .toList())
      .build();
  }
}
