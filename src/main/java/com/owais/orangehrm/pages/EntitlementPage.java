package com.owais.orangehrm.pages;

import com.owais.orangehrm.utils.TestContext;
import com.owais.orangehrm.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Leave — Add Leave Entitlement.
 *
 * <p>Test-data setup, not a test in itself. The shared demo rotates the
 * Admin employee on every login (Duane Cross, John Doe, Isabella Howe,
 * "manda user", ...), so an entitlement added yesterday for one employee
 * never covers today's employee and the Apply page shows
 * "No Leave Types with Leave Balance". This page adds a fresh entitlement
 * for whoever is logged in <i>right now</i> — the name is read from the
 * Dashboard top-right dropdown at run time, never hard-coded — so the
 * Apply Leave scenario can run in the same session.</p>
 *
 * <p>Form fields, in order (OrangeHRM 5.9, Leave &gt; Entitlements &gt;
 * Add Entitlement): Employee Name (autocomplete, placeholder
 * "Type for hints..."), Leave Type (dropdown), Leave Period (dropdown,
 * e.g. "2026-01-01 - 2026-31-12"), Entitlement (days). Saving for an
 * employee who already has an entitlement for that type/period shows a
 * confirm dialog — "Existing Entitlement value ... will be updated" —
 * which this page confirms.</p>
 */
public class EntitlementPage extends BasePage {

    private static final By ENTITLEMENTS_MENU =
            By.xpath("//span[normalize-space()='Entitlements']");
    private static final By ADD_ENTITLEMENT_LINK =
            By.xpath("//a[normalize-space()='Add Entitlements']");
    // Any tag, not just h6: the heading is visible on the form, but the
    // h6 locator timed out on 2026-11-25 (openAddEntitlementPage line 63).
    private static final By ADD_HEADER =
            By.xpath("//*[normalize-space()='Add Leave Entitlement']");
    // Locators verified live on 2026-11-25 (OrangeHRM OS 5.9). The label
    // text is exactly "Employee Name" / "Leave Type" / etc. — the * is a
    // CSS required-marker, not text — so the earlier failure was not the
    // star. It was the ancestor::div locator: it never matched. Use the
    // simple locators the live check gave instead (the employee one is
    // the same //input[@placeholder='Type for hints...'] PimPage uses).
    private static final By EMPLOYEE_INPUT = By.xpath(
            "//input[@placeholder='Type for hints...']");
    private static final By LEAVE_TYPE_DROPDOWN = By.xpath(
            "//label[normalize-space()='Leave Type']/following::div[contains(@class,'oxd-select-text')][1]");
    private static final By LEAVE_PERIOD_DROPDOWN = By.xpath(
            "//label[normalize-space()='Leave Period']/following::div[contains(@class,'oxd-select-text')][1]");
    private static final By ENTITLEMENT_INPUT = By.xpath(
            "//label[normalize-space()='Entitlement']/following::input[1]");
    private static final By SAVE_BUTTON = By.cssSelector("button[type='submit']");
    private static final By DROPDOWN_OPTION = By.cssSelector("div[role='listbox'] span");
    private static final By CONFIRM_BUTTON = By.xpath("//button[normalize-space()='Confirm']");
    private static final By TOAST = By.id("oxd-toaster_1");

    public EntitlementPage(WebDriver driver) {
        super(driver);
    }

    /** Leave module &gt; Entitlements &gt; Add Entitlements. Caller must already be on the Leave module. */
    public void openAddEntitlementPage() {
        click(ENTITLEMENTS_MENU);
        WaitUtils.waitForVisibility(driver, ADD_ENTITLEMENT_LINK);
        click(ADD_ENTITLEMENT_LINK);
        WaitUtils.waitForVisibility(driver, ADD_HEADER);
    }

    /**
     * Types the name and picks the autocomplete suggestion.
     * The Dashboard name is often the short form ("Duane Cross") while the
     * employee record is the full form ("Duane Melvin Cross"), so match on
     * first + last name, not on an exact string.
     */
    public void selectEmployee(String fullName) {
        type(EMPLOYEE_INPUT, fullName);
        List<WebElement> options = WaitUtils.waitForAllVisible(driver, DROPDOWN_OPTION);
        String[] tokens = fullName.trim().split("\\s+");
        String first = tokens[0].toLowerCase();
        String last = tokens[tokens.length - 1].toLowerCase();
        for (WebElement option : options) {
            String text = option.getText();
            if (text.equalsIgnoreCase(fullName)
                    || (text.toLowerCase().contains(first) && text.toLowerCase().contains(last))) {
                option.click();
                return;
            }
        }
        if (options.size() == 1) {
            options.get(0).click();
            return;
        }
        String available = options.stream().map(WebElement::getText).collect(Collectors.joining(", "));
        throw new IllegalArgumentException(
                "Employee '" + fullName + "' not found in autocomplete, options were: " + available);
    }

    /** Clicks a dropdown and picks the option by its exact visible text. */
    public void selectFromDropdown(By dropdown, String value) {
        click(dropdown);
        WaitUtils.waitForAllVisible(driver, DROPDOWN_OPTION);
        By option = By.xpath("//div[@role='listbox']//span[normalize-space()='" + value + "']");
        if (driver.findElements(option).isEmpty()) {
            throw new IllegalArgumentException("Option not found in dropdown: " + value);
        }
        click(option);
    }

    /**
     * Leave Period for the current year, in the demo's own format
     * (e.g. "2026-01-01 - 2026-31-12" — day 31, month 12, as the site shows it).
     * Matches on the year + start date, so a different end-date spelling still works.
     */
    public void selectLeavePeriodForCurrentYear() {
        int year = LocalDate.now().getYear();
        click(LEAVE_PERIOD_DROPDOWN);
        List<WebElement> options = WaitUtils.waitForAllVisible(driver, DROPDOWN_OPTION);
        String prefix = year + "-01-01";
        for (WebElement option : options) {
            if (option.getText().startsWith(prefix)) {
                option.click();
                return;
            }
        }
        String available = options.stream().map(WebElement::getText).collect(Collectors.joining(", "));
        throw new IllegalArgumentException(
                "Leave period for " + year + " not found, options were: " + available);
    }

    /** Clicks Confirm if the "Existing Entitlement ... will be updated" dialog appears; otherwise does nothing. */
    public void handleConfirmIfPresent() {
        try {
            WebElement confirm = WaitUtils.waitForVisibility(driver, CONFIRM_BUTTON, 5);
            confirm.click();
        } catch (Exception e) {
            // No confirm dialog — first entitlement for this employee / type / period.
        }
    }

    /**
     * Full setup in one call: open the form, fill it for the given employee,
     * save, confirm an update if asked, and stash the success toast.
     */
    public void addEntitlement(String employeeName, String leaveType, int days) {
        openAddEntitlementPage();
        selectEmployee(employeeName);
        selectFromDropdown(LEAVE_TYPE_DROPDOWN, leaveType);
        selectLeavePeriodForCurrentYear();
        type(ENTITLEMENT_INPUT, String.valueOf(days));
        click(SAVE_BUTTON);
        handleConfirmIfPresent();
        // The success toast auto-dismisses in a few seconds, so capture it now.
        try {
            TestContext.put("entitlementToast", textOf(TOAST));
        } catch (Exception e) {
            // No toast — the step's assertion will report it.
        }
    }
}
