package com.automation.tests.ui;

import com.automation.base.BaseTest;
import com.automation.listeners.RetryAnalyzer;
import com.automation.pages.LoginPage;
import com.automation.pages.SecureAreaPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * UI tests for the Login page (the-internet.herokuapp.com/login).
 */
@Epic("UI Tests")
@Feature("Login")
public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod
    public void navigateToLogin() {
        getDriver().get("https://the-internet.herokuapp.com/login");
        loginPage = new LoginPage(getDriver());
    }

    @Test(description = "Verify successful login with valid credentials",
            retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Valid Login")
    @Description("Test that a user can log in with valid username and password")
    public void testValidLogin() {
        SecureAreaPage securePage = loginPage.login("tomsmith", "SuperSecretPassword!");

        Assert.assertTrue(securePage.isPageLoaded(),
                "Secure area should be displayed after login");
        Assert.assertTrue(securePage.getFlashMessageText().contains("You logged into a secure area!"),
                "Success message should be displayed");
    }

    @Test(description = "Verify error message with invalid credentials",
            retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Story("Invalid Login")
    @Description("Test that appropriate error is shown for invalid credentials")
    public void testInvalidLogin() {
        loginPage.enterUsername("invalidUser");
        loginPage.enterPassword("invalidPassword");
        loginPage.clickLogin();

        String flashText = loginPage.getFlashMessageText();
        Assert.assertTrue(flashText.contains("Your username is invalid!"),
                "Error message should indicate invalid username");
    }

    @Test(description = "Verify login page elements are displayed")
    @Severity(SeverityLevel.MINOR)
    @Story("Page Load")
    public void testLoginPageLoaded() {
        Assert.assertTrue(loginPage.isPageLoaded(),
                "Login page should display username and password fields");
    }

    @Test(description = "Verify logout from secure area",
            retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Logout")
    @Description("Test that a user can log out after successful login")
    public void testLogout() {
        SecureAreaPage securePage = loginPage.login("tomsmith", "SuperSecretPassword!");
        Assert.assertTrue(securePage.isPageLoaded(), "Should be on secure page");

        LoginPage returnedLoginPage = securePage.logout();
        Assert.assertTrue(returnedLoginPage.getFlashMessageText().contains("You logged out of the secure area!"),
                "Logout success message should be displayed");
    }
}
