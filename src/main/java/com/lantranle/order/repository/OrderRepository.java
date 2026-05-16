package com.lantranle.order.repository;

import com.lantranle.order.entity.Order;
import com.lantranle.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

  Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
