package com.owais.orangehrm.pages;

import com.owais.orangehrm.utils.TestContext;

import org.openqa.selenium.Keys;
import com.owais.orangehrm.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Leave — Apply Leave form and its validation. */
public class LeavePage extends BasePage {

    private static final By APPLY_TAB = By.xpath("//a[text()='Apply']");
    private static final By LEAVE_TYPE_DROPDOWN = By.xpath("(//div[contains(@class,'oxd-select-text-input')])[1]");
    private static final By DROPDOWN_OPTION = By.cssSelector("div[role='listbox'] span");
    private static final By DATE_INPUTS = By.xpath("//input[@placeholder='yyyy-dd-mm']");
    private static final By COMMENT = By.tagName("textarea");
    private static final By APPLY_BUTTON = By.cssSelector("button[type='submit']");
    private static final By TOAST = By.id("oxd-toaster_1");
    private static final By FIELD_ERROR = By.cssSelector(".oxd-input-field-error-message");
    private static final By LEAVE_LIST_HEADER = By.xpath("//h5[text()='Leave List']");

    public LeavePage(WebDriver driver) {
        super(driver);
    }

    public boolean isLeaveListDisplayed() {
        return isDisplayed(LEAVE_LIST_HEADER);
    }

    public void openApplyTab() {
        click(APPLY_TAB);
        WaitUtils.waitForVisibility(driver, LEAVE_TYPE_DROPDOWN);
    }

    public void selectLeaveType(String leaveType) {
        click(LEAVE_TYPE_DROPDOWN);
        WaitUtils.waitForAllVisible(driver, DROPDOWN_OPTION);
        By option = By.xpath("//div[@role='listbox']//span[normalize-space()='" + leaveType + "']");
        if (driver.findElements(option).isEmpty()) {
            throw new IllegalArgumentException("Leave type not found in dropdown: " + leaveType);
        }
        click(option);
    }


    /** Dates in yyyy-MM-dd, e.g. 2026-12-05. First input = From, second = To. */
    public void setDates(String fromDate, String toDate) {
        List<WebElement> inputs = WaitUtils.waitForAllVisible(driver, DATE_INPUTS);
        setDate(inputs.get(0), fromDate);
        setDate(inputs.get(1), toDate);
    }

    private void setDate(WebElement input, String date) {
        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(Keys.DELETE);
        input.sendKeys(date);
        input.sendKeys(Keys.TAB);
    }


    public void enterComment(String comment) {
        type(COMMENT, comment);
    }

    public void submit() {
        click(APPLY_BUTTON);
    }

    public void applyLeave(String leaveType, String fromDate, String toDate, String comment) {
        selectLeaveType(leaveType);
        setDates(fromDate, toDate);
        enterComment(comment);
        submit();
        // The success toast auto-dismisses in a few seconds, so capture it
        // now and stash it for the Then step.
        try {
            TestContext.put("toastText", textOf(TOAST));
        } catch (Exception e) {
            // No toast (e.g. validation error) — the Then step handles it.
        }
    }

    public String getToastText() {
        return textOf(TOAST);
    }

    /** Inline validation message, e.g. "To date should be after From date". */
    public String getFieldErrorText() {
        try {
            return WaitUtils.waitForVisibility(driver, FIELD_ERROR, 5).getText();
        } catch (Exception e) {
            return null;
        }
    }
}
