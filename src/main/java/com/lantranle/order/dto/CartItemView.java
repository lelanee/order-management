package com.lantranle.order.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemView {

  private Long productId;
  private String name;
  private String imageUrl;
  private BigDecimal unitPrice;
  private Integer quantity;
  private Integer stockQuantity;
  private BigDecimal lineTotal;
}
