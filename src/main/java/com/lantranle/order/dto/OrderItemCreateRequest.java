package com.lantranle.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class OrderItemCreateRequest {

  @NotNull(message = "Product is required")
  private Long productId;

  @Min(value = 0, message = "Quantity must be greater than or equal to 0")
  private Integer quantity;
}
