package com.acme.employeehub.service;

import com.acme.employeehub.dto.CountryEmployeeCountDto;
import com.acme.employeehub.dto.DepartmentLocationCountDto;
import com.acme.employeehub.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {
    List<EmployeeDto> getAllEmployees(int pageNumber, int pageSize);

    long getEmployeeCount();

    List<EmployeeDto> getEmployeesByCountry(String country);

    List<DepartmentLocationCountDto> countByDepartmentAndLocation(Integer salaryThreshold, String location);

    List<CountryEmployeeCountDto> countEmployeesByCountry();

    List<EmployeeDto> findHighestPaidEmployeesByLocationAndDepartment();
}
