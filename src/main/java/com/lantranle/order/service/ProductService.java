package com.lantranle.order.service;

import com.lantranle.order.dto.ProductCreateRequest;
import com.lantranle.order.dto.ProductDetailResponse;
import com.lantranle.order.dto.ProductListResponse;
import com.lantranle.order.dto.ProductUpdateRequest;
import com.lantranle.order.entity.Product;
import com.lantranle.order.mapper.ProductMapper;
import com.lantranle.order.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ProductListResponse> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toProductListResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProduct(Long id) {
        return productMapper.toProductDetailResponse(getProductEntity(id));
    }

    @Transactional
    public ProductDetailResponse createProduct(ProductCreateRequest request) {
        Product product = productMapper.toProduct(request);

        return productMapper.toProductDetailResponse(productRepository.save(product));
    }

    @Transactional
    public ProductDetailResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product existingProduct = getProductEntity(id);

        productMapper.updateProduct(existingProduct, request);

        return productMapper.toProductDetailResponse(productRepository.save(existingProduct));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductEntity(id);
        productRepository.delete(product);
    }

    private Product getProductEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }
}
