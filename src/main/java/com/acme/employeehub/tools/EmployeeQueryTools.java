package com.acme.employeehub.tools;

import com.acme.employeehub.dto.EmployeeDto;
import com.acme.employeehub.service.EmployeeService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeQueryTools {

    private final EmployeeService employeeService;

    public EmployeeQueryTools(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Tool("Get the employee count by country")
    public List<EmployeeDto> getEmployeeCountByCountry(String country) {
        return employeeService.getEmployeesByCountry(country);
    }

    @Tool("Get the list of employee")
    public List<EmployeeDto> getEmployees() {
        return employeeService.getAllEmployees(0, 100);
    }


    @Tool("Get the employee Maximum salary by country")
    public List<EmployeeDto> getEmployeeMaxSalaryByCountry(String country) {
        return employeeService.findHighestPaidEmployeesByLocationAndDepartment();
    }

}
