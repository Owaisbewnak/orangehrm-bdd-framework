@regression
Feature: PIM — Add and Search Employee
  As an HR admin I want to add and search employees in PIM
  So that employee records stay accurate and findable

  Background:
    Given I am logged in as the Admin user

  @smoke
  Scenario: Add a new employee with a unique name
    When I navigate to the PIM module
    And I add a new employee with a generated unique name
    Then the employee should be created successfully
    And the Personal Details page should be displayed

  Scenario: Search for an existing employee
    When I navigate to the PIM module
    And I search for employee "Linda Anderson"
    Then the search results should contain "Linda Anderson"

  Scenario: Search for a non-existent employee
    When I navigate to the PIM module
    And I search for a non-existent employee "ZzzNoSuch Employee"
    Then I should see "No Records Found"

  Scenario Outline: Add employees with different name combinations
    When I navigate to the PIM module
    And I add an employee with first name "<firstName>" and last name "<lastName>"
    Then the employee should be created successfully

    Examples:
      | firstName | lastName |
      | Priya     | Sharma   |
      | Rahul     | Verma    |

  Scenario: Add an employee from the JSON test data file
    When I navigate to the PIM module
    And I add the first employee from the test data file
    Then the employee should be created successfully
    And the Personal Details page should be displayed
