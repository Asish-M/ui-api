package com.automation.healing;

import com.automation.config.ConfigManager;
import com.automation.utils.LoggerUtil;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

import java.util.List;

/**
 * Self-healing element finder.
 * <p>
 * When the primary locator for an element fails (throws {@link NoSuchElementException}
 * or the element is stale), this engine automatically tries each alternative locator
 * in order until one succeeds. Every healing event is logged and reported to Allure.
 *
 * <h3>How it works</h3>
 * <ol>
 *   <li>Try the primary locator.</li>
 *   <li>If it fails or the element is stale, iterate through alternatives.</li>
 *   <li>Return the first working element found.</li>
 *   <li>Log the healing event so the locator can be permanently updated later.</li>
 * </ol>
 */
public class SelfHealingDriver {

    private static final Logger LOG = LoggerUtil.getLogger(SelfHealingDriver.class);
    private final WebDriver driver;
    private final boolean healingEnabled;

    public SelfHealingDriver(WebDriver driver) {
        this.driver = driver;
        this.healingEnabled = ConfigManager.getInstance().isSelfHealingEnabled();
    }

    /**
     * Find an element using the self-healing locator.
     *
     * @param locator the self-healing locator with primary + alternatives
     * @return the found {@link WebElement}
     * @throws NoSuchElementException if no locator succeeds
     */
    public WebElement findElement(SelfHealingLocator locator) {
        // 1. Try primary locator
        try {
            WebElement element = driver.findElement(locator.getPrimaryLocator());
            if (isInteractable(element)) {
                LOG.debug("Element '{}' found with primary locator: {}",
                        locator.getElementName(), locator.getPrimaryLocator());
                return element;
            }
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            LOG.warn("Primary locator failed for '{}': {}", locator.getElementName(),
                    locator.getPrimaryLocator());
        }

        // 2. Self-healing: try alternatives
        if (!healingEnabled) {
            throw new NoSuchElementException(
                    "Element '" + locator.getElementName() + "' not found with primary locator "
                            + locator.getPrimaryLocator() + " and self-healing is disabled.");
        }

        return tryAlternativeLocators(locator);
    }

    /**
     * Find multiple elements using the self-healing locator.
     *
     * @param locator the self-healing locator
     * @return list of found {@link WebElement}s
     */
    public List<WebElement> findElements(SelfHealingLocator locator) {
        try {
            List<WebElement> elements = driver.findElements(locator.getPrimaryLocator());
            if (!elements.isEmpty()) {
                return elements;
            }
        } catch (Exception e) {
            LOG.warn("Primary locator failed for findElements '{}': {}",
                    locator.getElementName(), locator.getPrimaryLocator());
        }

        if (!healingEnabled) {
            return List.of();
        }

        // Try alternatives
        for (By alt : locator.getAlternativeLocators()) {
            try {
                List<WebElement> elements = driver.findElements(alt);
                if (!elements.isEmpty()) {
                    reportHealing(locator.getElementName(), locator.getPrimaryLocator(), alt);
                    return elements;
                }
            } catch (Exception ignored) {
                // continue to next alternative
            }
        }
        return List.of();
    }

    // ── Private helpers ───────────────────────────────────────────

    private WebElement tryAlternativeLocators(SelfHealingLocator locator) {
        List<By> alternatives = locator.getAlternativeLocators();
        LOG.info("Self-healing: trying {} alternative locator(s) for '{}'",
                alternatives.size(), locator.getElementName());

        for (By alt : alternatives) {
            try {
                WebElement element = driver.findElement(alt);
                if (isInteractable(element)) {
                    reportHealing(locator.getElementName(), locator.getPrimaryLocator(), alt);
                    return element;
                }
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                LOG.debug("Alternative locator also failed for '{}': {}", locator.getElementName(), alt);
            }
        }

        // All locators exhausted
        throw new NoSuchElementException(
                "Self-healing exhausted all " + (1 + alternatives.size())
                        + " locators for element '" + locator.getElementName() + "'.");
    }

    private boolean isInteractable(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }

    private void reportHealing(String elementName, By original, By healed) {
        String message = String.format(
                "SELF-HEALED element '%s': original [%s] → healed [%s]. "
                        + "Consider updating the primary locator.",
                elementName, original, healed);
        LOG.warn(message);

        // Attach healing report to Allure
        Allure.addAttachment("Self-Healing Report — " + elementName, "text/plain", message);
    }
}
