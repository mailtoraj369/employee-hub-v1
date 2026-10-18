Feature: Employee queries
  As an employee hub user
  I want to query employees by common business rules
  So that I can review the workforce summary and top earners

  Scenario: Get all employees
    Given the employee records are loaded
    When I request all employees with page number 0 and page size 10
    Then I should receive 4 employees
    And the first employee should be Bob

  Scenario: Get highest paid employees by location and department
    Given the employee records are loaded
    When I request the highest paid employees by location and department
    Then I should receive the top paid employee for each location and department
    And the highest paid employee should be Bob from USA Engineering

  Scenario: Get employee count by country
    Given the employee records are loaded
    When I request the employee count by country
    Then I should receive counts for USA, India, and Canada
    And the USA count should be 2
