package com.automation.driver;

import com.automation.config.ConfigManager;
import com.automation.utils.LoggerUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Thread-safe WebDriver factory.
 * <p>
 * Supports local and remote (Selenium Grid / Docker) execution.
 * Browser, headless mode, and Grid URL are read from {@link ConfigManager}.
 */
public final class DriverFactory {

    private static final Logger LOG = LoggerUtil.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    private DriverFactory() {
        // utility class
    }

    /**
     * Initialise a WebDriver instance for the current thread.
     */
    public static WebDriver initDriver() {
        ConfigManager config = ConfigManager.getInstance();
        String browser = config.getBrowser();
        boolean headless = config.isHeadless();
        String gridUrl = config.getSeleniumGridUrl();

        WebDriver driver;

        if (gridUrl != null && !gridUrl.isBlank()) {
            driver = createRemoteDriver(browser, headless, gridUrl);
        } else {
            driver = createLocalDriver(browser, headless);
        }

        configureTimeouts(driver, config);
        driver.manage().window().maximize();
        DRIVER_THREAD_LOCAL.set(driver);

        LOG.info("Driver initialised — browser={}, headless={}, thread={}",
                browser, headless, Thread.currentThread().getName());
        return driver;
    }

    /**
     * Get the WebDriver instance for the current thread.
     */
    public static WebDriver getDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver not initialised for thread: " + Thread.currentThread().getName());
        }
        return driver;
    }

    /**
     * Quit the WebDriver and clean up the thread-local reference.
     */
    public static void quitDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver != null) {
            try {
                driver.quit();
                LOG.info("Driver quit — thread={}", Thread.currentThread().getName());
            } catch (Exception e) {
                LOG.warn("Error quitting driver: {}", e.getMessage());
            } finally {
                DRIVER_THREAD_LOCAL.remove();
            }
        }
    }

    // ── Private helpers ───────────────────────────────────────────

    private static WebDriver createLocalDriver(String browser, boolean headless) {
        return switch (browser) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (headless) options.addArguments("--headless");
                options.addArguments("--width=1920", "--height=1080");
                yield new FirefoxDriver(options);
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions options = new EdgeOptions();
                if (headless) options.addArguments("--headless=new");
                options.addArguments("--window-size=1920,1080", "--disable-gpu",
                        "--no-sandbox", "--disable-dev-shm-usage");
                yield new EdgeDriver(options);
            }
            default -> {
                // chrome (default)
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (headless) options.addArguments("--headless=new");
                options.addArguments("--window-size=1920,1080", "--disable-gpu",
                        "--no-sandbox", "--disable-dev-shm-usage");
                yield new ChromeDriver(options);
            }
        };
    }

    private static WebDriver createRemoteDriver(String browser, boolean headless, String gridUrl) {
        AbstractDriverOptions<?> options = switch (browser) {
            case "firefox" -> {
                FirefoxOptions opts = new FirefoxOptions();
                if (headless) opts.addArguments("--headless");
                yield opts;
            }
            case "edge" -> {
                EdgeOptions opts = new EdgeOptions();
                if (headless) opts.addArguments("--headless=new");
                yield opts;
            }
            default -> {
                ChromeOptions opts = new ChromeOptions();
                if (headless) opts.addArguments("--headless=new");
                opts.addArguments("--no-sandbox", "--disable-dev-shm-usage");
                yield opts;
            }
        };

        try {
            LOG.info("Connecting to Selenium Grid at {}", gridUrl);
            return new RemoteWebDriver(new URL(gridUrl), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Selenium Grid URL: " + gridUrl, e);
        }
    }

    private static void configureTimeouts(WebDriver driver, ConfigManager config) {
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(config.getImplicitWait()))
                .pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
    }
}
