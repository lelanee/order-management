package com.lantranle.order.controller;

import com.lantranle.order.dto.ProductRequest;
import com.lantranle.order.entity.Product;
import com.lantranle.order.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

  private final ProductService productService;

  @GetMapping
  public String listProducts(
    @RequestParam(required = false) String name,
    @RequestParam(required = false) Boolean active,
    @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
    Model model
  ) {
    Page<Product> page = productService.listProductsForAdmin(pageable, name, active);

    model.addAttribute("page", page);
    model.addAttribute("name", name);
    model.addAttribute("active", active);
    return "admin/products";
  }

  @GetMapping("/new")
  public String getCreateProductPage(Model model) {
    if (!model.containsAttribute("product")) {
      model.addAttribute("product", ProductRequest.builder().active(true).build());
    }

    model.addAttribute("formAction", "/admin/products");
    model.addAttribute("pageTitle", "Add product");
    return "admin/product-form";
  }

  @PostMapping
  public String createProduct(
    @Valid @ModelAttribute("product") ProductRequest request,
    BindingResult bindingResult,
    Model model,
    RedirectAttributes redirectAttributes
  ) {
    request.setActive(Boolean.TRUE.equals(request.getActive()));
    if (bindingResult.hasErrors()) {
      model.addAttribute("formAction", "/admin/products");
      model.addAttribute("pageTitle", "Add product");
      return "admin/product-form";
    }

    productService.createProduct(request);
    redirectAttributes.addFlashAttribute("successMessage", "Product created successfully");
    return "redirect:/admin/products";
  }

  @GetMapping("/{id}")
  public String getProductDetail(@PathVariable Long id, Model model) {
    model.addAttribute("product", productService.getProductById(id));
    return "admin/product-detail";
  }

  @GetMapping("/{id}/edit")
  public String getEditProductPage(@PathVariable Long id, Model model) {
    if (!model.containsAttribute("product")) {
      model.addAttribute("product", toRequest(productService.getProductById(id)));
    }

    model.addAttribute("productId", id);
    model.addAttribute("formAction", "/admin/products/" + id);
    model.addAttribute("pageTitle", "Edit product");
    return "admin/product-form";
  }

  @PostMapping("/{id}")
  public String updateProduct(
    @PathVariable Long id,
    @Valid @ModelAttribute("product") ProductRequest request,
    BindingResult bindingResult,
    Model model,
    RedirectAttributes redirectAttributes
  ) {
    request.setActive(Boolean.TRUE.equals(request.getActive()));
    if (bindingResult.hasErrors()) {
      model.addAttribute("productId", id);
      model.addAttribute("formAction", "/admin/products/" + id);
      model.addAttribute("pageTitle", "Edit product");
      return "admin/product-form";
    }

    productService.updateProduct(id, request);
    redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully");
    return "redirect:/admin/products/" + id;
  }

  @PostMapping("/{id}/delete")
  public String deactivateProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    productService.deactivateProduct(id);
    redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully");
    return "redirect:/admin/products";
  }

  private ProductRequest toRequest(Product product) {
    return ProductRequest.builder()
      .name(product.getName())
      .description(product.getDescription())
      .price(product.getPrice())
      .stockQuantity(product.getStockQuantity())
      .imageUrl(product.getImageUrl())
      .active(product.getActive())
      .build();
  }
}
