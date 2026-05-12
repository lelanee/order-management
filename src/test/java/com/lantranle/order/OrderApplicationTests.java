package com.lantranle.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lantranle.order.controller.ProductController;
import com.lantranle.order.dto.ProductCreateRequest;
import com.lantranle.order.dto.ProductDetailResponse;
import com.lantranle.order.dto.ProductUpdateRequest;
import com.lantranle.order.entity.Product;
import com.lantranle.order.repository.ProductRepository;
import com.lantranle.order.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrderApplicationTests {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductController productController;

	@Test
	void contextLoads() {
	}

	@Test
	void productRepositoryCanSaveAndFindProduct() {
		Product product = Product.builder()
				.name("Test Product")
				.description("Product for repository test")
				.price(BigDecimal.valueOf(10000))
				.stockQuantity(10)
				.imageUrl("https://example.com/product.jpg")
				.active(true)
				.build();

		Product savedProduct = productRepository.save(product);

		assertThat(savedProduct.getId()).isNotNull();
		assertThat(productRepository.findById(savedProduct.getId()))
				.isPresent()
				.get()
				.extracting(Product::getName)
				.isEqualTo("Test Product");
	}

	@Test
	void productServiceCanCreateUpdateAndDeleteProduct() {
		ProductCreateRequest product = ProductCreateRequest.builder()
				.name("Service Product")
				.description("Product for service test")
				.price(BigDecimal.valueOf(15000))
				.stockQuantity(5)
				.active(true)
				.build();

		ProductDetailResponse createdProduct = productService.createProduct(product);

		assertThat(createdProduct.getId()).isNotNull();

		ProductUpdateRequest updateRequest = ProductUpdateRequest.builder()
				.name("Updated Service Product")
				.description("Updated product for service test")
				.price(BigDecimal.valueOf(20000))
				.stockQuantity(8)
				.imageUrl("https://example.com/updated-product.jpg")
				.active(false)
				.build();

		ProductDetailResponse updatedProduct = productService.updateProduct(createdProduct.getId(), updateRequest);

		assertThat(updatedProduct.getName()).isEqualTo("Updated Service Product");
		assertThat(updatedProduct.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(20000));
		assertThat(updatedProduct.getActive()).isFalse();

		productService.deleteProduct(createdProduct.getId());

		assertThatThrownBy(() -> productService.getProduct(createdProduct.getId()))
				.isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	void productControllerCanCreateGetUpdateAndDeleteProduct() {
		ProductCreateRequest product = ProductCreateRequest.builder()
				.name("Controller Product")
				.description("Product for controller test")
				.price(BigDecimal.valueOf(30000))
				.stockQuantity(12)
				.active(true)
				.build();

		ProductDetailResponse createdProduct = productController.createProduct(product);

		assertThat(createdProduct.getId()).isNotNull();
		assertThat(productController.getProduct(createdProduct.getId()).getName())
				.isEqualTo("Controller Product");

		ProductUpdateRequest updateRequest = ProductUpdateRequest.builder()
				.name("Updated Controller Product")
				.description("Updated product for controller test")
				.price(BigDecimal.valueOf(35000))
				.stockQuantity(15)
				.active(false)
				.build();

		ProductDetailResponse updatedProduct = productController.updateProduct(createdProduct.getId(), updateRequest);

		assertThat(updatedProduct.getName()).isEqualTo("Updated Controller Product");
		assertThat(updatedProduct.getActive()).isFalse();

		productController.deleteProduct(createdProduct.getId());

		assertThatThrownBy(() -> productController.getProduct(createdProduct.getId()))
				.isInstanceOf(EntityNotFoundException.class);
	}

}
