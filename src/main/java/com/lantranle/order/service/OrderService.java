package com.lantranle.order.service;

import com.lantranle.order.dto.OrderCreateRequest;
import com.lantranle.order.dto.OrderItemCreateRequest;
import com.lantranle.order.entity.Order;
import com.lantranle.order.entity.OrderItem;
import com.lantranle.order.entity.OrderStatus;
import com.lantranle.order.entity.Product;
import com.lantranle.order.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final ProductService productService;

  @Transactional(readOnly = true)
  public List<Order> listOrders(OrderStatus status) {
    Sort sort = Sort.by("createdAt").descending();
    return status == null ? orderRepository.findAll(sort) : orderRepository.findByStatus(status, sort);
  }

  @Transactional(readOnly = true)
  public Order getOrderById(Long id) {
    return findOrderById(id);
  }

  @Transactional
  public Order createOrder(OrderCreateRequest request) {
    List<OrderItemCreateRequest> selectedItems = request.getItems().stream()
      .filter(item -> item.getQuantity() != null && item.getQuantity() > 0)
      .toList();

    if (selectedItems.isEmpty()) {
      throw new IllegalArgumentException("Please select at least one product");
    }

    Order order = Order.builder()
      .customerName(request.getCustomerName())
      .customerPhone(request.getCustomerPhone())
      .customerEmail(request.getCustomerEmail())
      .shippingAddress(request.getShippingAddress())
      .note(request.getNote())
      .status(OrderStatus.PENDING)
      .build();

    BigDecimal totalAmount = BigDecimal.ZERO;
    for (OrderItemCreateRequest itemRequest : selectedItems) {
      Product product = productService.getActiveProductEntityById(itemRequest.getProductId());
      int quantity = itemRequest.getQuantity();

      if (quantity > product.getStockQuantity()) {
        throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
      }

      BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
      OrderItem orderItem = OrderItem.builder()
        .order(order)
        .product(product)
        .productName(product.getName())
        .quantity(quantity)
        .unitPrice(product.getPrice())
        .lineTotal(lineTotal)
        .build();

      product.setStockQuantity(product.getStockQuantity() - quantity);
      order.getItems().add(orderItem);
      totalAmount = totalAmount.add(lineTotal);
    }

    order.setTotalAmount(totalAmount);

    try {
      return orderRepository.saveAndFlush(order);
    } catch (ObjectOptimisticLockingFailureException | OptimisticLockException exception) {
      throw new IllegalArgumentException("Product stock changed while creating the order. Please try again.");
    }
  }

  @Transactional
  public Order updateStatus(Long id, OrderStatus status) {
    Order order = findOrderById(id);
    order.setStatus(status);

    return orderRepository.save(order);
  }

  private Order findOrderById(Long id) {
    return orderRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
  }
}
