package com.automation.config;

import com.automation.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Singleton configuration manager.
 * <p>
 * Load order (later wins):
 * <ol>
 *   <li>config/config.properties          — defaults</li>
 *   <li>config/environments/{env}.properties — env-specific</li>
 *   <li>System properties (-Dbrowser=firefox)</li>
 *   <li>Environment variables  (BROWSER=firefox)</li>
 * </ol>
 */
public final class ConfigManager {

    private static final Logger LOG = LoggerUtil.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private final Properties properties = new Properties();

    private ConfigManager() {
        loadDefaults();
        loadEnvironmentFile();
        overrideWithSystemProps();
        overrideWithEnvVars();
        LOG.info("Configuration loaded. browser={}, headless={}, parallel.thread.count={}",
                getBrowser(), isHeadless(), getThreadCount());
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    // ── Typed Getters ─────────────────────────────────────────────

    public String getBrowser() {
        return get("browser", "chrome").toLowerCase();
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(get("headless", "false"));
    }

    public boolean isParallelEnabled() {
        return Boolean.parseBoolean(get("parallel.enabled", "true"));
    }

    public int getThreadCount() {
        return Integer.parseInt(get("parallel.thread.count", "3"));
    }

    public String getBaseUrl() {
        return get("base.url", "https://the-internet.herokuapp.com");
    }

    public String getApiBaseUrl() {
        return get("api.base.url", "https://reqres.in/api");
    }

    public int getImplicitWait() {
        return Integer.parseInt(get("implicit.wait", "0"));
    }

    public int getExplicitWait() {
        return Integer.parseInt(get("explicit.wait", "15"));
    }

    public int getPageLoadTimeout() {
        return Integer.parseInt(get("page.load.timeout", "30"));
    }

    public String getEnvironment() {
        return get("environment", "dev");
    }

    public String getSeleniumGridUrl() {
        return get("selenium.grid.url", "");
    }

    public boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(get("screenshot.on.failure", "true"));
    }

    public String getScreenshotDir() {
        return get("screenshot.dir", "target/screenshots");
    }

    public int getRetryCount() {
        return Integer.parseInt(get("retry.count", "1"));
    }

    public boolean isSelfHealingEnabled() {
        return Boolean.parseBoolean(get("self.healing.enabled", "true"));
    }

    // ── Generic getter ────────────────────────────────────────────

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    // ── Loaders ───────────────────────────────────────────────────

    private void loadDefaults() {
        loadPropertiesFile("config/config.properties");
    }

    private void loadEnvironmentFile() {
        String env = properties.getProperty("environment", "dev");
        // Allow system prop to override early
        env = System.getProperty("environment", env);
        String path = "config/environments/" + env + ".properties";
        if (Files.exists(Paths.get(path))) {
            loadPropertiesFile(path);
            LOG.info("Loaded environment config: {}", path);
        }
    }

    private void loadPropertiesFile(String path) {
        Path filePath = Paths.get(path);
        if (!Files.exists(filePath)) {
            LOG.warn("Config file not found: {}", path);
            return;
        }
        try (InputStream is = new FileInputStream(filePath.toFile())) {
            properties.load(is);
        } catch (IOException e) {
            LOG.error("Failed to load config file: {}", path, e);
        }
    }

    private void overrideWithSystemProps() {
        System.getProperties().forEach((key, value) -> {
            String k = key.toString();
            if (properties.containsKey(k)) {
                properties.setProperty(k, value.toString());
                LOG.debug("System property override: {}={}", k, value);
            }
        });
    }

    private void overrideWithEnvVars() {
        // Map ENV_VAR style to property style: BROWSER -> browser, SELENIUM_GRID_URL -> selenium.grid.url
        System.getenv().forEach((envKey, envValue) -> {
            String propKey = envKey.toLowerCase().replace('_', '.');
            if (properties.containsKey(propKey)) {
                properties.setProperty(propKey, envValue);
                LOG.debug("Environment variable override: {}={}", propKey, envValue);
            }
        });
    }

    /** Reset for testing purposes. */
    public static synchronized void reset() {
        instance = null;
    }
}
