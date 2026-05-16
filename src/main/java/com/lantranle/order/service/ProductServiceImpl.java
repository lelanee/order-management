package com.lantranle.order.service;

import com.lantranle.order.dto.ProductRequest;
import com.lantranle.order.entity.Product;
import com.lantranle.order.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<Product> listProductsForAdmin(Pageable pageable, String name, Boolean active) {
    return productRepository.search(name, active, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Product> listActiveProductsForShop() {
    return productRepository.findByActiveTrueOrderByIdAsc();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Product> listActiveProductsForShop(Pageable pageable) {
    return productRepository.findByActiveTrueOrderByIdAsc(pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Product getProductById(Long id) {
    return findProductById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Product getActiveProductEntityById(Long id) {
    Product product = findProductById(id);
    if (!Boolean.TRUE.equals(product.getActive())) {
      throw new EntityNotFoundException("Active product not found with id: " + id);
    }

    return product;
  }

  /**
   * Creates a product from validated admin input and normalizes a missing active flag to inactive.
   *
   * @param request product form values submitted from the admin page
   * @return persisted product with generated database metadata
   */
  @Override
  @Transactional
  public Product createProduct(ProductRequest request) {
    Product product = Product.builder()
      .name(request.getName())
      .description(request.getDescription())
      .price(request.getPrice())
      .stockQuantity(request.getStockQuantity())
      .imageUrl(request.getImageUrl())
      .active(Boolean.TRUE.equals(request.getActive()))
      .build();

    return productRepository.save(product);
  }

  /**
   * Updates a product in place so JPA can enforce optimistic locking through {@code Product.version}.
   *
   * @param id product identifier to update
   * @param request replacement product values from the admin form
   * @return saved product after applying the submitted values
   */
  @Override
  @Transactional
  public Product updateProduct(Long id, ProductRequest request) {
    Product existingProduct = findProductById(id);

    existingProduct.setName(request.getName());
    existingProduct.setDescription(request.getDescription());
    existingProduct.setPrice(request.getPrice());
    existingProduct.setStockQuantity(request.getStockQuantity());
    existingProduct.setImageUrl(request.getImageUrl());
    existingProduct.setActive(Boolean.TRUE.equals(request.getActive()));

    return productRepository.save(existingProduct);
  }

  /**
   * Soft-deletes a product by marking it inactive while preserving historical order references.
   *
   * @param id product identifier to deactivate
   */
  @Override
  @Transactional
  public void deactivateProduct(Long id) {
    Product product = findProductById(id);
    product.setActive(false);
    productRepository.save(product);
  }

  @Override
  public Product findProductById(Long id) {
    return productRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
  }
}
