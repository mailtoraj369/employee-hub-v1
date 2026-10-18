package com.acme.employeehub.controller;

import com.acme.employeehub.config.EmployeeProperties;
import com.acme.employeehub.dto.CountryEmployeeCountDto;
import com.acme.employeehub.dto.DepartmentLocationCountDto;
import com.acme.employeehub.dto.EmployeeDto;
import com.acme.employeehub.exception.GlobalExceptionHandler;
import com.acme.employeehub.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({EmployeeControllerMvcTest.EmployeePropertiesTestConfig.class, GlobalExceptionHandler.class})
class EmployeeControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void getAllEmployees_returnsEmployeesUsingDefaultPagination() throws Exception {
        EmployeeDto employee = new EmployeeDto(
                "EMP-001",
                "Alice",
                "alice@acme.com",
                "Engineering",
                600000,
                "USD",
                "USA"
        );

        when(employeeService.getAllEmployees(0, 10)).thenReturn(List.of(employee));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].empId").value("EMP-001"))
                .andExpect(jsonPath("$[0].location").value("USA"));

        verify(employeeService).getAllEmployees(0, 10);
    }

    @Test
    void getAllEmployees_usesProvidedPaginationValues() throws Exception {
        when(employeeService.getAllEmployees(2, 5)).thenReturn(List.of());

        mockMvc.perform(get("/api/employees")
                        .param("pageNumber", "2")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService).getAllEmployees(2, 5);
    }

    @Test
    void getAllEmployees_returnsValidationErrorWhenPageNumberIsNegative() throws Exception {
        mockMvc.perform(get("/api/employees").param("pageNumber", "-1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("EMP-4001"))
                .andExpect(jsonPath("$.message").value("page must be 0 or greater"));
    }

    @Test
    void getAllEmployees_returnsValidationErrorWhenPageSizeIsLessThanOne() throws Exception {
        mockMvc.perform(get("/api/employees").param("pageSize", "0"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("EMP-4002"))
                .andExpect(jsonPath("$.message").value("size must be at least 1"));
    }

    @Test
    void getEmployeeCount_returnsCount() throws Exception {
        when(employeeService.getEmployeeCount()).thenReturn(7L);

        mockMvc.perform(get("/api/employees/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(7));

        verify(employeeService).getEmployeeCount();
    }

    @Test
    void getEmployeesByCountry_returnsEmployeesFromCountry() throws Exception {
        EmployeeDto employee = new EmployeeDto(
                "EMP-002",
                "Bob",
                "bob@acme.com",
                "Sales",
                720000,
                "INR",
                "India"
        );

        when(employeeService.getEmployeesByCountry("India")).thenReturn(List.of(employee));

        mockMvc.perform(get("/api/employees/country/India"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Bob"))
                .andExpect(jsonPath("$[0].currency").value("INR"));

        verify(employeeService).getEmployeesByCountry("India");
    }

    @Test
    void getHighestPaidEmployeesByLocationAndDepartment_returnsEmployees() throws Exception {
        EmployeeDto highestPaidEmployee = new EmployeeDto(
                "EMP-200",
                "Charlie",
                "charlie@acme.com",
                "Engineering",
                920000,
                "USD",
                "USA"
        );

        when(employeeService.findHighestPaidEmployeesByLocationAndDepartment())
                .thenReturn(List.of(highestPaidEmployee));

        mockMvc.perform(get("/api/employees/highest-paid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].empId").value("EMP-200"))
                .andExpect(jsonPath("$[0].name").value("Charlie"))
                .andExpect(jsonPath("$[0].location").value("USA"));

        verify(employeeService).findHighestPaidEmployeesByLocationAndDepartment();
    }

    @Test
    void getCountryCounts_returnsEmployeeCountsByCountry() throws Exception {
        CountryEmployeeCountDto usa = new CountryEmployeeCountDto("USA", 3L);
        CountryEmployeeCountDto india = new CountryEmployeeCountDto("India", 2L);

        when(employeeService.countEmployeesByCountry()).thenReturn(List.of(usa, india));

        mockMvc.perform(get("/api/employees/country-counts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].location").value("USA"))
                .andExpect(jsonPath("$[0].employeeCount").value(3))
                .andExpect(jsonPath("$[1].location").value("India"))
                .andExpect(jsonPath("$[1].employeeCount").value(2));

        verify(employeeService).countEmployeesByCountry();
    }

    @Test
    void getDepartmentCounts_usesDefaultSalaryThresholdWhenMissing() throws Exception {
        DepartmentLocationCountDto dto = new DepartmentLocationCountDto("Engineering", "USA", 3L);
        when(employeeService.countByDepartmentAndLocation(500000, "USA")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/employees/department-counts").param("location", "USA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].department").value("Engineering"))
                .andExpect(jsonPath("$[0].employeeCount").value(3L));

        verify(employeeService).countByDepartmentAndLocation(500000, "USA");
    }

    @Test
    void getDepartmentCounts_usesProvidedSalaryThreshold() throws Exception {
        DepartmentLocationCountDto dto = new DepartmentLocationCountDto("Sales", "USA", 2L);
        when(employeeService.countByDepartmentAndLocation(250000, "USA")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/employees/department-counts")
                        .param("salaryThreshold", "250000")
                        .param("location", "USA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].department").value("Sales"));

        verify(employeeService).countByDepartmentAndLocation(250000, "USA");
    }

    @Test
    void getDepartmentCounts_returnsValidationErrorWhenSalaryThresholdIsBelowOne() throws Exception {
        mockMvc.perform(get("/api/employees/department-counts")
                        .param("salaryThreshold", "0")
                        .param("location", "USA"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("EMP-4003"))
                .andExpect(jsonPath("$.message").value("Salary must be within the range of 1 to 500000"));
    }

    @Test
    void getDepartmentCounts_returnsValidationErrorWhenSalaryThresholdExceedsDefaultThreshold() throws Exception {
        mockMvc.perform(get("/api/employees/department-counts")
                        .param("salaryThreshold", "500001")
                        .param("location", "USA"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("EMP-4003"))
                .andExpect(jsonPath("$.message").value("Salary must be within the range of 1 to 500000"));
    }

    @TestConfiguration
    static class EmployeePropertiesTestConfig {
        @Bean
        EmployeeProperties employeeProperties() {
            return new EmployeeProperties(
                    new EmployeeProperties.Pagination(0, 10),
                    new EmployeeProperties.Salary(500000),
                    new EmployeeProperties.Defaults("USD")
            );
        }
    }
}
