package com.lantranle.order.controller;

import com.lantranle.order.entity.OrderStatus;
import com.lantranle.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

  private final OrderService orderService;

  @GetMapping
  public String listOrders(@RequestParam(required = false) OrderStatus status, Model model) {
    model.addAttribute("orders", orderService.listOrders(status));
    model.addAttribute("selectedStatus", status);
    model.addAttribute("statuses", OrderStatus.values());
    return "admin/orders";
  }

  @GetMapping("/{id}")
  public String getOrderDetail(@PathVariable Long id, Model model) {
    model.addAttribute("order", orderService.getOrderById(id));
    model.addAttribute("statuses", OrderStatus.values());
    return "admin/order-detail";
  }

  @PostMapping("/{id}/status")
  public String updateStatus(
    @PathVariable Long id,
    @RequestParam OrderStatus status,
    RedirectAttributes redirectAttributes
  ) {
    orderService.updateStatus(id, status);
    redirectAttributes.addFlashAttribute("successMessage", "Order status updated successfully");
    return "redirect:/admin/orders/" + id;
  }
}
