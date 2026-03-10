package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.healing.SelfHealingLocator;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the secure area after successful login.
 */
public class SecureAreaPage extends BasePage {

    // ── Self-healing locators ─────────────────────────────────────

    private final SelfHealingLocator flashMessage = SelfHealingLocator.builder("secureFlashMessage")
            .primary(By.id("flash"))
            .addAlternative(By.cssSelector(".flash.success"))
            .addAlternative(By.cssSelector("#flash-messages .flash"))
            .build();

    private final SelfHealingLocator logoutButton = SelfHealingLocator.builder("logoutButton")
            .primary(By.cssSelector("a.button[href='/logout']"))
            .addAlternative(By.linkText("Logout"))
            .addAlternative(By.xpath("//a[@href='/logout']"))
            .build();

    private final SelfHealingLocator heading = SelfHealingLocator.builder("secureAreaHeading")
            .primary(By.tagName("h2"))
            .addAlternative(By.cssSelector(".example h2"))
            .addAlternative(By.xpath("//h2"))
            .build();

    // ── Constructor ───────────────────────────────────────────────

    public SecureAreaPage(WebDriver driver) {
        super(driver);
    }

    // ── Actions ───────────────────────────────────────────────────

    @Step("Get secure area flash message")
    public String getFlashMessageText() {
        return getText(flashMessage);
    }

    @Step("Get secure area heading")
    public String getHeadingText() {
        return getText(heading);
    }

    @Step("Click Logout")
    public LoginPage logout() {
        click(logoutButton);
        return new LoginPage(driver);
    }

    /**
     * Verify the secure area is displayed.
     */
    public boolean isPageLoaded() {
        return isDisplayed(logoutButton);
    }
}
