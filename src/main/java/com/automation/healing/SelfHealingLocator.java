package com.automation.healing;

import org.openqa.selenium.By;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores multiple locator strategies for a single UI element.
 * <p>
 * When the primary locator fails, the self-healing engine
 * iterates through alternatives to find a working one.
 *
 * <pre>
 *   SelfHealingLocator locator = SelfHealingLocator.builder("username")
 *       .primary(By.id("username"))
 *       .addAlternative(By.name("username"))
 *       .addAlternative(By.cssSelector("input[name='username']"))
 *       .addAlternative(By.xpath("//input[@id='username']"))
 *       .build();
 * </pre>
 */
public class SelfHealingLocator {

    private final String elementName;
    private final By primaryLocator;
    private final List<By> alternativeLocators;

    private SelfHealingLocator(String elementName, By primaryLocator, List<By> alternativeLocators) {
        this.elementName = elementName;
        this.primaryLocator = primaryLocator;
        this.alternativeLocators = alternativeLocators;
    }

    public String getElementName() {
        return elementName;
    }

    public By getPrimaryLocator() {
        return primaryLocator;
    }

    public List<By> getAlternativeLocators() {
        return alternativeLocators;
    }

    /**
     * Returns all locators (primary first, then alternatives).
     */
    public List<By> getAllLocators() {
        List<By> all = new ArrayList<>();
        all.add(primaryLocator);
        all.addAll(alternativeLocators);
        return all;
    }

    public static Builder builder(String elementName) {
        return new Builder(elementName);
    }

    // ── Builder ───────────────────────────────────────────────────

    public static class Builder {
        private final String elementName;
        private By primaryLocator;
        private final List<By> alternativeLocators = new ArrayList<>();

        private Builder(String elementName) {
            this.elementName = elementName;
        }

        public Builder primary(By locator) {
            this.primaryLocator = locator;
            return this;
        }

        public Builder addAlternative(By locator) {
            this.alternativeLocators.add(locator);
            return this;
        }

        public SelfHealingLocator build() {
            if (primaryLocator == null) {
                throw new IllegalStateException("Primary locator must be set for element: " + elementName);
            }
            return new SelfHealingLocator(elementName, primaryLocator, alternativeLocators);
        }
    }
}
