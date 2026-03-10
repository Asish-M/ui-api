package com.automation.listeners;

import com.automation.config.ConfigManager;
import com.automation.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG retry analyzer — automatically retries failed tests.
 * <p>
 * Retry count is read from {@code retry.count} in config.properties.
 * Usage:
 * <pre>
 * &#64;Test(retryAnalyzer = RetryAnalyzer.class)
 * public void flakyTest() { ... }
 * </pre>
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LoggerUtil.getLogger(RetryAnalyzer.class);
    private int currentRetry = 0;

    @Override
    public boolean retry(ITestResult result) {
        int maxRetry = ConfigManager.getInstance().getRetryCount();
        if (currentRetry < maxRetry) {
            currentRetry++;
            LOG.warn("Retrying test '{}' — attempt {}/{}", result.getName(),
                    currentRetry, maxRetry);
            return true;
        }
        return false;
    }
}
