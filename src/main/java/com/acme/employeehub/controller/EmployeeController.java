package com.acme.employeehub.controller;

import com.acme.employeehub.config.EmployeeProperties;
import com.acme.employeehub.dto.DepartmentLocationCountDto;
import com.acme.employeehub.dto.EmployeeDto;
import com.acme.employeehub.exception.EmployeeValidationException;
import com.acme.employeehub.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeProperties employeeProperties;
    private final EmployeeService employeeService;

    public EmployeeController(
            EmployeeService employeeService,
            EmployeeProperties employeeProperties
    ) {
        this.employeeService = employeeService;
        this.employeeProperties = employeeProperties;
    }

    @GetMapping
    public List<EmployeeDto> getAllEmployees(
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize ) {
        var pagination = employeeProperties.pagination();

        int actualPageNumber = pageNumber != null ? pageNumber : pagination.defaultPageNumber();
        int actualPageSize = pageSize != null ? pageSize : pagination.defaultPageSize();

        if (actualPageNumber < 0) {
            throw new EmployeeValidationException("EMP-4001", "page must be 0 or greater");
        }
        if (actualPageSize < 1) {
            throw new EmployeeValidationException("EMP-4002", "size must be at least 1");
        }
        return employeeService.getAllEmployees(actualPageNumber, actualPageSize);
    }

    @GetMapping("/count")
    public long getEmployeeCount() {
        return employeeService.getEmployeeCount();
    }

    @GetMapping("/country/{country}")
    public List<EmployeeDto> getEmployeesByCountry(@PathVariable String country) {
        return employeeService.getEmployeesByCountry(country);
    }

    @GetMapping("/department-counts")
    public List<DepartmentLocationCountDto> getDepartmentCounts(
            @RequestParam(required = false) Integer salaryThreshold,
            @RequestParam String location
    ) {
        int defaultSalaryThreshold = employeeProperties.salary().highSalaryThreshold();
        int actualSalaryThreshold = salaryThreshold != null
                ? salaryThreshold : defaultSalaryThreshold;
        if (actualSalaryThreshold < 1 || actualSalaryThreshold > defaultSalaryThreshold) {
            throw new EmployeeValidationException("EMP-4003",
                    "Salary must be within the range of 1 to " + defaultSalaryThreshold);
        }
        return employeeService.countByDepartmentAndLocation(
                actualSalaryThreshold, location
        );
    }
}
