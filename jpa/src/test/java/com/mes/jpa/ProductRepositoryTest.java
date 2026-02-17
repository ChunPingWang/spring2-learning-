package com.mes.jpa;

import com.mes.jpa.domain.model.Product;
import com.mes.jpa.domain.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Should save product and retrieve by id")
    void saveAndFindById_shouldWork() {
        Product product = new Product("P-001", "Test Product", "Electronics", "PCS", 100.0);
        Product saved = productRepository.save(product);

        Optional<Product> found = productRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getProductCode()).isEqualTo("P-001");
    }

    @Test
    @DisplayName("Should find product by product code")
    void findByProductCode_shouldWork() {
        Product product = new Product("P-002", "Product 2", "Hardware", "BOX", 50.0);
        productRepository.save(product);

        Optional<Product> found = productRepository.findByProductCode("P-002");

        assertThat(found).isPresent();
        assertThat(found.get().getProductName()).isEqualTo("Product 2");
    }

    @Test
    @DisplayName("Should find products by category")
    void findByCategory_shouldWork() {
        productRepository.save(new Product("P-003", "Product A", "Electronics", "PCS", 100.0));
        productRepository.save(new Product("P-004", "Product B", "Electronics", "PCS", 200.0));
        productRepository.save(new Product("P-005", "Product C", "Hardware", "BOX", 50.0));

        List<Product> electronics = productRepository.findByCategory("Electronics");

        assertThat(electronics).hasSize(2);
    }

    @Test
    @DisplayName("Should find low stock products")
    void findLowStockProducts_shouldWork() {
        Product lowStock = new Product("P-006", "Low Stock", "Electronics", "PCS", 100.0);
        lowStock.updateStock(5);
        lowStock.setMinimumStock(10);
        productRepository.save(lowStock);

        productRepository.save(new Product("P-007", "Normal Stock", "Electronics", "PCS", 100.0));

        List<Product> lowStockProducts = productRepository.findLowStockProducts();

        assertThat(lowStockProducts).hasSize(1);
        assertThat(lowStockProducts.get(0).getProductCode()).isEqualTo("P-006");
    }

    @Test
    @DisplayName("Should check if product code exists")
    void existsByProductCode_shouldWork() {
        productRepository.save(new Product("P-008", "Test", "Electronics", "PCS", 100.0));

        boolean exists = productRepository.existsByProductCode("P-008");
        boolean notExists = productRepository.existsByProductCode("P-999");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
