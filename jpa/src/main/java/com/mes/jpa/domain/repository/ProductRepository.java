package com.mes.jpa.domain.repository;

import com.mes.jpa.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByProductCode(String productCode);

    List<Product> findByCategory(String category);

    List<Product> findByStatus(Product.ProductStatus status);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity < p.minimumStock")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.status = :status")
    List<Product> findByCategoryAndStatus(@Param("category") String category, 
                                          @Param("status") Product.ProductStatus status);

    boolean existsByProductCode(String productCode);
}
