package com.acme.employeehub.dto;

public record DepartmentLocationCountDto(String department,
                                         String location,
                                         Long employeeCount
) {}
