package com.lantranle.order.controller;

import com.lantranle.order.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ProductPageController {

    private final ProductService productService;

    @GetMapping({"/", "/products"})
    public String getProductsPage(Model model) {
        model.addAttribute("products", productService.getProducts());
        return "products";
    }
}
