package com.automation.utils;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Screenshot capture utility with automatic Allure attachment.
 */
public final class ScreenshotUtil {

    private static final Logger LOG = LoggerUtil.getLogger(ScreenshotUtil.class);
    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtil() {
        // utility class
    }

    /**
     * Take a screenshot, save to disk, and attach to Allure report.
     *
     * @param driver     the current WebDriver
     * @param screenshotName  descriptive name for the screenshot
     * @return the saved file path (or null on failure)
     */
    public static String capture(WebDriver driver, String screenshotName) {
        if (driver == null) {
            LOG.warn("Cannot take screenshot — driver is null");
            return null;
        }

        try {
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            // Attach to Allure
            Allure.addAttachment(screenshotName, "image/png",
                    new ByteArrayInputStream(screenshotBytes), ".png");

            // Save to disk
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
            String fileName = sanitise(screenshotName) + "_" + timestamp + ".png";
            Path screenshotDir = Paths.get("target", "screenshots");
            Files.createDirectories(screenshotDir);
            Path filePath = screenshotDir.resolve(fileName);
            Files.write(filePath, screenshotBytes);

            LOG.info("Screenshot saved: {}", filePath.toAbsolutePath());
            return filePath.toAbsolutePath().toString();

        } catch (IOException e) {
            LOG.error("Failed to capture screenshot: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Take a screenshot and attach to Allure only (no disk save).
     */
    public static void attachToAllure(WebDriver driver, String name) {
        if (driver == null) return;
        try {
            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, "image/png",
                    new ByteArrayInputStream(bytes), ".png");
        } catch (Exception e) {
            LOG.warn("Failed to attach screenshot to Allure: {}", e.getMessage());
        }
    }

    private static String sanitise(String name) {
        return name.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
