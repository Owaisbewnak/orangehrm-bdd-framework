@regression
Feature: OrangeHRM Login
  As an HR user I want to log in to OrangeHRM
  So that I can access the HR modules

  Background:
    Given I am on the OrangeHRM login page

  @smoke
  Scenario: Valid login with Admin credentials
    When I login with username "Admin" and password "admin123"
    Then I should see the Dashboard page
    And the logged-in user name should be displayed

  Scenario: Login with invalid password
    When I login with username "Admin" and password "wrongPassword123"
    Then I should see an error message containing "Invalid credentials"
    And I should stay on the login page

  Scenario: Login with invalid username
    When I login with username "NoSuchUser" and password "admin123"
    Then I should see an error message containing "Invalid credentials"
    And I should stay on the login page

  Scenario: Login with empty credentials
    When I submit the login form without entering credentials
    Then I should see a validation error "Required"

  Scenario Outline: Login with invalid credential combinations
    When I login with username "<username>" and password "<password>"
    Then I should see an error message containing "Invalid credentials"

    Examples:
      | username | password      |
      | Admin    | Admin         |
      | admin    | admin123      |
      | Admin    | admin1234     |

  # WARNING: this scenario locks the shared demo Admin account for a while
  # and must NOT run in the same suite as the other scenarios.
  # Run it on demand only: mvn test -Dtest=LockedAccountRunner
  @locked
  Scenario: Account gets locked after 5 consecutive failed logins
    When I attempt to login with an invalid password 5 times
    Then I should see an error message containing "locked"
