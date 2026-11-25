package com.owais.orangehrm.pages;

import com.owais.orangehrm.utils.TestContext;
import com.owais.orangehrm.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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

    /**
     * Leave List header, waited for with the full explicit wait (30s).
     * Same fix as DashboardPage on 2026-11-25: the old 5s check in
     * BasePage.isDisplayed() failed in CI Run #1 ("Leave List was not
     * displayed") even though the page was still loading.
     */
    public boolean isLeaveListDisplayed() {
        try {
            WaitUtils.waitForVisibility(driver, LEAVE_LIST_HEADER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void openApplyTab() {
        click(APPLY_TAB);
        WaitUtils.waitForVisibility(driver, LEAVE_TYPE_DROPDOWN);
    }

    public void selectLeaveType(String leaveType) {
        click(LEAVE_TYPE_DROPDOWN);
        WaitUtils.waitForAllVisible(driver, DROPDOWN_OPTION);
        // Click by locator, not by a WebElement held from the list above —
        // the dropdown re-renders and that held element goes stale.
        By option = By.xpath("//div[@role='listbox']//span[normalize-space()='" + leaveType + "']");
        if (driver.findElements(option).isEmpty()) {
            throw new IllegalArgumentException("Leave type not found in dropdown: " + leaveType);
        }
        click(option);
    }

    /** Dates in yyyy-dd-MM — the demo site's format (see the "2026-31-12"
     *  date on Leave List, and the yyyy-dd-mm placeholder). E.g. 1 Dec 2026
     *  is "2026-01-12". First input = From, second = To. */
    public void setDates(String fromDate, String toDate) {
        List<WebElement> inputs = WaitUtils.waitForAllVisible(driver, DATE_INPUTS);
        setDate(inputs.get(0), fromDate);
        setDate(inputs.get(1), toDate);
    }

    /** clear() does not clear OrangeHRM's masked date inputs — the old
     *  value stays and sendKeys appends to it, giving e.g.
     *  "2026-12-012026-12-04" in the To field. Select-all + Delete first. */
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
