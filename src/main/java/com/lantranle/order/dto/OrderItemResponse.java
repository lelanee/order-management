package com.lantranle.order.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

  private Long id;

  private Long productId;

  private String productName;

  private Integer quantity;

  private BigDecimal unitPrice;

  private BigDecimal lineTotal;
}
