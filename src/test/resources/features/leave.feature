@regression
Feature: Leave — Apply for Leave
  As an employee I want to apply for leave
  So that my time off is recorded and approved

  Background:
    Given I am logged in as the Admin user

  # Back in @smoke (2026-11-25): the scenario now sets up its own data first.
  # The Given step adds a CAN - Personal entitlement for whoever is logged
  # in right now (name read from the Dashboard, never hard-coded), because
  # the shared demo rotates the Admin employee on every login (Duane Cross,
  # John Doe, Isabella Howe, "manda user", ...) and an entitlement added
  # yesterday never covers today's employee.
  @smoke
  Scenario: Apply for leave with valid dates
    Given the logged-in employee has a "CAN - Personal" leave entitlement
    When I navigate to the Leave module
    And I apply for "CAN - Personal" leave from 7 days from today to 10 days from today
    Then the leave should be applied successfully

  Scenario: Apply for leave with To date before From date
    Given the logged-in employee has a "CAN - Personal" leave entitlement
    When I navigate to the Leave module
    And I apply for "CAN - Personal" leave from 10 days from today to 7 days from today
    Then I should see a leave validation error containing "after"

  Scenario Outline: Apply for different leave types
    Given the logged-in employee has a "<leaveType>" leave entitlement
    When I navigate to the Leave module
    And I apply for "<leaveType>" leave from 7 days from today to 9 days from today
    Then the leave should be applied successfully

    Examples:
      | leaveType     |
      | CAN - Personal |
      | CAN - Vacation |
