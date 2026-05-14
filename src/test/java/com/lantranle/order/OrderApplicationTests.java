package com.lantranle.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lantranle.order.dto.OrderCreateRequest;
import com.lantranle.order.dto.OrderItemCreateRequest;
import com.lantranle.order.dto.ProductRequest;
import com.lantranle.order.dto.RegisterRequest;
import com.lantranle.order.entity.Order;
import com.lantranle.order.entity.OrderStatus;
import com.lantranle.order.entity.Product;
import com.lantranle.order.entity.UserRole;
import com.lantranle.order.repository.UserRepository;
import com.lantranle.order.controller.RegistrationController;
import com.lantranle.order.repository.OrderRepository;
import com.lantranle.order.repository.ProductRepository;
import com.lantranle.order.service.OrderService;
import com.lantranle.order.service.ProductService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;

@SpringBootTest
class OrderApplicationTests {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RegistrationController registrationController;

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderService orderService;

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
	void registrationCreatesActiveUserAccount() {
		RegisterRequest request = RegisterRequest.builder()
				.username("newuser" + System.nanoTime())
				.email("newuser" + System.nanoTime() + "@example.com")
				.password("secret123")
				.fullName("New User")
				.phoneNumber("0909000000")
				.build();
		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "registerRequest");

		String view = registrationController.register(request, bindingResult, new MockHttpServletRequest());

		assertThat(view).isEqualTo("redirect:/");
		assertThat(bindingResult.hasErrors()).isFalse();
		assertThat(userRepository.findByUsername(request.getUsername()))
				.isPresent()
				.get()
				.satisfies(user -> {
					assertThat(user.getRole()).isEqualTo(UserRole.USER);
					assertThat(user.getActive()).isTrue();
					assertThat(user.getPassword()).isNotEqualTo("secret123");
				});
	}

	@Test
	void registrationRejectsDuplicateUsername() {
		RegisterRequest request = RegisterRequest.builder()
				.username("admin")
				.email("admin-copy" + System.nanoTime() + "@example.com")
				.password("secret123")
				.fullName("Admin Copy")
				.build();
		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "registerRequest");

		String view = registrationController.register(request, bindingResult, new MockHttpServletRequest());

		assertThat(view).isEqualTo("register");
		assertThat(bindingResult.hasFieldErrors("username")).isTrue();
	}

	@Test
	void productServiceCanCreateUpdateAndDeleteProduct() {
		ProductRequest product = ProductRequest.builder()
				.name("Service Product")
				.description("Product for service test")
				.price(BigDecimal.valueOf(15000))
				.stockQuantity(5)
				.active(true)
				.build();

		Product createdProduct = productService.createProduct(product);

		assertThat(createdProduct.getId()).isNotNull();

		ProductRequest updateRequest = ProductRequest.builder()
				.name("Updated Service Product")
				.description("Updated product for service test")
				.price(BigDecimal.valueOf(20000))
				.stockQuantity(8)
				.imageUrl("https://example.com/updated-product.jpg")
				.active(true)
				.build();

		Product updatedProduct = productService.updateProduct(createdProduct.getId(), updateRequest);

		assertThat(updatedProduct.getName()).isEqualTo("Updated Service Product");
		assertThat(updatedProduct.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(20000));
		assertThat(updatedProduct.getActive()).isTrue();

		productService.deleteProduct(createdProduct.getId());

		assertThat(productService.getProductById(createdProduct.getId()).getActive()).isFalse();
		assertThat(productService.listActiveProductsForShop())
				.extracting(Product::getId)
				.doesNotContain(createdProduct.getId());
	}

	@Test
	void productServiceCanListProductsWithPaginationAndNameFilter() {
		ProductRequest product = ProductRequest.builder()
				.name("Coca Cola")
				.description("Product for pagination test")
				.price(BigDecimal.valueOf(12000))
				.stockQuantity(20)
				.active(true)
				.build();

		Product createdProduct = productService.createProduct(product);

		Page<Product> response = productService.listProductsForAdmin(
				PageRequest.of(0, 100, Sort.by("id").ascending()),
				"coca",
				null
		);

		assertThat(response.getNumber()).isZero();
		assertThat(response.getSize()).isEqualTo(100);
		assertThat(response.getContent())
				.extracting(Product::getId)
				.contains(createdProduct.getId());
	}

	@Test
	void productServiceSoftDeletesProduct() {
		ProductRequest product = ProductRequest.builder()
				.name("Soft Delete Product")
				.description("Product for soft delete test")
				.price(BigDecimal.valueOf(50000))
				.stockQuantity(3)
				.active(true)
				.build();

		Product createdProduct = productService.createProduct(product);

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
		Product product = productService.createProduct(ProductRequest.builder()
				.name("Order Product")
				.description("Product for order happy path")
				.price(BigDecimal.valueOf(25000))
				.stockQuantity(7)
				.active(true)
				.build());

		Order order = orderService.createOrder(orderRequest(product.getId(), 3));

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
		Product product = productService.createProduct(ProductRequest.builder()
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
		Product product = productService.createProduct(ProductRequest.builder()
				.name("Status Product")
				.description("Product for status workflow")
				.price(BigDecimal.valueOf(18000))
				.stockQuantity(5)
				.active(true)
				.build());
		Order createdOrder = orderService.createOrder(orderRequest(product.getId(), 1));

		Order updatedOrder = orderService.updateStatus(createdOrder.getId(), OrderStatus.CONFIRMED);

		assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
		assertThat(orderService.listOrders(OrderStatus.CONFIRMED))
				.extracting(Order::getId)
				.contains(createdOrder.getId());
		assertThat(orderRepository.findById(createdOrder.getId()))
				.isPresent()
				.get()
				.extracting(Order::getStatus)
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
