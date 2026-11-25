# OrangeHRM BDD Automation Framework

[![CI](https://github.com/Owaisbewnak/orangehrm-bdd-framework/actions/workflows/ci.yml/badge.svg)](https://github.com/Owaisbewnak/orangehrm-bdd-framework/actions/workflows/ci.yml)
![Java 17](https://img.shields.io/badge/Java-17-blue)
![Selenium 4](https://img.shields.io/badge/Selenium-4.25-green)
![Cucumber 7](https://img.shields.io/badge/Cucumber-7.20-brightgreen)
![TestNG](https://img.shields.io/badge/TestNG-7.10-orange)

A **BDD test automation framework** for the [OrangeHRM demo app](https://opensource-demo.orangehrmlive.com/)
(`Admin` / `admin123`), built with **Selenium 4, Cucumber 7, TestNG and Maven**.

It covers **Login, PIM (add/search employee) and Leave (apply)** as readable Gherkin
features, runs scenarios **one at a time** with a `ThreadLocal` WebDriver, and publishes
**Allure** and **Cucumber HTML** reports from **GitHub Actions** and **Jenkins**.

## Test coverage — 18 scenarios

| Feature | Scenarios | Tags |
| --- | --- | --- |
| `login.feature` | Valid login; invalid password; invalid username; empty credentials; 3 invalid-combination examples; account lockout after 5 failed logins | `@smoke`, `@regression`, `@locked` |
| `pim.feature` | Add employee with generated unique name; search existing employee; search non-existent employee; 2 data-driven add examples; add from JSON test data | `@smoke`, `@regression` |
| `leave.feature` | Apply leave with valid dates; To-before-From validation; 2 leave-type examples | `@smoke`, `@regression` — the smoke Leave scenario adds its own entitlement first (see below) |

Smoke suite = **3 scenarios** (valid login, add employee, apply leave). Leave is back in smoke since 2026-11-25: its Given step adds a CAN - Personal entitlement for whoever is logged in at run time, so the demo rotating the Admin employee no longer leaves the Apply page with no leave balance.
Regression suite = **17 scenarios** (everything except the lockout scenario).

## Tech stack

| Area | Choice |
| --- | --- |
| Language / build | Java 17, Maven |
| UI automation | Selenium WebDriver 4.25 |
| BDD | Cucumber 7.20 (Gherkin features + TestNG runner) |
| Test runner | TestNG 7.10, sequential scenarios (`@DataProvider(parallel = false)`, 1 thread — see below) |
| Design pattern | Page Object Model with PageFactory |
| Waits | Explicit waits only (`WaitUtils`) — implicit wait is 0 |
| Driver management | WebDriverManager (downloads matching Chrome/Firefox) + `ThreadLocal<WebDriver>` |
| Reporting | Allure + Cucumber HTML/JSON, screenshot on failure (Cucumber + Allure attachment) |
| Logging | Log4j2 (console + `target/logs/test.log`) |
| Test data | JSON (`src/test/resources/testdata/employees.json`) + generated unique names |
| CI/CD | GitHub Actions (Chrome headless) + Jenkinsfile (parameterised browser/suite) |

## Project structure

```text
orangehrm-bdd-framework/
├── pom.xml                                  # Selenium, Cucumber, TestNG, Allure, Log4j2
├── Jenkinsfile                              # Parameterised Jenkins pipeline
├── .github/workflows/ci.yml                # GitHub Actions: regression on push/PR
├── src/main/java/com/owais/orangehrm/
│   ├── config/
│   │   ├── ConfigReader.java               # config.properties + -D overrides
│   │   └── DriverManager.java              # ThreadLocal WebDriver (Chrome/Firefox)
│   ├── pages/
│   │   ├── BasePage.java                   # PageFactory + explicit-wait helpers
│   │   ├── LoginPage.java                  # Login + error banners
│   │   ├── DashboardPage.java              # Left navigation, logout
│   │   ├── PimPage.java                    # Add employee, search, results
│   │   ├── LeavePage.java                  # Apply leave, date validation
│   │   └── EntitlementPage.java            # Add Leave Entitlement (test-data setup for Leave)
│   └── utils/
│       ├── WaitUtils.java                  # Explicit waits only
│       ├── TestContext.java                # ThreadLocal scenario scratchpad
│       ├── ScreenshotUtils.java            # Failure screenshots
│       ├── EmployeeDataGenerator.java      # Unique letters-only names
│       └── JsonTestData.java               # Jackson reader for test data
├── src/test/java/com/owais/orangehrm/
│   ├── runners/
│   │   ├── SmokeRunner.java                # @smoke
│   │   ├── RegressionRunner.java           # @regression (default)
│   │   └── LockedAccountRunner.java        # @locked, on demand only
│   ├── stepdefs/                           # LoginSteps, PimSteps, LeaveSteps, CommonSteps
│   └── hooks/Hooks.java                    # Browser per scenario + screenshot on failure
└── src/test/resources/
    ├── config.properties                   # baseUrl, browser, headless, timeouts
    ├── testng.xml                          # Default suite (regression, 3 threads)
    ├── testng-smoke.xml                    # Smoke suite
    ├── log4j2.xml
    ├── features/                           # login.feature, pim.feature, leave.feature
    └── testdata/employees.json
```

## Prerequisites

- Java 17+ (`java -version`)
- Maven 3.9+ (`mvn -version`)
- Chrome or Firefox (WebDriverManager downloads the matching driver)
- Internet access to `opensource-demo.orangehrmlive.com`

## How to run

```bash
# Full regression (default suite, Chrome headless, sequential, 1 thread)
mvn clean test

# Smoke suite only (3 scenarios)
mvn test -Dtest=SmokeRunner
# or: mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-smoke.xml

# Visible browser (headed)
mvn test -Dheadless=false

# Firefox
mvn test -Dbrowser=firefox

# A single tag, e.g. only login scenarios
mvn test -Dcucumber.filter.tags="@smoke"

# Lockout scenario only (see warning below)
mvn test -Dtest=LockedAccountRunner
```

Every option can also be set in `src/test/resources/config.properties`;
a `-D` system property always wins over the file.

## Reports

```bash
mvn allure:serve        # Interactive Allure report (needs the Allure CLI download)
mvn allure:report       # Static report in target/allure-report/
```

- Cucumber HTML: `target/cucumber-reports/regression.html`
- Surefire/TestNG: `target/surefire-reports/`
- Logs: `target/logs/test.log`
- Failure screenshots: `target/screenshots/`

## CI/CD

- **GitHub Actions** (`.github/workflows/ci.yml`): runs `mvn clean test` on Chrome
  headless for every push/PR to `main`, then uploads Cucumber, Allure and log artifacts.
- **Jenkins** (`Jenkinsfile`): parameterised pipeline (`BROWSER`, `SUITE`), publishes
  JUnit results, archives reports and posts an Allure report (requires the Jenkins
  Allure plugin and tool names `JDK17` / `Maven3` — rename them to match your server).

## Design notes

- **Parallel-safe by construction:** the driver lives in a `ThreadLocal`, scenario
  data lives in a `ThreadLocal` `TestContext`, and step definitions create page
  objects lazily from the current thread's driver.
- **No flaky sleeps:** every interaction waits explicitly; the implicit wait is 0.
- **Unique test data:** new employees get a random letters-only suffix, so reruns
  never collide with earlier runs on the shared demo.
- **Fast-failing toasts:** the "Successfully Saved" toast auto-dismisses, so page
  objects capture it immediately after submit and store it in `TestContext`.

## Known limitations

- The OrangeHRM demo is a **shared environment** — data created by one run is
  visible to the next, and the demo resets periodically. Unique generated names
  keep the PIM scenarios independent anyway.
- The **account-lockout** scenario (`@locked`) really does lock the shared `Admin`
  account for a while, so it is excluded from the smoke/regression runners and
  must be run alone with `LockedAccountRunner`.
- Leave dates are generated relative to today (`+7` / `+10` days), so the leave
  scenarios stay valid on any run date.
- **Leave sets up its own data.** The shared demo rotates the Admin
  employee on every login (Duane Cross, then John Doe, then Isabella Howe,
  then "manda user", then "Siva Test"), so an entitlement added yesterday never covers
  today's employee and the Apply page showed "No Leave Types with Leave
  Balance". Since 2026-11-25 every Leave scenario starts with
  `Given the logged-in employee has a "<leaveType>" leave entitlement`:
  `LeaveSteps` reads the name from the Dashboard top-right dropdown and
  `EntitlementPage` adds a 10-day entitlement for that employee, for the
  current year's leave period, in the same session — confirming the
  "Existing Entitlement ... will be updated" dialog if it appears.
- **Regression runs sequentially, not in parallel.** CI Run #1 ran 3
  threads in parallel and failed 10/17 — 3 browsers at once, all as the
  same Admin on the shared demo, overloaded the site (toasts missed,
  pages timing out, autocomplete timing out). Since 2026-11-25 both
  runners use `@DataProvider(parallel = false)` and testng.xml uses
  `parallel="none" thread-count="1"`. Slower, but stable on this demo.

## Author

**Owais Shakil Bewnak** — QA Automation Engineer / SDET
[GitHub](https://github.com/Owaisbewnak) · [LinkedIn](https://linkedin.com/in/owais-bewnak)
