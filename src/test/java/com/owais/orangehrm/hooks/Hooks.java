package com.owais.orangehrm.hooks;

import com.owais.orangehrm.config.DriverManager;
import com.owais.orangehrm.utils.ScreenshotUtils;
import com.owais.orangehrm.utils.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;

/** One fresh browser per scenario; screenshot + Allure attachment on failure. */
public class Hooks {

    private static final Logger LOG = LogManager.getLogger(Hooks.class);

    @Before(order = 0)
    public void setUp(Scenario scenario) {
        LOG.info("START scenario: {}", scenario.getName());
        DriverManager.initDriver();
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                byte[] screenshot = ScreenshotUtils.captureAsBytes(DriverManager.getDriver());
                scenario.attach(screenshot, "image/png", "failure-screenshot");
                Allure.addAttachment("Failure screenshot",
                        new ByteArrayInputStream(screenshot));
                ScreenshotUtils.captureToFile(DriverManager.getDriver(), scenario.getName()
                        .replaceAll("[^a-zA-Z0-9]", "_"));
                LOG.error("FAILED scenario: {}", scenario.getName());
            } else {
                LOG.info("PASSED scenario: {}", scenario.getName());
            }
        } catch (Exception e) {
            LOG.warn("Could not capture failure screenshot: {}", e.getMessage());
        } finally {
            DriverManager.quitDriver();
            TestContext.clear();
        }
    }
}
