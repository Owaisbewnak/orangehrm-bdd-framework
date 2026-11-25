package com.owais.orangehrm.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Account-lockout scenario ONLY — run on demand and never in the same
 * suite as the other scenarios, because it locks the shared demo Admin
 * account: {@code mvn test -Dtest=LockedAccountRunner}
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.owais.orangehrm.stepdefs", "com.owais.orangehrm.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/locked.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@locked",
        monochrome = true
)
public class LockedAccountRunner extends AbstractTestNGCucumberTests {
}
