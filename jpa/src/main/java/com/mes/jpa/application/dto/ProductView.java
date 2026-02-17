package com.mes.jpa.application.dto;

import com.mes.jpa.domain.model.Product;

import java.time.LocalDateTime;

public class ProductView {
    private Long id;
    private String productCode;
    private String productName;
    private String category;
    private String unit;
    private Double price;
    private String status;
    private Integer stockQuantity;
    private Integer minimumStock;
    private boolean lowStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductView() {
    }

    public static ProductView fromDomain(Product product) {
        ProductView view = new ProductView();
        view.id = product.getId();
        view.productCode = product.getProductCode();
        view.productName = product.getProductName();
        view.category = product.getCategory();
        view.unit = product.getUnit();
        view.price = product.getPrice();
        view.status = product.getStatus().name();
        view.stockQuantity = product.getStockQuantity();
        view.minimumStock = product.getMinimumStock();
        view.lowStock = product.isLowStock();
        view.createdAt = product.getCreatedAt();
        view.updatedAt = product.getUpdatedAt();
        return view;
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

    public String getStatus() {
        return status;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public Integer getMinimumStock() {
        return minimumStock;
    }

    public boolean isLowStock() {
        return lowStock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
