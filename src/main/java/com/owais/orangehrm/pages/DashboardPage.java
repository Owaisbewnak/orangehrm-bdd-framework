package com.owais.orangehrm.pages;

import com.owais.orangehrm.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Dashboard + left navigation, shared by every logged-in flow. */
public class DashboardPage extends BasePage {

    private static final By DASHBOARD_HEADER = By.xpath("//h6[text()='Dashboard']");
    private static final By USER_DROPDOWN = By.cssSelector(".oxd-userdropdown-name");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Dashboard header, waited for with the full explicit wait (30s).
     * The shared demo is often slow after login — the Login scenario took
     * 20s on 2026-11-25 — and the old 5s check in BasePage.isDisplayed()
     * failed the Background step even though the login had succeeded.
     */
    public boolean isDashboardDisplayed() {
        try {
            WaitUtils.waitForVisibility(driver, DASHBOARD_HEADER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getLoggedInUserName() {
        return textOf(USER_DROPDOWN);
    }

    private void openMenu(String menuName) {
        click(By.xpath("//span[text()='" + menuName + "']"));
    }

    public void goToPim() {
        openMenu("PIM");
        WaitUtils.waitForUrlToContain(driver, "/pim/");
    }

    public void goToLeave() {
        openMenu("Leave");
        WaitUtils.waitForUrlToContain(driver, "/leave/");
    }

    public void goToDashboard() {
        openMenu("Dashboard");
        WaitUtils.waitForVisibility(driver, DASHBOARD_HEADER);
    }

    public void logout() {
        click(USER_DROPDOWN);
        click(By.xpath("//a[text()='Logout']"));
    }
}
