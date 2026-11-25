package com.owais.orangehrm.stepdefs;

import com.owais.orangehrm.config.DriverManager;
import com.owais.orangehrm.pages.DashboardPage;
import com.owais.orangehrm.pages.PimPage;
import com.owais.orangehrm.utils.EmployeeDataGenerator;
import com.owais.orangehrm.utils.JsonTestData;
import com.owais.orangehrm.utils.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

public class PimSteps {

    private DashboardPage dashboardPage() {
        return new DashboardPage(DriverManager.getDriver());
    }

    private PimPage pimPage() {
        return new PimPage(DriverManager.getDriver());
    }

    @When("I navigate to the PIM module")
    public void iNavigateToThePimModule() {
        dashboardPage().goToPim();
        Assert.assertTrue(pimPage().isEmployeeListDisplayed(), "PIM employee list was not displayed");
    }

    @And("I add a new employee with a generated unique name")
    public void iAddANewEmployeeWithAGeneratedUniqueName() {
        String[] names = EmployeeDataGenerator.uniqueEmployeeName("Auto", "Test");
        TestContext.put("fullName", EmployeeDataGenerator.fullName(names[0], names[1]));
        pimPage().clickAddEmployee();
        pimPage().addEmployee(names[0], "", names[1]);
    }

    @And("I add an employee with first name {string} and last name {string}")
    public void iAddAnEmployeeWithFirstAndLastName(String firstName, String lastName) {
        TestContext.put("fullName", EmployeeDataGenerator.fullName(firstName, lastName));
        pimPage().clickAddEmployee();
        pimPage().addEmployee(firstName, "", lastName);
    }

    @And("I add the first employee from the test data file")
    public void iAddTheFirstEmployeeFromTheTestDataFile() {
        List<Map<String, String>> employees = JsonTestData.readEmployees();
        Map<String, String> employee = employees.get(0);
        String fullName = EmployeeDataGenerator.fullName(
                employee.get("firstName"), employee.get("lastName"));
        TestContext.put("fullName", fullName);
        pimPage().clickAddEmployee();
        pimPage().addEmployee(employee.get("firstName"),
                employee.getOrDefault("middleName", ""), employee.get("lastName"));
    }

    @Then("the employee should be created successfully")
    public void theEmployeeShouldBeCreatedSuccessfully() {
        String toast = TestContext.getString("toastText");
        Assert.assertNotNull(toast, "No success toast was captured after saving the employee");
        Assert.assertTrue(toast.contains("Successfully Saved"),
                "Toast was '" + toast + "', expected 'Successfully Saved'");
    }

    @And("the Personal Details page should be displayed")
    public void thePersonalDetailsPageShouldBeDisplayed() {
        Assert.assertTrue(pimPage().isPersonalDetailsDisplayed(),
                "Personal Details page was not displayed after adding the employee");
    }

    @And("I search for employee {string}")
    public void iSearchForEmployee(String fullName) {
        pimPage().searchEmployeeByName(fullName);
    }

    @And("I search for a non-existent employee {string}")
    public void iSearchForANonExistentEmployee(String name) {
        pimPage().searchEmployeeByNameNoAutocomplete(name);
    }

    @Then("the search results should contain {string}")
    public void theSearchResultsShouldContain(String fullName) {
        Assert.assertTrue(pimPage().isEmployeeInResults(fullName),
                "Employee '" + fullName + "' was not found in the search results");
    }

    @Then("I should see {string}")
    public void iShouldSee(String expectedText) {
        Assert.assertEquals(pimPage().getRecordsFoundText(), expectedText,
                "Records-found text did not match");
    }
}
