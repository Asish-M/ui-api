package com.automation.utils;

import com.automation.config.ConfigManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Reusable explicit-wait utility methods.
 */
public class WaitUtil {

    private static final Logger LOG = LoggerUtil.getLogger(WaitUtil.class);
    private final WebDriver driver;
    private final int defaultTimeout;

    public WaitUtil(WebDriver driver) {
        this.driver = driver;
        this.defaultTimeout = ConfigManager.getInstance().getExplicitWait();
    }

    // ── Visibility ────────────────────────────────────────────────

    public WebElement waitForVisible(By locator) {
        return waitForVisible(locator, defaultTimeout);
    }

    public WebElement waitForVisible(By locator, int timeoutSeconds) {
        LOG.debug("Waiting for element visible: {} (timeout={}s)", locator, timeoutSeconds);
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForVisible(WebElement element) {
        return waitForVisible(element, defaultTimeout);
    }

    public WebElement waitForVisible(WebElement element, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.visibilityOf(element));
    }

    // ── Clickable ─────────────────────────────────────────────────

    public WebElement waitForClickable(By locator) {
        return waitForClickable(locator, defaultTimeout);
    }

    public WebElement waitForClickable(By locator, int timeoutSeconds) {
        LOG.debug("Waiting for element clickable: {} (timeout={}s)", locator, timeoutSeconds);
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement waitForClickable(WebElement element) {
        return waitForClickable(element, defaultTimeout);
    }

    public WebElement waitForClickable(WebElement element, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    // ── Presence ──────────────────────────────────────────────────

    public WebElement waitForPresence(By locator) {
        return waitForPresence(locator, defaultTimeout);
    }

    public WebElement waitForPresence(By locator, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public List<WebElement> waitForAllVisible(By locator) {
        return waitForAllVisible(locator, defaultTimeout);
    }

    public List<WebElement> waitForAllVisible(By locator, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    // ── Text ──────────────────────────────────────────────────────

    public boolean waitForTextPresent(By locator, String text) {
        return waitForTextPresent(locator, text, defaultTimeout);
    }

    public boolean waitForTextPresent(By locator, String text, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    // ── URL ───────────────────────────────────────────────────────

    public boolean waitForUrlContains(String urlFragment) {
        return waitForUrlContains(urlFragment, defaultTimeout);
    }

    public boolean waitForUrlContains(String urlFragment, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.urlContains(urlFragment));
    }

    // ── Invisibility ──────────────────────────────────────────────

    public boolean waitForInvisible(By locator) {
        return waitForInvisible(locator, defaultTimeout);
    }

    public boolean waitForInvisible(By locator, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // ── Staleness ─────────────────────────────────────────────────

    public boolean waitForStaleness(WebElement element) {
        return waitForStaleness(element, defaultTimeout);
    }

    public boolean waitForStaleness(WebElement element, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.stalenessOf(element));
    }

    // ── Fluent Wait ───────────────────────────────────────────────

    public WebElement fluentWait(By locator, int timeoutSeconds, int pollingMs) {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(pollingMs))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .until(d -> d.findElement(locator));
    }
}
