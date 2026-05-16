package com.lantranle.order.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartView {

  private List<CartItemView> items;
  private BigDecimal subtotal;
  private Integer totalItems;

  public boolean isEmpty() {
    return items == null || items.isEmpty();
  }
}
