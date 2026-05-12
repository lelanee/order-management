package com.lantranle.order.dto;

import java.math.BigDecimal;
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
public class ProductListResponse {

    private Long id;

    private String name;

    private BigDecimal price;

    private Integer stockQuantity;

    private String imageUrl;

    private Boolean active;
}
