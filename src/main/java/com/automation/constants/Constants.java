package com.automation.constants;

/**
 * Framework-wide constants.
 */
public final class Constants {

    private Constants() {
        // utility class
    }

    // ── Paths ─────────────────────────────────────────────────────
    public static final String CONFIG_PATH = "config/config.properties";
    public static final String TEST_DATA_DIR = "src/test/resources/testdata/";
    public static final String SCREENSHOTS_DIR = "target/screenshots/";
    public static final String LOGS_DIR = "logs/";

    // ── Timeouts (defaults — prefer ConfigManager values) ────────
    public static final int DEFAULT_EXPLICIT_WAIT = 15;
    public static final int DEFAULT_IMPLICIT_WAIT = 0;
    public static final int DEFAULT_PAGE_LOAD_TIMEOUT = 30;
    public static final int POLLING_INTERVAL_MS = 500;

    // ── API ───────────────────────────────────────────────────────
    public static final String CONTENT_TYPE_JSON = "application/json";

    // ── Self-Healing ──────────────────────────────────────────────
    public static final int SELF_HEALING_MAX_ALTERNATIVES = 5;
}
