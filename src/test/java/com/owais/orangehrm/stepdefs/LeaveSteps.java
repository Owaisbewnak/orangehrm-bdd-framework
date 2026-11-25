package com.owais.orangehrm.stepdefs;

import com.owais.orangehrm.config.ConfigReader;
import com.owais.orangehrm.config.DriverManager;
import com.owais.orangehrm.pages.DashboardPage;
import com.owais.orangehrm.pages.EntitlementPage;
import com.owais.orangehrm.pages.LeavePage;
import com.owais.orangehrm.utils.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LeaveSteps {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-dd-MM");

    private DashboardPage dashboardPage() {
        return new DashboardPage(DriverManager.getDriver());
    }

    private LeavePage leavePage() {
        return new LeavePage(DriverManager.getDriver());
    }

    private EntitlementPage entitlementPage() {
        return new EntitlementPage(DriverManager.getDriver());
    }

    /**
     * Test-data setup for the Apply Leave scenarios. Reads the logged-in
     * employee's name from the Dashboard (never a hard-coded name — the
     * demo rotates it on every login) and adds a fresh entitlement for
     * that employee, so the Apply page has a leave type with balance.
     * Runs in the same browser session, just before the Apply steps.
     */
    @Given("the logged-in employee has a {string} leave entitlement")
    public void theLoggedInEmployeeHasALeaveEntitlement(String leaveType) {
        String employeeName = dashboardPage().getLoggedInUserName();
        Assert.assertNotNull(employeeName, "No logged-in user name was shown");
        Assert.assertFalse(employeeName.isBlank(), "Logged-in user name was blank");
        dashboardPage().goToLeave();
        int days = ConfigReader.getInt("leave.entitlementDays", 10);
        entitlementPage().addEntitlement(employeeName, leaveType, days);
        String toast = TestContext.getString("entitlementToast");
        Assert.assertNotNull(toast,
                "No success toast was captured after adding the leave entitlement for " + employeeName);
        Assert.assertTrue(toast.contains("Successfully Saved"),
                "Entitlement toast was '" + toast + "', expected 'Successfully Saved'");
    }

    private String dateDaysFromToday(int days) {
        return LocalDate.now().plusDays(days).format(DATE_FORMAT);
    }

    @When("I navigate to the Leave module")
    public void iNavigateToTheLeaveModule() {
        dashboardPage().goToLeave();
        Assert.assertTrue(leavePage().isLeaveListDisplayed(), "Leave List was not displayed");
    }

    @And("I apply for {string} leave from {int} days from today to {int} days from today")
    public void iApplyForLeave(String leaveType, int fromDays, int toDays) {
        leavePage().openApplyTab();
        leavePage().applyLeave(leaveType, dateDaysFromToday(fromDays),
                dateDaysFromToday(toDays), "Automation test leave — " + leaveType);
    }

    @Then("the leave should be applied successfully")
    public void theLeaveShouldBeAppliedSuccessfully() {
        String toast = TestContext.getString("toastText");
        Assert.assertNotNull(toast, "No success toast was captured after applying leave");
        Assert.assertTrue(toast.contains("Successfully Saved"),
                "Toast was '" + toast + "', expected 'Successfully Saved'");
    }

    @Then("I should see a leave validation error containing {string}")
    public void iShouldSeeALeaveValidationErrorContaining(String expectedText) {
        String error = leavePage().getFieldErrorText();
        Assert.assertNotNull(error, "No inline validation error was shown on the leave form");
        Assert.assertTrue(error.toLowerCase().contains(expectedText.toLowerCase()),
                "Validation error was '" + error + "', expected it to contain '" + expectedText + "'");
    }
}
