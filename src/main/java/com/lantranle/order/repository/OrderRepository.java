package com.lantranle.order.repository;

import com.lantranle.order.entity.Order;
import com.lantranle.order.entity.OrderStatus;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

  List<Order> findByStatus(OrderStatus status, Sort sort);
}
