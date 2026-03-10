package com.automation.base;

import com.automation.config.ConfigManager;
import com.automation.driver.DriverFactory;
import com.automation.listeners.TestListener;
import com.automation.utils.LoggerUtil;
import com.automation.utils.ScreenshotUtil;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * Base test class.
 * <p>
 * Every test class should extend {@code BaseTest} to get:
 * <ul>
 *   <li>Automatic WebDriver setup/teardown per test method</li>
 *   <li>Screenshot on failure</li>
 *   <li>TestListener registration (Allure + logging)</li>
 * </ul>
 */
@Listeners(TestListener.class)
public abstract class BaseTest {

    protected static final Logger LOG = LoggerUtil.getLogger(BaseTest.class);
    private final ConfigManager config = ConfigManager.getInstance();

    /**
     * Get the WebDriver for the current thread.
     */
    protected WebDriver getDriver() {
        return DriverFactory.getDriver();
    }

    /**
     * Setup: initialise WebDriver and navigate to base URL.
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.initDriver();
        String baseUrl = config.getBaseUrl();
        getDriver().get(baseUrl);
        LOG.info("Test setup complete — navigated to {}", baseUrl);
    }

    /**
     * Teardown: capture screenshot on failure, then quit driver.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            if (result.getStatus() == ITestResult.FAILURE && config.isScreenshotOnFailure()) {
                LOG.error("Test FAILED: {}", result.getName());
                ScreenshotUtil.capture(getDriver(), "FAIL_" + result.getName());
            }
        } finally {
            DriverFactory.quitDriver();
            LOG.info("Test teardown complete — driver quit");
        }
    }
}
