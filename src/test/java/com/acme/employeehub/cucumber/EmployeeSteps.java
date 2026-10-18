package com.acme.employeehub.cucumber;

import com.acme.employeehub.entity.CountryEmployeeCount;
import com.acme.employeehub.entity.Employee;
import com.acme.employeehub.repository.EmployeeRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@CucumberContextConfiguration
@SpringBootTest
@ActiveProfiles("test")
public class EmployeeSteps {

    @Autowired
    private EmployeeRepository employeeRepository;

    private List<Employee> allEmployees;
    private List<Employee> topPaidEmployees;
    private List<CountryEmployeeCount> countryCounts;

    @Given("the employee records are loaded")
    public void loadEmployeeRecords() {
        employeeRepository.deleteAll();
        employeeRepository.saveAll(List.of(
                new Employee("EMP_02", "Bob", "bob@acme.com", "Engineering", 950_000, "USD", "USA"),
                new Employee("EMP_04", "Dan", "dan@acme.com", "Sales", 880_000, "USD", "USA"),
                new Employee("EMP_05", "Eve", "eve@acme.com", "Engineering", 820_000, "INR", "India"),
                new Employee("EMP_06", "Frank", "frank@acme.com", "Finance", 750_000, "CAD", "Canada")
        ));
    }

    @When("I request all employees with page number {int} and page size {int}")
    public void requestAllEmployees(int pageNumber, int pageSize) {
        allEmployees = employeeRepository.findAll(org.springframework.data.domain.PageRequest.of(pageNumber, pageSize)).getContent();
    }

    @Then("I should receive {int} employees")
    public void assertEmployeeCount(int expectedCount) {
        assertThat(allEmployees).hasSize(expectedCount);
    }

    @Then("the first employee should be Bob")
    public void assertFirstEmployeeIsBob() {
        assertThat(allEmployees.get(0).getName()).isEqualTo("Bob");
    }

    @When("I request the highest paid employees by location and department")
    public void requestHighestPaidEmployees() {
        topPaidEmployees = employeeRepository.findHighestPaidEmployeesByLocationAndDepartment();
    }

    @Then("I should receive the top paid employee for each location and department")
    public void assertTopPaidEmployeesReturned() {
        assertThat(topPaidEmployees).isNotEmpty();
        assertThat(topPaidEmployees.stream().map(Employee::getName).collect(Collectors.toSet()))
                .contains("Bob", "Dan", "Eve", "Frank");
    }

    @Then("the highest paid employee should be Bob from USA Engineering")
    public void assertHighestPaidEmployeeForUsaEngineering() {
        Employee employee = topPaidEmployees.stream()
                .filter(e -> e.getLocation().equals("USA") && e.getDepartment().equals("Engineering"))
                .findFirst()
                .orElseThrow();
        assertThat(employee.getName()).isEqualTo("Bob");
        assertThat(employee.getSalary()).isEqualTo(950_000);
    }

    @When("I request the employee count by country")
    public void requestEmployeeCountByCountry() {
        countryCounts = employeeRepository.countEmployeesByCountry();
    }

    @Then("I should receive counts for USA, India, and Canada")
    public void assertCountryCountRowsPresent() {
        assertThat(countryCounts)
                .extracting(CountryEmployeeCount::getLocation)
                .contains("USA", "India", "Canada");
    }

    @Then("the USA count should be {int}")
    public void assertUsaCount(int expectedCount) {
        CountryEmployeeCount usa = countryCounts.stream()
                .filter(c -> c.getLocation().equals("USA"))
                .findFirst()
                .orElseThrow();
        assertThat(usa.getEmployeeCount()).isEqualTo((long) expectedCount);
    }
}
