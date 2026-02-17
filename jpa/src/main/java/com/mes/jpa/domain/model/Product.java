package com.mes.jpa.domain.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String productCode;

    @Column(nullable = false)
    private String productName;

    @Column
    private String category;

    @Column
    private String unit;

    @Column
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column
    private ProductStatus status;

    @Column
    private Integer stockQuantity;

    @Column
    private Integer minimumStock;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    public Product() {
    }

    public Product(String productCode, String productName, String category, String unit, Double price) {
        this.productCode = productCode;
        this.productName = productName;
        this.category = category;
        this.unit = unit;
        this.price = price;
        this.status = ProductStatus.ACTIVE;
        this.stockQuantity = 0;
        this.minimumStock = 10;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStock(Integer quantity) {
        this.stockQuantity = quantity;
    }

    public void setMinimumStock(Integer minimumStock) {
        this.minimumStock = minimumStock;
    }

    public boolean isLowStock() {
        return stockQuantity != null && stockQuantity < minimumStock;
    }

    public Long getId() {
        return id;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }

    public String getUnit() {
        return unit;
    }

    public Double getPrice() {
        return price;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public Integer getMinimumStock() {
        return minimumStock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public enum ProductStatus {
        ACTIVE, INACTIVE, DISCONTINUED
    }
}
