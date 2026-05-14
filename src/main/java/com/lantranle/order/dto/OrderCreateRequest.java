package com.lantranle.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
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
public class OrderCreateRequest {

  @NotBlank(message = "Customer name is required")
  @Size(max = 150, message = "Customer name must not exceed 150 characters")
  private String customerName;

  @NotBlank(message = "Customer phone is required")
  @Size(max = 20, message = "Customer phone must not exceed 20 characters")
  private String customerPhone;

  @Email(message = "Customer email must be valid")
  @Size(max = 150, message = "Customer email must not exceed 150 characters")
  private String customerEmail;

  @NotBlank(message = "Shipping address is required")
  @Size(max = 500, message = "Shipping address must not exceed 500 characters")
  private String shippingAddress;

  @Size(max = 1000, message = "Note must not exceed 1000 characters")
  private String note;

  @Valid
  @Builder.Default
  private List<OrderItemCreateRequest> items = new ArrayList<>();
}
