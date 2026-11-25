package com.owais.orangehrm.pages;

import com.owais.orangehrm.utils.WaitUtils;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

/**
 * Base for all page objects. Every interaction goes through an explicit
 * wait — no direct driver.findElement() calls in page classes.
 */
public abstract class BasePage {

    protected final WebDriver driver;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    protected void click(By locator) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                WaitUtils.waitForClickable(driver, locator).click();
                return;
            } catch (StaleElementReferenceException e) {
                if (attempt == 2) {
                    throw e;
                }
            }
        }
    }


    protected void click(WebElement element) {
        WaitUtils.wait(driver).until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(element)).click();
    }

    protected void type(By locator, String text) {
        WebElement element = WaitUtils.waitForVisibility(driver, locator);
        element.clear();
        element.sendKeys(text);
    }

    protected void type(WebElement element, String text) {
        WaitUtils.wait(driver).until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
    }

    protected String textOf(By locator) {
        return WaitUtils.waitForVisibility(driver, locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return WaitUtils.waitForVisibility(driver, locator, 5).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }
}
