package com.lantranle.order.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

  @NotBlank(message = "Product name is required")
  @Size(max = 150, message = "Product name must not exceed 150 characters")
  private String name;

  @Size(max = 1000, message = "Description must not exceed 1000 characters")
  private String description;

  @NotNull(message = "Price is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
  private BigDecimal price;

  @NotNull(message = "Stock quantity is required")
  @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
  private Integer stockQuantity;

  @Size(max = 500, message = "Image URL must not exceed 500 characters")
  private String imageUrl;

  private Boolean active;
}
