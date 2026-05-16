package com.lantranle.order.service;

import com.lantranle.order.dto.ProductRequest;
import com.lantranle.order.entity.Product;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

  Page<Product> listProductsForAdmin(Pageable pageable, String name, Boolean active);

  List<Product> listActiveProductsForShop();

  Product getProductById(Long id);

  Product getActiveProductEntityById(Long id);

  Product createProduct(ProductRequest request);

  Product updateProduct(Long id, ProductRequest request);

  void deactivateProduct(Long id);

  Product findProductById(Long id);
}
