Feature: User Management
  As a system administrator
  I want to manage user accounts
  So that I can control access to the system

  Background:
    Given a user with username "test_user" and email "test@example.com"

  Scenario: Create a new user
    Then the user should have username "test_user"
    And the user should have email "test@example.com"
    And the user should be enabled
    And the user should not be locked
    And a "UserCreatedEvent" should be published

  Scenario: Assign role to user
    When I assign role "ROLE_ADMIN" to the user
    Then the user should have role "ROLE_ADMIN"
    And a "RoleAssignedEvent" should be published

  Scenario: Assign duplicate role should fail
    Given I assign role "ROLE_ADMIN" to the user
    When I try to assign role "ROLE_ADMIN" to the user
    Then an error "already has role" should be thrown

  Scenario: Remove existing role
    Given I assign role "ROLE_ADMIN" to the user
    And I assign role "ROLE_OPERATOR" to the user
    When I remove role "ADMIN" from the user
    Then the user should have role "ROLE_OPERATOR"
    And the user should not have role "ROLE_ADMIN"

  Scenario: Change user password
    When I change password to "$2a$10$newEncodedPassword"
    Then the user should have encoded password "$2a$10$newEncodedPassword"

  Scenario: Change password to blank should fail
    When I try to change password to ""
    Then an error should be thrown

  Scenario: Lock user account
    When I lock the user account with reason "Suspicious activity"
    Then the user should be locked
    And a "UserLockedEvent" should be published

  Scenario: Lock already locked account should fail
    Given I lock the user account with reason "First lock"
    When I try to lock the user account with reason "Second lock"
    Then an error "already locked" should be thrown

  Scenario: Unlock locked account
    Given I lock the user account with reason "Test lock"
    When I unlock the user account
    Then the user should not be locked

  Scenario: Unlock non-locked account should fail
    When I try to unlock the user account
    Then an error "not locked" should be thrown

  Scenario: User with OPERATOR role has READ permission
    Given I assign role "ROLE_OPERATOR" to the user
    Then the user should have permission "WORK_ORDER" "READ"

  Scenario: User with VIEWER role does not have DELETE permission
    Given I assign role "ROLE_VIEWER" to the user
    Then the user should not have permission "WORK_ORDER" "DELETE"

  Scenario: User without roles does not have any permission
    Then the user should not have permission "WORK_ORDER" "READ"
