package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.healing.SelfHealingLocator;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the login page at the-internet.herokuapp.com/login.
 */
public class LoginPage extends BasePage {

    // ── Self-healing locators ─────────────────────────────────────

    private final SelfHealingLocator usernameField = SelfHealingLocator.builder("username")
            .primary(By.id("username"))
            .addAlternative(By.name("username"))
            .addAlternative(By.cssSelector("#username"))
            .addAlternative(By.xpath("//input[@id='username']"))
            .build();

    private final SelfHealingLocator passwordField = SelfHealingLocator.builder("password")
            .primary(By.id("password"))
            .addAlternative(By.name("password"))
            .addAlternative(By.cssSelector("#password"))
            .addAlternative(By.xpath("//input[@id='password']"))
            .build();

    private final SelfHealingLocator loginButton = SelfHealingLocator.builder("loginButton")
            .primary(By.cssSelector("button[type='submit']"))
            .addAlternative(By.id("login"))
            .addAlternative(By.xpath("//button[@type='submit']"))
            .addAlternative(By.cssSelector("i.fa.fa-2x.fa-sign-in"))
            .build();

    private final SelfHealingLocator flashMessage = SelfHealingLocator.builder("flashMessage")
            .primary(By.id("flash"))
            .addAlternative(By.cssSelector(".flash"))
            .addAlternative(By.cssSelector("#flash-messages .flash"))
            .build();

    // ── Constructor ───────────────────────────────────────────────

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ── Actions ───────────────────────────────────────────────────

    @Step("Enter username: {username}")
    public LoginPage enterUsername(String username) {
        type(usernameField, username);
        return this;
    }

    @Step("Enter password")
    public LoginPage enterPassword(String password) {
        type(passwordField, password);
        return this;
    }

    @Step("Click Login button")
    public void clickLogin() {
        click(loginButton);
    }

    /**
     * Perform full login — fluent API.
     */
    @Step("Login with username: {username}")
    public SecureAreaPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return new SecureAreaPage(driver);
    }

    /**
     * Get the flash message text after a login attempt.
     */
    @Step("Get flash message text")
    public String getFlashMessageText() {
        return getText(flashMessage);
    }

    /**
     * Check if login page is displayed.
     */
    public boolean isPageLoaded() {
        return isDisplayed(usernameField) && isDisplayed(passwordField);
    }
}
