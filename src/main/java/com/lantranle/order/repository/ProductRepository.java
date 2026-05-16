package com.lantranle.order.repository;

import com.lantranle.order.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query("""
    select product
    from Product product
    where (:name is null or :name = '' or lower(product.name) like lower(concat('%', :name, '%')))
      and (:active is null or product.active = :active)
    """)
  Page<Product> search(String name, Boolean active, Pageable pageable);

  List<Product> findByActiveTrueOrderByIdAsc();

  Page<Product> findByActiveTrueOrderByIdAsc(Pageable pageable);

  Optional<Product> findByName(String name);
}
