package com.automation.api;

import com.automation.config.ConfigManager;
import com.automation.listeners.TestListener;
import com.automation.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

/**
 * Base test class for API tests.
 * <p>
 * Does NOT initialise a WebDriver — only sets up common API config.
 */
@Listeners(TestListener.class)
public abstract class BaseAPITest {

    protected static final Logger LOG = LoggerUtil.getLogger(BaseAPITest.class);
    protected final ConfigManager config = ConfigManager.getInstance();
    protected String apiBaseUrl;

    @BeforeClass(alwaysRun = true)
    public void apiSetUp() {
        apiBaseUrl = config.getApiBaseUrl();
        LOG.info("API Test setup — base URL: {}", apiBaseUrl);
    }
}
