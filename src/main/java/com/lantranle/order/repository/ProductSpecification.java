package com.lantranle.order.repository;

import com.lantranle.order.dto.ProductListRequest;
import com.lantranle.order.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> filterBy(ProductListRequest request) {
        return hasName(request.getName())
                .and(hasActive(request.getActive()));
    }

    private static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(name)) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.trim().toLowerCase() + "%"
            );
        };
    }

    private static Specification<Product> hasActive(Boolean active) {
        return (root, query, criteriaBuilder) -> {
            if (active == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("active"), active);
        };
    }
}
