Feature: Material Inventory Management
  As a warehouse manager
  I want to manage material inventory
  So that I can track stock levels and receive alerts for low inventory

  Background:
    Given a material with id "MAT-001", name "Stainless Steel", type "RAW_MATERIAL"
    And initial stock of 100 KG
    And minimum stock threshold of 20 KG
    And supplier "SUP-001" named "Taiwan Steel"

  Scenario: Receive new material into inventory
    When I receive 50 KG of material
    Then the current stock should be 150 KG
    And a "MaterialReceivedEvent" should be published

  Scenario: Consume material from inventory
    When I consume 30 KG for work order "WO-001"
    Then the current stock should be 70 KG
    And a "MaterialConsumedEvent" should be published

  Scenario: Fail to consume due to insufficient stock
    When I try to consume 200 KG for work order "WO-001"
    Then an error "Insufficient stock" should be thrown

  Scenario: Receive low stock alert after consumption
    When I consume 85 KG for work order "WO-001"
    Then the current stock should be 15 KG
    And a "LowStockAlertEvent" should be published

  Scenario: No low stock alert when stock is sufficient
    When I consume 30 KG for work order "WO-001"
    Then the current stock should be 70 KG
    And no "LowStockAlertEvent" should be published

  Scenario: Adjust stock level manually
    When I adjust stock to 50 KG
    Then the current stock should be 50 KG

  Scenario: Update supplier information
    When I update supplier to "SUP-002" named "New Supplier"
    Then the supplier id should be "SUP-002"
    And the supplier name should be "New Supplier"

  Scenario: Reject null material name
    When I try to create material with null name
    Then an error should be thrown

  Scenario: Reject negative minimum stock
    When I try to create material with -10 minimum stock
    Then an error should be thrown
