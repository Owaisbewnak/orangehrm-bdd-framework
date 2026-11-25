package com.owais.orangehrm.stepdefs;

import com.owais.orangehrm.config.ConfigReader;
import com.owais.orangehrm.config.DriverManager;
import com.owais.orangehrm.pages.DashboardPage;
import com.owais.orangehrm.pages.LoginPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class LoginSteps {

    private LoginPage loginPage() {
        return new LoginPage(DriverManager.getDriver());
    }

    private DashboardPage dashboardPage() {
        return new DashboardPage(DriverManager.getDriver());
    }

    @Given("I am on the OrangeHRM login page")
    public void iAmOnTheLoginPage() {
        loginPage().open(ConfigReader.baseUrl());
        Assert.assertTrue(loginPage().isLoginPageDisplayed(), "Login page was not displayed");
    }

    @When("I login with username {string} and password {string}")
    public void iLoginWithCredentials(String username, String password) {
        loginPage().login(username, password);
    }

    @When("I submit the login form without entering credentials")
    public void iSubmitTheLoginFormWithoutCredentials() {
        loginPage().loginWithoutCredentials();
    }

    @When("I attempt to login with an invalid password {int} times")
    public void iAttemptToLoginWithAnInvalidPasswordTimes(int attempts) {
        for (int i = 1; i <= attempts; i++) {
            loginPage().login("Admin", "wrongPassword123");
            String alert = loginPage().getAlertText();
            Assert.assertNotNull(alert, "Expected an error banner after failed login #" + i);
        }
    }

    @Then("I should see the Dashboard page")
    public void iShouldSeeTheDashboardPage() {
        Assert.assertTrue(dashboardPage().isDashboardDisplayed(),
                "Dashboard page was not displayed after login");
    }

    @And("the logged-in user name should be displayed")
    public void theLoggedInUserNameShouldBeDisplayed() {
        String actualUser = dashboardPage().getLoggedInUserName();
        Assert.assertNotNull(actualUser, "No logged-in user name was shown");
        Assert.assertFalse(actualUser.isBlank(), "Logged-in user name was blank");
    }


    @Then("I should see an error message containing {string}")
    public void iShouldSeeAnErrorMessageContaining(String expectedText) {
        String alert = loginPage().getAlertText();
        Assert.assertNotNull(alert, "No error banner was shown");
        Assert.assertTrue(alert.toLowerCase().contains(expectedText.toLowerCase()),
                "Error banner was '" + alert + "', expected it to contain '" + expectedText + "'");
    }

    @And("I should stay on the login page")
    public void iShouldStayOnTheLoginPage() {
        Assert.assertTrue(loginPage().isLoginPageDisplayed(),
                "Expected to stay on the login page");
    }

    @Then("I should see a validation error {string}")
    public void iShouldSeeAValidationError(String expectedText) {
        String error = loginPage().getFieldErrorText();
        Assert.assertNotNull(error, "No inline validation error was shown");
        Assert.assertTrue(error.contains(expectedText),
                "Validation error was '" + error + "', expected '" + expectedText + "'");
    }
}
