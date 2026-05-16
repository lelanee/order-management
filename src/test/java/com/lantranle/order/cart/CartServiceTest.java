package com.lantranle.order.cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.lantranle.order.entity.Product;
import com.lantranle.order.service.ProductService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CartServiceTest {

  private final ProductService productService = Mockito.mock(ProductService.class);
  private final CartService cartService = new CartService(productService);

  @Test
  void addUpdateRemoveAndClearItems() {
    Product product = product(1L, "Coffee", 100000, 5);
    when(productService.getActiveProductEntityById(1L)).thenReturn(product);

    cartService.addItem(1L, 2);
    cartService.updateQuantity(1L, 3);

    assertThat(cartService.totalItems()).isEqualTo(3);
    assertThat(cartService.view().getSubtotal()).isEqualByComparingTo("300000");

    cartService.removeItem(1L);
    assertThat(cartService.isEmpty()).isTrue();

    cartService.addItem(1L, 1);
    cartService.clear();
    assertThat(cartService.totalItems()).isZero();
  }

  @Test
  void rejectsQuantityGreaterThanStock() {
    Product product = product(1L, "Coffee", 100000, 2);
    when(productService.getActiveProductEntityById(1L)).thenReturn(product);

    assertThatThrownBy(() -> cartService.addItem(1L, 3))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Insufficient stock");
  }

  private Product product(Long id, String name, int price, int stockQuantity) {
    return Product.builder()
      .id(id)
      .name(name)
      .price(BigDecimal.valueOf(price))
      .stockQuantity(stockQuantity)
      .active(true)
      .build();
  }
}
