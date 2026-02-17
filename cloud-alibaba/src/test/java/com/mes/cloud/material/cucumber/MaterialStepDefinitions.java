package com.mes.cloud.material.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import com.mes.cloud.material.domain.*;
import com.mes.cloud.material.domain.event.*;
import com.mes.common.ddd.event.DomainEvent;
import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.common.exception.DomainException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@CucumberContextConfiguration
@SpringBootTest(classes = com.mes.cloud.MesCloudAlibabaApplication.class)
public class MaterialStepDefinitions {

    private Material material;
    private MaterialId materialId;
    private String materialName;
    private MaterialType materialType;
    private MaterialUnit materialUnit;
    private StockLevel stockLevel;
    private int minimumStock;
    private Supplier supplier;
    private Exception thrownException;

    @Before
    public void setUp() {
        thrownException = null;
    }

    @Given("a material with id {string}, name {string}, type {string}")
    public void aMaterialWithIdNameType(String id, String name, String type) {
        this.materialId = MaterialId.of(id);
        this.materialName = name;
        this.materialType = MaterialType.valueOf(type);
    }

    @And("initial stock of {int} KG")
    public void initialStockOfKG(int quantity) {
        this.stockLevel = new StockLevel(quantity, "KG");
        this.materialUnit = MaterialUnit.KG;
    }

    @And("minimum stock threshold of {int} KG")
    public void minimumStockThresholdOfKG(int minStock) {
        this.minimumStock = minStock;
    }

    @And("supplier {string} named {string}")
    public void supplierNamed(String supplierId, String supplierName) {
        this.supplier = new Supplier(supplierId, supplierName, "02-12345678");
    }

    @When("I receive {int} KG of material")
    public void iReceiveKGOfMaterial(int quantity) {
        createMaterialIfNeeded();
        material.receive(quantity);
    }

    @When("I consume {int} KG for work order {string}")
    public void iConsumeKGForWorkOrder(int quantity, String workOrderId) {
        createMaterialIfNeeded();
        material.consume(quantity, workOrderId);
    }

    @When("I try to consume {int} KG for work order {string}")
    public void iTryToConsumeKGForWorkOrder(int quantity, String workOrderId) {
        createMaterialIfNeeded();
        try {
            material.consume(quantity, workOrderId);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("I adjust stock to {int} KG")
    public void iAdjustStockToKG(int newLevel) {
        createMaterialIfNeeded();
        material.adjustStock(newLevel);
    }

    @When("I update supplier to {string} named {string}")
    public void iUpdateSupplierToNamed(String supplierId, String supplierName) {
        createMaterialIfNeeded();
        Supplier newSupplier = new Supplier(supplierId, supplierName, "03-87654321");
        material.updateSupplier(newSupplier);
    }

    @When("I try to create material with null name")
    public void iTryToCreateMaterialWithNullName() {
        try {
            new Material(materialId, null, materialType, materialUnit, stockLevel, minimumStock, supplier);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("I try to create material with {int} minimum stock")
    public void iTryToCreateMaterialWithNegativeMinimumStock(int minStock) {
        try {
            new Material(materialId, materialName, materialType, materialUnit, stockLevel, minStock, supplier);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the current stock should be {int} KG")
    public void theCurrentStockShouldBeKG(int expectedStock) {
        assertThat(material.getStockLevel().getCurrentQuantity()).isEqualTo(expectedStock);
    }

    @Then("a {string} should be published")
    public void aShouldBePublished(String eventType) {
        List<DomainEvent> events = material.getDomainEvents();
        boolean found = events.stream().anyMatch(e -> e.getClass().getSimpleName().equals(eventType));
        assertThat(found).isTrue();
    }

    @Then("no {string} should be published")
    public void noShouldBePublished(String eventType) {
        List<DomainEvent> events = material.getDomainEvents();
        boolean found = events.stream().anyMatch(e -> e.getClass().getSimpleName().equals(eventType));
        assertThat(found).isFalse();
    }

    @Then("an error {string} should be thrown")
    public void anErrorShouldBeThrown(String errorMessage) {
        assertThat(thrownException).isNotNull();
        assertThat(thrownException.getMessage()).contains(errorMessage);
    }

    @Then("an error should be thrown")
    public void anErrorShouldBeThrown() {
        assertThat(thrownException).isInstanceOfAny(DomainException.class, BusinessRuleViolationException.class);
    }

    @Then("the supplier id should be {string}")
    public void theSupplierIdShouldBe(String expectedSupplierId) {
        assertThat(material.getSupplier().getSupplierId()).isEqualTo(expectedSupplierId);
    }

    @Then("the supplier name should be {string}")
    public void theSupplierNameShouldBe(String expectedSupplierName) {
        assertThat(material.getSupplier().getSupplierName()).isEqualTo(expectedSupplierName);
    }

    private void createMaterialIfNeeded() {
        if (material == null) {
            material = new Material(materialId, materialName, materialType,
                    materialUnit, stockLevel, minimumStock, supplier);
        }
    }
}
