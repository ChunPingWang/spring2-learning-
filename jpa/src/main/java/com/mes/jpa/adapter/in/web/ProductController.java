package com.mes.jpa.adapter.in.web;

import com.mes.jpa.application.ProductService;
import com.mes.jpa.application.dto.ProductView;
import com.mes.jpa.domain.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductView> createProduct(@RequestBody CreateProductRequest request) {
        Product product = productService.createProduct(
                request.getProductCode(),
                request.getProductName(),
                request.getCategory(),
                request.getUnit(),
                request.getPrice()
        );
        return ResponseEntity.ok(ProductView.fromDomain(product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductView> getProduct(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(p -> ResponseEntity.ok(ProductView.fromDomain(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProductView>> getAllProducts(
            @RequestParam(required = false) String category) {
        List<ProductView> products;
        if (category != null) {
            products = productService.getProductsByCategory(category).stream()
                    .map(ProductView::fromDomain)
                    .collect(Collectors.toList());
        } else {
            products = productService.getAllProducts().stream()
                    .map(ProductView::fromDomain)
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductView>> getLowStockProducts() {
        List<ProductView> products = productService.getLowStockProducts().stream()
                .map(ProductView::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductView> updateStock(@PathVariable Long id, 
                                                    @RequestBody UpdateStockRequest request) {
        Product product = productService.updateStock(id, request.getQuantity());
        return ResponseEntity.ok(ProductView.fromDomain(product));
    }

    public static class CreateProductRequest {
        private String productCode;
        private String productName;
        private String category;
        private String unit;
        private Double price;

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }
    }

    public static class UpdateStockRequest {
        private Integer quantity;

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
