package com.lantranle.order.cart;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cart implements Serializable {

  private final Map<Long, Integer> items = new LinkedHashMap<>();

  public void addOrUpdate(Long productId, int quantity) {
    if (quantity <= 0) {
      remove(productId);
      return;
    }

    items.merge(productId, quantity, Integer::sum);
  }

  public void update(Long productId, int quantity) {
    if (quantity <= 0) {
      remove(productId);
      return;
    }

    items.put(productId, quantity);
  }

  public void remove(Long productId) {
    items.remove(productId);
  }

  public void clear() {
    items.clear();
  }

  public int totalItems() {
    return items.values().stream()
      .mapToInt(Integer::intValue)
      .sum();
  }

  public boolean isEmpty() {
    return items.isEmpty();
  }
}
