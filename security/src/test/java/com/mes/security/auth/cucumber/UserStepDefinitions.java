package com.mes.security.auth.cucumber;

import com.mes.common.ddd.event.DomainEvent;
import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.common.exception.DomainException;
import com.mes.security.auth.domain.model.*;
import com.mes.security.auth.domain.event.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@CucumberContextConfiguration
@SpringBootTest(classes = com.mes.security.MesSecurityApplication.class)
public class UserStepDefinitions {

    private User user;
    private String username;
    private String email;
    private Exception thrownException;

    @Before
    public void setUp() {
        thrownException = null;
    }

    @Given("a user with username {string} and email {string}")
    public void aUserWithUsernameAndEmail(String username, String email) {
        this.username = username;
        this.email = email;
        user = new User(
                UserId.generate(),
                new Username(username),
                "$2a$10$encodedPassword",
                new Email(email)
        );
    }

    @When("I assign role {string} to the user")
    public void iAssignRoleToTheUser(String roleName) {
        user.assignRole(getRoleByName(roleName));
    }

    @When("I try to assign role {string} to the user")
    public void iTryToAssignRoleToTheUser(String roleName) {
        try {
            user.assignRole(getRoleByName(roleName));
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("I remove role {string} from the user")
    public void iRemoveRoleFromTheUser(String roleName) {
        user.removeRole(roleName);
    }

    @When("I change password to {string}")
    public void iChangePasswordTo(String newPassword) {
        user.changePassword(newPassword);
    }

    @When("I try to change password to {string}")
    public void iTryToChangePasswordTo(String newPassword) {
        try {
            user.changePassword(newPassword);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("I lock the user account with reason {string}")
    public void iLockTheUserAccountWithReason(String reason) {
        user.lock(reason);
    }

    @When("I try to lock the user account with reason {string}")
    public void iTryToLockTheUserAccountWithReason(String reason) {
        try {
            user.lock(reason);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("I unlock the user account")
    public void iUnlockTheUserAccount() {
        user.unlock();
    }

    @When("I try to unlock the user account")
    public void iTryToUnlockTheUserAccount() {
        try {
            user.unlock();
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the user should have username {string}")
    public void theUserShouldHaveUsername(String expectedUsername) {
        assertThat(user.getUsername().getValue()).isEqualTo(expectedUsername);
    }

    @Then("the user should have email {string}")
    public void theUserShouldHaveEmail(String expectedEmail) {
        assertThat(user.getEmail().getValue()).isEqualTo(expectedEmail);
    }

    @Then("the user should be enabled")
    public void theUserShouldBeEnabled() {
        assertThat(user.isEnabled()).isTrue();
    }

    @Then("the user should not be locked")
    public void theUserShouldNotBeLocked() {
        assertThat(user.isLocked()).isFalse();
    }

    @Then("the user should be locked")
    public void theUserShouldBeLocked() {
        assertThat(user.isLocked()).isTrue();
    }

    @Then("the user should have role {string}")
    public void theUserShouldHaveRole(String roleName) {
        Role role = getRoleByName(roleName);
        assertThat(user.getRoles()).contains(role);
    }

    @Then("the user should not have role {string}")
    public void theUserShouldNotHaveRole(String roleName) {
        Role role = getRoleByName(roleName);
        assertThat(user.getRoles()).doesNotContain(role);
    }

    @Then("the user should have encoded password {string}")
    public void theUserShouldHaveEncodedPassword(String expectedPassword) {
        assertThat(user.getEncodedPassword()).isEqualTo(expectedPassword);
    }

    @Then("the user should have permission {string} {string}")
    public void theUserShouldHavePermission(String resource, String action) {
        assertThat(user.hasPermission(resource, action)).isTrue();
    }

    @Then("the user should not have permission {string} {string}")
    public void theUserShouldNotHavePermission(String resource, String action) {
        assertThat(user.hasPermission(resource, action)).isFalse();
    }

    @Then("a {string} should be published")
    public void aShouldBePublished(String eventType) {
        List<DomainEvent> events = user.getDomainEvents();
        boolean found = events.stream().anyMatch(e -> e.getClass().getSimpleName().equals(eventType));
        assertThat(found).isTrue();
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

    private Role getRoleByName(String roleName) {
        switch (roleName) {
            case "ROLE_ADMIN":
                return Role.ROLE_ADMIN;
            case "ROLE_OPERATOR":
                return Role.ROLE_OPERATOR;
            case "ROLE_VIEWER":
                return Role.ROLE_VIEWER;
            default:
                throw new IllegalArgumentException("Unknown role: " + roleName);
        }
    }
}
