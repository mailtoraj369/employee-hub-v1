package com.acme.employeehub.mapper;

import com.acme.employeehub.dto.EmployeeDto;
import com.acme.employeehub.entity.Employee;

public class EmployeeCopyUtil {

    private EmployeeCopyUtil() {
        // Utility class
    }

    public static EmployeeDto toDto(Employee employee) {
        if (employee == null) {
            return null;
        }

        return new EmployeeDto(
                employee.getEmpId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getSalary(),
                employee.getCurrency(),
                employee.getLocation()
        );
    }
}
