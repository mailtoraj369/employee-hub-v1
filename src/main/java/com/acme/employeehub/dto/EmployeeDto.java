package com.acme.employeehub.dto;

import com.acme.employeehub.entity.Employee;
import com.acme.employeehub.mapper.EmployeeCopyUtil;

public record EmployeeDto(
    String empId,
    String name,
    String email,
    String department,
    Integer salary,
    String currency,
    String location
) {
        public static EmployeeDto from(Employee employee) {
            return EmployeeCopyUtil.toDto(employee);
        }
    }
