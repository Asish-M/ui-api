package com.automation.base;

import com.automation.healing.SelfHealingDriver;
import com.automation.healing.SelfHealingLocator;
import com.automation.utils.LoggerUtil;
import com.automation.utils.WaitUtil;
import io.qameta.allure.Step;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

/**
 * Base page class for the Page Object Model.
 * <p>
 * Provides self-healing element location, explicit waits,
 * and common interaction helper methods. All page objects
 * should extend this class.
 *
 * <pre>
 * public class LoginPage extends BasePage {
 *     private final SelfHealingLocator usernameField = SelfHealingLocator.builder("username")
 *             .primary(By.id("username"))
 *             .addAlternative(By.name("username"))
 *             .build();
 *
 *     public LoginPage(WebDriver driver) { super(driver); }
 *
 *     public void enterUsername(String user) { type(usernameField, user); }
 * }
 * </pre>
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WaitUtil waitUtil;
    protected final SelfHealingDriver healingDriver;
    protected final Logger log;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.waitUtil = new WaitUtil(driver);
        this.healingDriver = new SelfHealingDriver(driver);
        this.log = LoggerUtil.getLogger(this.getClass());
    }

    // ── Self-Healing element operations ───────────────────────────

    /**
     * Find an element using self-healing locator.
     */
    protected WebElement find(SelfHealingLocator locator) {
        return healingDriver.findElement(locator);
    }

    /**
     * Find an element using a simple By locator.
     */
    protected WebElement find(By locator) {
        return driver.findElement(locator);
    }

    /**
     * Click an element located by self-healing locator.
     */
    @Step("Click on '{locator.elementName}'")
    protected void click(SelfHealingLocator locator) {
        WebElement element = healingDriver.findElement(locator);
        waitUtil.waitForClickable(element);
        element.click();
        log.info("Clicked element: {}", locator.getElementName());
    }

    /**
     * Click an element located by simple By locator.
     */
    @Step("Click on element")
    protected void click(By locator) {
        waitUtil.waitForClickable(locator).click();
    }

    /**
     * Type text into an element (clears first).
     */
    @Step("Type '{text}' into '{locator.elementName}'")
    protected void type(SelfHealingLocator locator, String text) {
        WebElement element = healingDriver.findElement(locator);
        waitUtil.waitForVisible(element);
        element.clear();
        element.sendKeys(text);
        log.info("Typed '{}' into element: {}", text, locator.getElementName());
    }

    /**
     * Type text into an element using By locator.
     */
    @Step("Type '{text}' into element")
    protected void type(By locator, String text) {
        WebElement element = waitUtil.waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Get text from an element.
     */
    protected String getText(SelfHealingLocator locator) {
        return healingDriver.findElement(locator).getText();
    }

    protected String getText(By locator) {
        return waitUtil.waitForVisible(locator).getText();
    }

    /**
     * Check if element is displayed.
     */
    protected boolean isDisplayed(SelfHealingLocator locator) {
        try {
            return healingDriver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Get attribute value from an element.
     */
    protected String getAttribute(SelfHealingLocator locator, String attribute) {
        return healingDriver.findElement(locator).getAttribute(attribute);
    }

    /**
     * Select dropdown value by visible text.
     */
    protected void selectByText(SelfHealingLocator locator, String visibleText) {
        WebElement element = healingDriver.findElement(locator);
        new Select(element).selectByVisibleText(visibleText);
        log.info("Selected '{}' from dropdown: {}", visibleText, locator.getElementName());
    }

    /**
     * Scroll to an element.
     */
    protected void scrollTo(SelfHealingLocator locator) {
        WebElement element = healingDriver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Hover over an element.
     */
    protected void hover(SelfHealingLocator locator) {
        WebElement element = healingDriver.findElement(locator);
        new Actions(driver).moveToElement(element).perform();
    }

    /**
     * Execute JavaScript.
     */
    protected Object executeJs(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    /**
     * Get current page title.
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Get current URL.
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
