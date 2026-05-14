package com.lantranle.order.service;

import com.lantranle.order.dto.ProductCreateRequest;
import com.lantranle.order.dto.PageResponse;
import com.lantranle.order.dto.ProductDetailResponse;
import com.lantranle.order.dto.ProductListRequest;
import com.lantranle.order.dto.ProductListResponse;
import com.lantranle.order.dto.ProductUpdateRequest;
import com.lantranle.order.entity.Product;
import com.lantranle.order.mapper.ProductMapper;
import com.lantranle.order.repository.ProductRepository;
import com.lantranle.order.repository.ProductSpecification;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  @Transactional(readOnly = true)
  public PageResponse<ProductListResponse> listProducts(ProductListRequest request) {
    return listProductsForAdmin(request);
  }

  @Transactional(readOnly = true)
  public PageResponse<ProductListResponse> listProductsForAdmin(ProductListRequest request) {
    Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("id").ascending());
    Page<ProductListResponse> products = productRepository.findAll(ProductSpecification.filterBy(request), pageable)
      .map(productMapper::toProductListResponse);

    return PageResponse.from(products);
  }

  @Transactional(readOnly = true)
  public List<ProductListResponse> listActiveProductsForShop() {
    ProductListRequest request = new ProductListRequest();
    request.setActive(true);
    request.setSize(Integer.MAX_VALUE);

    return productRepository.findAll(ProductSpecification.filterBy(request), Sort.by("id").ascending()).stream()
      .map(productMapper::toProductListResponse)
      .toList();
  }

  @Transactional(readOnly = true)
  public ProductDetailResponse getProductById(Long id) {
    return productMapper.toProductDetailResponse(findProductById(id));
  }

  @Transactional(readOnly = true)
  public Product getProductEntityById(Long id) {
    return findProductById(id);
  }

  @Transactional(readOnly = true)
  public Product getActiveProductEntityById(Long id) {
    Product product = findProductById(id);
    if (!Boolean.TRUE.equals(product.getActive())) {
      throw new EntityNotFoundException("Active product not found with id: " + id);
    }

    return product;
  }

  @Transactional
  public ProductDetailResponse createProduct(ProductCreateRequest request) {
    Product product = productMapper.toProduct(request);

    return productMapper.toProductDetailResponse(productRepository.save(product));
  }

  @Transactional
  public ProductDetailResponse updateProduct(Long id, ProductUpdateRequest request) {
    Product existingProduct = findProductById(id);

    productMapper.updateProduct(existingProduct, request);

    return productMapper.toProductDetailResponse(productRepository.save(existingProduct));
  }

  @Transactional
  public void deleteProduct(Long id) {
    Product product = findProductById(id);
    product.setActive(false);
    productRepository.save(product);
  }

  public Product findProductById(Long id) {
    return productRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
  }
}
