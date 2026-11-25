package com.owais.orangehrm.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Screenshot helper — returns raw bytes for Cucumber/Allure attachments. */
public final class ScreenshotUtils {

    private static final Logger LOG = LogManager.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private ScreenshotUtils() {
    }

    public static byte[] captureAsBytes(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    /** Also writes a PNG under target/screenshots for manual inspection. */
    public static Path captureToFile(WebDriver driver, String name) {
        try {
            Path dir = Path.of("target", "screenshots");
            Files.createDirectories(dir);
            Path file = dir.resolve(name + "-" + LocalDateTime.now().format(TS) + ".png");
            Files.write(file, captureAsBytes(driver));
            return file;
        } catch (IOException e) {
            LOG.warn("Could not save screenshot file: {}", e.getMessage());
            return null;
        }
    }
}
