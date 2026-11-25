package com.owais.orangehrm.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/** Smoke suite — the 3 highest-value scenarios (valid login, add employee,
 *  apply leave). Leave is back in smoke since 2026-11-25: its Given step
 *  adds a leave entitlement for whoever is logged in right now (see
 *  EntitlementPage), so the demo rotating the Admin employee no longer
 *  leaves the Apply page with no leave balance. Run: mvn test -Dtest=SmokeRunner */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.owais.orangehrm.stepdefs", "com.owais.orangehrm.hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/smoke.html",
                "json:target/cucumber-reports/smoke.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "rerun:target/rerun.txt"
        },
        tags = "@smoke and not @locked",
        monochrome = true
)
public class SmokeRunner extends AbstractTestNGCucumberTests {

    /** Parallel scenario execution (3 threads, see testng-smoke.xml).
     *  Temporarily sequential (parallel = false) while debugging the
     *  shared demo site — 3 simultaneous Admin logins made Leave flaky. */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
