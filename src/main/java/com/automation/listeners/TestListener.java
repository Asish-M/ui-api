package com.automation.listeners;

import com.automation.driver.DriverFactory;
import com.automation.utils.LoggerUtil;
import com.automation.utils.ScreenshotUtil;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener for logging and Allure integration.
 * <p>
 * Automatically logs test lifecycle events and captures
 * screenshots on failure for the Allure report.
 */
public class TestListener implements ITestListener {

    private static final Logger LOG = LoggerUtil.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info("▶ TEST STARTED: {}", getTestName(result));
        Allure.step("Test started: " + getTestName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("✅ TEST PASSED: {} ({}ms)", getTestName(result), getDuration(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("❌ TEST FAILED: {} — {}", getTestName(result),
                result.getThrowable().getMessage());

        // Capture screenshot if this is a UI test with an active driver
        try {
            WebDriver driver = DriverFactory.getDriver();
            ScreenshotUtil.capture(driver, "FAILURE_" + getTestName(result));
        } catch (IllegalStateException e) {
            LOG.debug("No driver available for screenshot (likely an API test)");
        }

        // Attach exception to Allure
        if (result.getThrowable() != null) {
            Allure.addAttachment("Exception", "text/plain",
                    result.getThrowable().toString());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("⏭ TEST SKIPPED: {}", getTestName(result));
    }

    @Override
    public void onStart(ITestContext context) {
        LOG.info("═══════════════════════════════════════════════════════");
        LOG.info("  SUITE STARTED: {}", context.getName());
        LOG.info("═══════════════════════════════════════════════════════");
    }

    @Override
    public void onFinish(ITestContext context) {
        LOG.info("═══════════════════════════════════════════════════════");
        LOG.info("  SUITE FINISHED: {} | Passed={} Failed={} Skipped={}",
                context.getName(),
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
        LOG.info("═══════════════════════════════════════════════════════");
    }

    private String getTestName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName() + "." + result.getName();
    }

    private long getDuration(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}
