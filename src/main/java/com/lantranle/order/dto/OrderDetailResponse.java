package com.lantranle.order.dto;

import com.lantranle.order.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
public class OrderDetailResponse {

  private Long id;

  private String customerName;

  private String customerPhone;

  private String customerEmail;

  private String shippingAddress;

  private String note;

  private OrderStatus status;

  private BigDecimal totalAmount;

  private List<OrderItemResponse> items;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
