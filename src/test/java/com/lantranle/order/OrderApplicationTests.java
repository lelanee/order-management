package com.lantranle.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lantranle.order.controller.ProductController;
import com.lantranle.order.dto.OrderCreateRequest;
import com.lantranle.order.dto.OrderDetailResponse;
import com.lantranle.order.dto.OrderItemCreateRequest;
import com.lantranle.order.dto.PageResponse;
import com.lantranle.order.dto.ProductCreateRequest;
import com.lantranle.order.dto.ProductDetailResponse;
import com.lantranle.order.dto.ProductListRequest;
import com.lantranle.order.dto.ProductListResponse;
import com.lantranle.order.dto.ProductUpdateRequest;
import com.lantranle.order.entity.OrderStatus;
import com.lantranle.order.entity.Product;
import com.lantranle.order.repository.OrderRepository;
import com.lantranle.order.repository.ProductRepository;
import com.lantranle.order.service.OrderService;
import com.lantranle.order.service.ProductService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class OrderApplicationTests {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private ProductController productController;

	@Autowired
	private JdbcTemplate jdbcTemplate;

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
				.active(true)
				.build();

		ProductDetailResponse updatedProduct = productService.updateProduct(createdProduct.getId(), updateRequest);

		assertThat(updatedProduct.getName()).isEqualTo("Updated Service Product");
		assertThat(updatedProduct.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(20000));
		assertThat(updatedProduct.getActive()).isTrue();

		productService.deleteProduct(createdProduct.getId());

		assertThat(productService.getProductById(createdProduct.getId()).getActive()).isFalse();
		assertThat(productService.listActiveProductsForShop())
				.extracting(ProductListResponse::getId)
				.doesNotContain(createdProduct.getId());
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
		assertThat(productController.getProductById(createdProduct.getId()).getName())
				.isEqualTo("Controller Product");

		ProductUpdateRequest updateRequest = ProductUpdateRequest.builder()
				.name("Updated Controller Product")
				.description("Updated product for controller test")
				.price(BigDecimal.valueOf(35000))
				.stockQuantity(15)
				.active(true)
				.build();

		ProductDetailResponse updatedProduct = productController.updateProduct(createdProduct.getId(), updateRequest);

		assertThat(updatedProduct.getName()).isEqualTo("Updated Controller Product");
		assertThat(updatedProduct.getActive()).isTrue();

		productController.deleteProduct(createdProduct.getId());

		assertThat(productController.getProductById(createdProduct.getId()).getActive()).isFalse();
	}

	@Test
	void productServiceCanListProductsWithPaginationAndNameFilter() {
		ProductCreateRequest product = ProductCreateRequest.builder()
				.name("Coca Cola")
				.description("Product for pagination test")
				.price(BigDecimal.valueOf(12000))
				.stockQuantity(20)
				.active(true)
				.build();

		ProductDetailResponse createdProduct = productService.createProduct(product);

		ProductListRequest request = new ProductListRequest();
		request.setName("coca");
		request.setPage(0);
		request.setSize(100);

		PageResponse<ProductListResponse> response = productService.listProducts(request);

		assertThat(response.getPage()).isZero();
		assertThat(response.getSize()).isEqualTo(100);
		assertThat(response.getContent())
				.extracting(ProductListResponse::getId)
				.contains(createdProduct.getId());
	}

	@Test
	void productServiceSoftDeletesProduct() {
		ProductCreateRequest product = ProductCreateRequest.builder()
				.name("Soft Delete Product")
				.description("Product for soft delete test")
				.price(BigDecimal.valueOf(50000))
				.stockQuantity(3)
				.active(true)
				.build();

		ProductDetailResponse createdProduct = productService.createProduct(product);

		productService.deleteProduct(createdProduct.getId());

		assertThat(productService.getProductById(createdProduct.getId()).getActive()).isFalse();
		assertThat(productRepository.findById(createdProduct.getId())).isPresent();
		assertThat(jdbcTemplate.queryForObject(
				"select active from products where id = ?",
				Boolean.class,
				createdProduct.getId()
		)).isFalse();
	}

	@Test
	void orderServiceCanCreateOrderAndDecrementStock() {
		ProductDetailResponse product = productService.createProduct(ProductCreateRequest.builder()
				.name("Order Product")
				.description("Product for order happy path")
				.price(BigDecimal.valueOf(25000))
				.stockQuantity(7)
				.active(true)
				.build());

		OrderDetailResponse order = orderService.createOrder(orderRequest(product.getId(), 3));

		assertThat(order.getId()).isNotNull();
		assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
		assertThat(order.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(75000));
		assertThat(order.getItems()).hasSize(1);
		assertThat(productRepository.findById(product.getId()))
				.isPresent()
				.get()
				.extracting(Product::getStockQuantity)
				.isEqualTo(4);
	}

	@Test
	void orderServiceRejectsInsufficientStock() {
		ProductDetailResponse product = productService.createProduct(ProductCreateRequest.builder()
				.name("Low Stock Product")
				.description("Product for stock validation")
				.price(BigDecimal.valueOf(10000))
				.stockQuantity(1)
				.active(true)
				.build());

		assertThatThrownBy(() -> orderService.createOrder(orderRequest(product.getId(), 2)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Insufficient stock");

		assertThat(productRepository.findById(product.getId()))
				.isPresent()
				.get()
				.extracting(Product::getStockQuantity)
				.isEqualTo(1);
	}

	@Test
	void orderServiceRejectsEmptySelection() {
		OrderCreateRequest request = baseOrderRequest();
		request.setItems(List.of(OrderItemCreateRequest.builder()
				.productId(1L)
				.quantity(0)
				.build()));

		assertThatThrownBy(() -> orderService.createOrder(request))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Please select at least one product");
	}

	@Test
	void orderServiceCanUpdateStatusAndFilterOrders() {
		ProductDetailResponse product = productService.createProduct(ProductCreateRequest.builder()
				.name("Status Product")
				.description("Product for status workflow")
				.price(BigDecimal.valueOf(18000))
				.stockQuantity(5)
				.active(true)
				.build());
		OrderDetailResponse createdOrder = orderService.createOrder(orderRequest(product.getId(), 1));

		OrderDetailResponse updatedOrder = orderService.updateStatus(createdOrder.getId(), OrderStatus.CONFIRMED);

		assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
		assertThat(orderService.listOrders(OrderStatus.CONFIRMED))
				.extracting("id")
				.contains(createdOrder.getId());
		assertThat(orderRepository.findById(createdOrder.getId()))
				.isPresent()
				.get()
				.extracting(order -> order.getStatus())
				.isEqualTo(OrderStatus.CONFIRMED);
	}

	private OrderCreateRequest orderRequest(Long productId, Integer quantity) {
		OrderCreateRequest request = baseOrderRequest();
		request.setItems(List.of(OrderItemCreateRequest.builder()
				.productId(productId)
				.quantity(quantity)
				.build()));
		return request;
	}

	private OrderCreateRequest baseOrderRequest() {
		return OrderCreateRequest.builder()
				.customerName("Test Customer")
				.customerPhone("0909123456")
				.customerEmail("customer@example.com")
				.shippingAddress("123 Test Street")
				.note("Test note")
				.build();
	}

}
