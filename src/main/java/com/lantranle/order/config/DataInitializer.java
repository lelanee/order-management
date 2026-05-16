package com.lantranle.order.config;

import com.lantranle.order.entity.User;
import com.lantranle.order.entity.UserRole;
import com.lantranle.order.entity.Product;
import com.lantranle.order.repository.ProductRepository;
import com.lantranle.order.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.demo.enabled", havingValue = "true")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final JdbcTemplate jdbcTemplate;
  private final PasswordEncoder passwordEncoder;

  private static final Set<String> OLD_DEMO_PRODUCT_NAMES = Set.of(
    "Test Product",
    "Coca Cola",
    "Coca Manual Check",
    "Updated Service Product",
    "Updated Controller Product",
    "Soft Delete Product"
  );

  private static final List<ProductSeed> SEVEN_ELEVEN_PRODUCTS = List.of(
    new ProductSeed("Phin Cà Phê 7-Eleven", "UPC 2820186070011 · Bộ phin cà phê thương hiệu 7-Eleven.", 99000, 30, "/images/products/phin-ca-phe.webp"),
    new ProductSeed("Đế Lót Ly 7-Eleven Always Open - Gói 5 Cái", "UPC 2600193570014 · Bộ 5 đế lót ly 7-Eleven Always Open.", 18000, 40, "/images/products/de-lot-ly.webp"),
    new ProductSeed("Bình Giữ Nhiệt 7-Eleven 950ml", "UPC 739205327741 · Bình giữ nhiệt 950ml thương hiệu 7-Eleven.", 350000, 20, "/images/products/binh-giu-nhiet.webp"),
    new ProductSeed("Nón Kết 7-Eleven Always Open Trắng", "UPC 2600193590012 · Nón kết trắng 7-Eleven Always Open.", 149000, 25, "/images/products/non-ket.webp"),
    new ProductSeed("Giấy Thơm Treo Ô Tô 7-Eleven", "UPC 739205327796 · Giấy thơm treo ô tô thương hiệu 7-Eleven.", 25000, 35, "/images/products/giay-thom-treo-oto.webp"),
    new ProductSeed("Pin Cài Summies 1 Cái", "7Collection · Pin cài Summies độc quyền 7-Eleven.", 60000, 30, "/images/products/pin.webp"),
    new ProductSeed("Phụ Kiện Móc Khóa Điện Thoại Summies GG", "7Collection · Phụ kiện móc khóa điện thoại Summies GG.", 115000, 25, "/images/products/phu-kien-moc.webp"),
    new ProductSeed("Phụ Kiện Dây Treo Điện Thoại Summies GG", "7Collection · Dây treo điện thoại Summies GG.", 115000, 25, "/images/products/phu-kien-day.webp"),
    new ProductSeed("Nam Châm Trang Trí 7-Eleven 8x6cm", "7Collection · Nam châm trang trí 7-Eleven kích thước 8x6cm.", 35000, 45, "/images/products/nam-cham.webp"),
    new ProductSeed("Postcard 7-Eleven Bánh Mì Thịt Chả", "7Collection · Postcard chủ đề bánh mì thịt chả.", 19000, 60, "/images/products/giay-thom.webp"),
    new ProductSeed("Postcard 7-Eleven Cà Phê Sữa Đá", "7Collection · Postcard chủ đề cà phê sữa đá.", 19000, 60, "/images/products/postcard-ca-phe.webp"),
    new ProductSeed("Gối Cổ Chữ U 7-Eleven Always Open", "7Collection · Gối cổ chữ U 7-Eleven Always Open.", 90000, 30, "/images/products/goi-om.webp"),
    new ProductSeed("Nón Lá 7-Eleven Việt Nam Size 33", "7Collection · Nón lá 7-Eleven Việt Nam size 33.", 115000, 20, "/images/products/non-33.webp"),
    new ProductSeed("Nón Lá 7-Eleven Việt Nam Size 40", "7Collection · Nón lá 7-Eleven Việt Nam size 40.", 127000, 20, "/images/products/non-40.webp"),
    new ProductSeed("Quạt Vải Cầm Tay 7-Eleven Việt Nam", "7Collection · Quạt vải cầm tay 7-Eleven Việt Nam.", 35000, 45, "/images/products/quat-cam-tay.webp"),
    new ProductSeed("Nón Kết 7-Eleven Always Open Đen", "7Collection · Nón kết 7-Eleven Always Open màu đen.", 149000, 25, "/images/products/non.webp"),
    new ProductSeed("Dù Trong Suốt 7-Eleven Always Open", "7Collection · Dù trong suốt 7-Eleven Always Open.", 139000, 20, "/images/products/du-trong-suot.webp"),
    new ProductSeed("Magnet Sticker 7-Eleven Bộ 4 Cái", "7Collection · Bộ 4 magnet sticker 7-Eleven.", 45000, 45, "/images/products/manget-sticker.webp"),
    new ProductSeed("Sticker 7-Eleven Sheet A6", "7Collection · Sticker 7-Eleven sheet A6.", 25000, 60, "/images/products/sticker-sheet.webp"),
    new ProductSeed("Sticker Pack 7-Eleven Bộ 12 Cái", "7Collection · Bộ 12 sticker 7-Eleven.", 55000, 45, "/images/products/stick-pack.webp"),
    new ProductSeed("Phụ Kiện Trang Trí 7-Eleven Sài Gòn", "7Collection · Phụ kiện trang trí 7-Eleven Sài Gòn.", 69000, 35, "/images/products/phu-kien-trang-tri.webp"),
    new ProductSeed("Móc Khóa Acrylic 7-Eleven Vietnam 2 Cái", "7Collection · Bộ 2 móc khóa acrylic 7-Eleven Vietnam.", 45000, 45, "/images/products/moc-khoa-acrylic.webp"),
    new ProductSeed("Móc Khóa 7-Eleven Xích Lô Việt Nam", "7Collection · Móc khóa 7-Eleven chủ đề xích lô Việt Nam.", 49000, 45, "/images/products/moc-khoa-xich-lo.webp"),
    new ProductSeed("Móc Khóa 7-Eleven Cà Phê Việt Nam", "7Collection · Móc khóa 7-Eleven chủ đề cà phê Việt Nam.", 49000, 45, "/images/products/moc-khoa-ca-phe.webp"),
    new ProductSeed("Ly Giữ Nhiệt 7-Eleven Trắng 735ml", "7Collection · Ly giữ nhiệt 7-Eleven màu trắng dung tích 735ml.", 350000, 20, "/images/products/ly-giu-nhiet-trang.webp"),
    new ProductSeed("Ly Giữ Nhiệt 7-Eleven Xám 735ml", "7Collection · Ly giữ nhiệt 7-Eleven màu xám dung tích 735ml.", 350000, 20, "/images/products/ly-giu-nhiet-xam.webp"),
    new ProductSeed("Thiệp Nổi Xoắn 7-Eleven 13x13", "7Collection · Thiệp nổi xoắn 7-Eleven kích thước 13x13.", 55000, 35, "/images/products/giay-thom.webp"),
    new ProductSeed("Postcard Xoắn 7-Eleven", "7Collection · Postcard xoắn 7-Eleven.", 50000, 35, "/images/products/giay-thom.webp"),
    new ProductSeed("Magnet Việt Nam Thu Nhỏ 6x8cm", "7Collection · Magnet Việt Nam thu nhỏ kích thước 6x8cm.", 51000, 35, "/images/products/giay-thom.webp"),
    new ProductSeed("Móc Khóa Nón Lá Việt Nam 7-Eleven", "7Collection · Móc khóa nón lá Việt Nam 7-Eleven.", 70000, 30, "/images/products/non.webp"),
    new ProductSeed("Quạt Vải Cầm Tay 7-Eleven Màu Xanh", "7Collection · Quạt vải cầm tay 7-Eleven màu xanh.", 69000, 35, "/images/products/giay-thom.webp"),
    new ProductSeed("Quạt Vải Cầm Tay 7-Eleven Màu Đỏ", "7Collection · Quạt vải cầm tay 7-Eleven màu đỏ.", 69000, 35, "/images/products/giay-thom.webp"),
    new ProductSeed("Sổ Tay Lò Xo A5 7-Eleven", "7Collection · Sổ tay lò xo A5 7-Eleven.", 65000, 40, "/images/products/giay-thom.webp"),
    new ProductSeed("Sticker Summie 7-Eleven 3D", "7Collection · Sticker Summie 7-Eleven 3D.", 69000, 40, "/images/products/giay-thom.webp"),
    new ProductSeed("Sticker Summie 7-Eleven 2D", "7Collection · Sticker Summie 7-Eleven 2D.", 20000, 60, "/images/products/giay-thom.webp"),
    new ProductSeed("Bộ Pin Cài Summie 7-Eleven 7 Cái", "7Collection · Bộ 7 pin cài Summie 7-Eleven.", 160000, 20, "/images/products/giay-thom.webp"),
    new ProductSeed("Móc Khóa Summie 7-Eleven", "7Collection · Móc khóa Summie 7-Eleven.", 55000, 40, "/images/products/giay-thom.webp")
  );

  @Value("${app.demo.admin.username}")
  private String demoAdminUsername;

  @Value("${app.demo.admin.email}")
  private String demoAdminEmail;

  @Value("${app.demo.admin.password}")
  private String demoAdminPassword;

  @Value("${app.demo.admin.full-name}")
  private String demoAdminFullName;

  @Value("${app.demo.user.username}")
  private String demoUserUsername;

  @Value("${app.demo.user.email}")
  private String demoUserEmail;

  @Value("${app.demo.user.password}")
  private String demoUserPassword;

  @Value("${app.demo.user.full-name}")
  private String demoUserFullName;

  @Override
  public void run(String... args) {
    upsertDemoUser(demoAdminUsername, demoAdminEmail, demoAdminPassword, demoAdminFullName, UserRole.ADMIN);
    upsertDemoUser(demoUserUsername, demoUserEmail, demoUserPassword, demoUserFullName, UserRole.USER);
    initializeProductVersions();
    deleteOldDemoProducts();
    seedSevenElevenProducts();
  }

  private void upsertDemoUser(String username, String email, String password, String fullName, UserRole role) {
    User user = userRepository.findByUsername(username)
      .orElseGet(() -> User.builder().username(username).build());

    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    user.setFullName(fullName);
    user.setRole(role);
    user.setActive(true);
    userRepository.save(user);
  }

  private void initializeProductVersions() {
    jdbcTemplate.update("update products set version = 0 where version is null");
  }

  private void seedSevenElevenProducts() {
    SEVEN_ELEVEN_PRODUCTS.forEach(product -> upsertProduct(
      product.name(),
      product.description(),
      product.price(),
      product.stockQuantity(),
      product.imageUrl()
    ));
  }

  private void upsertProduct(String name, String description, int price, int stockQuantity, String imageUrl) {
    Product product = productRepository.findByName(name)
      .orElseGet(() -> Product.builder().name(name).build());

    product.setDescription(description);
    product.setPrice(BigDecimal.valueOf(price));
    product.setStockQuantity(stockQuantity);
    product.setImageUrl(imageUrl);
    product.setActive(true);
    productRepository.save(product);
  }

  private void deleteOldDemoProducts() {
    productRepository.findAll().stream()
      .filter(product -> OLD_DEMO_PRODUCT_NAMES.contains(product.getName()))
      .forEach(productRepository::delete);
  }

  private record ProductSeed(String name, String description, int price, int stockQuantity, String imageUrl) {
  }
}
