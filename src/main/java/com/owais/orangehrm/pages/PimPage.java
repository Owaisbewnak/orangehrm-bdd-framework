package com.owais.orangehrm.pages;

import com.owais.orangehrm.utils.TestContext;
import com.owais.orangehrm.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/** PIM — employee list, Add Employee, and employee search. */
public class PimPage extends BasePage {

    private static final By ADD_BUTTON = By.xpath("//button[normalize-space()='Add']");
    private static final By FIRST_NAME = By.name("firstName");
    private static final By MIDDLE_NAME = By.name("middleName");
    private static final By LAST_NAME = By.name("lastName");
    private static final By SAVE_BUTTON = By.cssSelector("button[type='submit']");
    private static final By PERSONAL_DETAILS_HEADER = By.xpath("//h6[text()='Personal Details']");
    private static final By TOAST = By.id("oxd-toaster_1");
    private static final By SEARCH_NAME_INPUT = By.xpath("//input[@placeholder='Type for hints...']");
    private static final By SEARCH_BUTTON = By.cssSelector("button[type='submit']");
    private static final By AUTOCOMPLETE_OPTION = By.cssSelector("div[role='listbox'] span");
    private static final By RECORDS_FOUND = By.xpath("//span[contains(text(),'Records Found')]");
    private static final By NO_RECORDS = By.xpath("//span[text()='No Records Found']");
    private static final By TABLE_ROWS = By.cssSelector(".oxd-table-body .oxd-table-card");

    public PimPage(WebDriver driver) {
        super(driver);
    }

    public boolean isEmployeeListDisplayed() {
        return isDisplayed(By.xpath("//h5[text()='Employee Information']"));
    }

    public void clickAddEmployee() {
        click(ADD_BUTTON);
        WaitUtils.waitForVisibility(driver, FIRST_NAME);
    }

    public void addEmployee(String firstName, String middleName, String lastName) {
        type(FIRST_NAME, firstName);
        if (middleName != null && !middleName.isBlank()) {
            type(MIDDLE_NAME, middleName);
        }
        type(LAST_NAME, lastName);
        click(SAVE_BUTTON);
        // The success toast auto-dismisses in a few seconds, so capture it
        // now and stash it for the Then step — before waiting for the next page.
        try {
            TestContext.put("toastText", textOf(TOAST));
        } catch (Exception e) {
            // Toast already gone; the Personal Details check below still validates the save.
        }
        // Save lands on the new employee's Personal Details page.
        WaitUtils.waitForVisibility(driver, PERSONAL_DETAILS_HEADER);
    }

    /** Toast title+message, e.g. "Success / Successfully Saved". */
    public String getToastText() {
        return textOf(TOAST);
    }

    public boolean isPersonalDetailsDisplayed() {
        return isDisplayed(PERSONAL_DETAILS_HEADER);
    }

    /** Searches by full name and picks the autocomplete suggestion. */
    public void searchEmployeeByName(String fullName) {
        type(SEARCH_NAME_INPUT, fullName);
        List<WebElement> options = WaitUtils.waitForAllVisible(driver, AUTOCOMPLETE_OPTION);
        for (WebElement option : options) {
            if (option.getText().equalsIgnoreCase(fullName)) {
                option.click();
                break;
            }
        }
        click(SEARCH_BUTTON);
        WaitUtils.waitForVisibility(driver, By.xpath("//span[contains(text(),'Records Found') or text()='No Records Found']"));
    }

    /** Same search without relying on autocomplete (used for names that don't exist). */
    public void searchEmployeeByNameNoAutocomplete(String name) {
        type(SEARCH_NAME_INPUT, name);
        click(SEARCH_BUTTON);
        WaitUtils.waitForVisibility(driver, By.xpath("//span[contains(text(),'Records Found') or text()='No Records Found']"));
    }

    public String getRecordsFoundText() {
        if (isDisplayed(NO_RECORDS)) {
            return "No Records Found";
        }
        return textOf(RECORDS_FOUND);
    }

    public boolean isEmployeeInResults(String fullName) {
        if (isDisplayed(NO_RECORDS)) {
            return false;
        }
        List<WebElement> rows = driver.findElements(TABLE_ROWS);
        for (WebElement row : rows) {
            if (row.getText().contains(fullName)) {
                return true;
            }
        }
        return false;
    }
}
