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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final ProductService productService;

  @Override
  @Transactional(readOnly = true)
  public Page<Order> listOrders(OrderStatus status, Pageable pageable) {
    return status == null ? orderRepository.findAll(pageable) : orderRepository.findByStatus(status, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Order getOrderById(Long id) {
    return findOrderById(id);
  }

  /**
   * Creates an order from selected shop items, validates available stock, reserves quantities,
   * calculates line totals, and saves the aggregate in one transaction. Product {@code @Version}
   * values protect the stock decrement from concurrent checkout conflicts.
   *
   * @param request customer and item selections from the shop order form
   * @return persisted order with order items and total amount populated
   */
  @Override
  @Transactional
  public Order createOrder(OrderCreateRequest request) {
    List<OrderItemCreateRequest> selectedItems = request.getItems().stream()
      .filter(item -> item.getQuantity() != null && item.getQuantity() > 0)
      .toList();

    if (selectedItems.isEmpty()) {
      throw new IllegalArgumentException("Please select at least one product");
    }

    Order order = buildPendingOrder(request);
    order.setTotalAmount(validateAndReserveStock(order, selectedItems));

    try {
      return orderRepository.saveAndFlush(order);
    } catch (ObjectOptimisticLockingFailureException | OptimisticLockException exception) {
      throw new IllegalArgumentException("Product stock changed while creating the order. Please try again.");
    }
  }

  /**
   * Changes an order workflow status from the admin screen.
   *
   * @param id order identifier to update
   * @param status new status selected by the admin
   * @return saved order with the updated status
   */
  @Override
  @Transactional
  public Order updateStatus(Long id, OrderStatus status) {
    Order order = findOrderById(id);
    order.setStatus(status);

    return orderRepository.save(order);
  }

  private BigDecimal validateAndReserveStock(Order order, List<OrderItemCreateRequest> selectedItems) {
    BigDecimal totalAmount = BigDecimal.ZERO;
    for (OrderItemCreateRequest itemRequest : selectedItems) {
      Product product = productService.getActiveProductEntityById(itemRequest.getProductId());
      int quantity = itemRequest.getQuantity();

      if (quantity > product.getStockQuantity()) {
        throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
      }

      BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
      order.getItems().add(buildOrderItem(order, product, quantity, lineTotal));
      product.setStockQuantity(product.getStockQuantity() - quantity);
      totalAmount = totalAmount.add(lineTotal);
    }

    return totalAmount;
  }

  private Order buildPendingOrder(OrderCreateRequest request) {
    return Order.builder()
      .customerName(request.getCustomerName())
      .customerPhone(request.getCustomerPhone())
      .customerEmail(request.getCustomerEmail())
      .shippingAddress(request.getShippingAddress())
      .note(request.getNote())
      .status(OrderStatus.PENDING)
      .build();
  }

  private OrderItem buildOrderItem(Order order, Product product, int quantity, BigDecimal lineTotal) {
    return OrderItem.builder()
      .order(order)
      .product(product)
      .productName(product.getName())
      .quantity(quantity)
      .unitPrice(product.getPrice())
      .lineTotal(lineTotal)
      .build();
  }

  private Order findOrderById(Long id) {
    return orderRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
  }
}
