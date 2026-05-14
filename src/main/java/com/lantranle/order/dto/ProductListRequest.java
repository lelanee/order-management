package com.lantranle.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductListRequest {

  private String name;

  private Boolean active;

  private int page = 0;

  private int size = 20;
}
