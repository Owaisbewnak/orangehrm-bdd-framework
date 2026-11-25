package com.owais.orangehrm.pages;

import com.owais.orangehrm.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Login page — https://opensource-demo.orangehrmlive.com/web/index.php/auth/login */
public class LoginPage extends BasePage {

    private static final By USERNAME = By.name("username");
    private static final By PASSWORD = By.name("password");
    private static final By LOGIN_BUTTON = By.cssSelector("button[type='submit']");
    private static final By ALERT_TEXT = By.cssSelector(".oxd-alert-content-text");
    private static final By FIELD_ERROR = By.cssSelector(".oxd-input-field-error-message");

    @FindBy(name = "username")
    private WebElement usernameInput;

    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/web/index.php/auth/login");
        WaitUtils.waitForVisibility(driver, USERNAME);
    }

    public boolean isLoginPageDisplayed() {
        return isDisplayed(USERNAME) && isDisplayed(PASSWORD);
    }

    public void login(String username, String password) {
        type(usernameInput, username);
        type(passwordInput, password);
        click(loginButton);
    }

    public void loginWithoutCredentials() {
        click(loginButton);
    }

    /** Text of the red "Invalid credentials" / "Account locked" banner, or null. */
    public String getAlertText() {
        try {
            return WaitUtils.waitForVisibility(driver, ALERT_TEXT, 5).getText();
        } catch (Exception e) {
            return null;
        }
    }

    /** Inline "Required" message shown under an empty field, or null. */
    public String getFieldErrorText() {
        try {
            return WaitUtils.waitForVisibility(driver, FIELD_ERROR, 5).getText();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isFieldErrorDisplayed() {
        return isDisplayed(FIELD_ERROR);
    }
}
