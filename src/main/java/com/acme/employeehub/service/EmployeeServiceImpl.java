package com.acme.employeehub.service;

import com.acme.employeehub.dto.CountryEmployeeCountDto;
import com.acme.employeehub.dto.DepartmentLocationCountDto;
import com.acme.employeehub.dto.EmployeeDto;
import com.acme.employeehub.entity.CountryEmployeeCount;
import com.acme.employeehub.entity.DepartmentLocationCount;
import com.acme.employeehub.entity.Employee;
import com.acme.employeehub.mapper.DepartmentLocationCountCopyUtil;
import com.acme.employeehub.mapper.EmployeeCopyUtil;
import com.acme.employeehub.repository.EmployeeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDto> getAllEmployees(int pageNumber, int pageSize) {
        List<Employee> employees = employeeRepository.findAll(
                PageRequest.of(pageNumber, pageSize, Sort.by("empId").ascending())
        ).getContent();
        return employees.stream()
                .map(EmployeeCopyUtil::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getEmployeeCount() {
        return employeeRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesByCountry(String country) {
        List<Employee> employees =  employeeRepository.findByLocationContainingIgnoreCase(country);
        return employees.stream()
                .map(EmployeeCopyUtil::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public List<DepartmentLocationCountDto> countByDepartmentAndLocation(Integer salaryThreshold, String location) {
        List<DepartmentLocationCount> departmentLocationCounts = employeeRepository.countByDepartmentAndLocation(salaryThreshold, location);
        return departmentLocationCounts.stream()
                .map(DepartmentLocationCountCopyUtil::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CountryEmployeeCountDto> countEmployeesByCountry() {
        List<CountryEmployeeCount> countryEmployeeCounts = employeeRepository.countEmployeesByCountry();
        return countryEmployeeCounts.stream()
                .map(count -> new CountryEmployeeCountDto(count.getLocation(), count.getEmployeeCount()))
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> findHighestPaidEmployeesByLocationAndDepartment() {
        List<Employee> highestPaidEmployees = employeeRepository.findHighestPaidEmployeesByLocationAndDepartment();
        return highestPaidEmployees.stream()
                .map(EmployeeCopyUtil::toDto)
                .collect(Collectors.toList());
    }
}
