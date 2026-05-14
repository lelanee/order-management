package com.lantranle.order.mapper;

import com.lantranle.order.dto.OrderDetailResponse;
import com.lantranle.order.dto.OrderItemResponse;
import com.lantranle.order.dto.OrderListResponse;
import com.lantranle.order.entity.Order;
import com.lantranle.order.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

  public OrderListResponse toOrderListResponse(Order order) {
    return OrderListResponse.builder()
      .id(order.getId())
      .customerName(order.getCustomerName())
      .customerPhone(order.getCustomerPhone())
      .customerEmail(order.getCustomerEmail())
      .status(order.getStatus())
      .totalAmount(order.getTotalAmount())
      .createdAt(order.getCreatedAt())
      .build();
  }

  public OrderDetailResponse toOrderDetailResponse(Order order) {
    return OrderDetailResponse.builder()
      .id(order.getId())
      .customerName(order.getCustomerName())
      .customerPhone(order.getCustomerPhone())
      .customerEmail(order.getCustomerEmail())
      .shippingAddress(order.getShippingAddress())
      .note(order.getNote())
      .status(order.getStatus())
      .totalAmount(order.getTotalAmount())
      .items(order.getItems().stream().map(this::toOrderItemResponse).toList())
      .createdAt(order.getCreatedAt())
      .updatedAt(order.getUpdatedAt())
      .build();
  }

  private OrderItemResponse toOrderItemResponse(OrderItem item) {
    return OrderItemResponse.builder()
      .id(item.getId())
      .productId(item.getProduct().getId())
      .productName(item.getProductName())
      .quantity(item.getQuantity())
      .unitPrice(item.getUnitPrice())
      .lineTotal(item.getLineTotal())
      .build();
  }
}
