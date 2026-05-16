package com.lantranle.order.service;

import com.lantranle.order.dto.OrderCreateRequest;
import com.lantranle.order.entity.Order;
import com.lantranle.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

  Page<Order> listOrders(OrderStatus status, Pageable pageable);

  Order getOrderById(Long id);

  Order createOrder(OrderCreateRequest request);

  Order updateStatus(Long id, OrderStatus status);
}
