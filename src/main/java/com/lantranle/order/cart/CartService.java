package com.lantranle.order.cart;

import com.lantranle.order.dto.CartItemView;
import com.lantranle.order.dto.CartView;
import com.lantranle.order.entity.Product;
import com.lantranle.order.service.ProductService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

@Service
@SessionScope
@RequiredArgsConstructor
public class CartService {

  private final ProductService productService;
  private final Cart cart = new Cart();

  public void addItem(Long productId, int quantity) {
    Product product = productService.getActiveProductEntityById(productId);
    int currentQuantity = cart.getItems().getOrDefault(productId, 0);
    int requestedQuantity = currentQuantity + quantity;
    validateQuantity(product, requestedQuantity);
    cart.addOrUpdate(productId, quantity);
  }

  public void updateQuantity(Long productId, int quantity) {
    Product product = productService.getActiveProductEntityById(productId);
    validateQuantity(product, quantity);
    cart.update(productId, quantity);
  }

  public void removeItem(Long productId) {
    cart.remove(productId);
  }

  public void clear() {
    cart.clear();
  }

  public int totalItems() {
    return cart.totalItems();
  }

  public boolean isEmpty() {
    return cart.isEmpty();
  }

  public CartView view() {
    List<CartItemView> items = new ArrayList<>();
    BigDecimal subtotal = BigDecimal.ZERO;

    for (var entry : cart.getItems().entrySet()) {
      Product product = productService.getActiveProductEntityById(entry.getKey());
      int quantity = Math.min(entry.getValue(), product.getStockQuantity());
      BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
      subtotal = subtotal.add(lineTotal);
      items.add(CartItemView.builder()
        .productId(product.getId())
        .name(product.getName())
        .imageUrl(product.getImageUrl())
        .unitPrice(product.getPrice())
        .quantity(quantity)
        .stockQuantity(product.getStockQuantity())
        .lineTotal(lineTotal)
        .build());
    }

    return CartView.builder()
      .items(items)
      .subtotal(subtotal)
      .totalItems(cart.totalItems())
      .build();
  }

  private void validateQuantity(Product product, int quantity) {
    if (quantity <= 0) {
      return;
    }

    if (quantity > product.getStockQuantity()) {
      throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
    }
  }
}
