package com.owais.orangehrm.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Full regression suite (default: {@code mvn test} via testng.xml).
 * The @locked scenario is excluded — it locks the shared demo Admin
 * account, so it has its own runner (LockedAccountRunner).
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.owais.orangehrm.stepdefs", "com.owais.orangehrm.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/regression.html",
                "json:target/cucumber-reports/regression.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "rerun:target/rerun.txt"
        },
        tags = "@regression and not @locked",
        monochrome = true
)
public class RegressionRunner extends AbstractTestNGCucumberTests {

    /** Parallel scenario execution (3 threads, see testng.xml). */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
