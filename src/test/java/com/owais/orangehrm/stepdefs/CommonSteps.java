package com.owais.orangehrm.stepdefs;

import com.owais.orangehrm.config.ConfigReader;
import com.owais.orangehrm.config.DriverManager;
import com.owais.orangehrm.pages.DashboardPage;
import com.owais.orangehrm.pages.LoginPage;
import io.cucumber.java.en.Given;
import org.testng.Assert;

/** Steps shared by several features (login background). */
public class CommonSteps {

    private LoginPage loginPage() {
        return new LoginPage(DriverManager.getDriver());
    }

    private DashboardPage dashboardPage() {
        return new DashboardPage(DriverManager.getDriver());
    }

    @Given("I am logged in as the Admin user")
    public void iAmLoggedInAsTheAdminUser() {
        loginPage().open(ConfigReader.baseUrl());
        loginPage().login(ConfigReader.get("admin.username"), ConfigReader.get("admin.password"));
        Assert.assertTrue(dashboardPage().isDashboardDisplayed(),
                "Dashboard was not displayed after login — check credentials / demo availability");
    }
}
