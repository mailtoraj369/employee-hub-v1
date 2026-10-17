package com.acme.employeehub.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "employees")
public record EmployeeProperties(
        @Valid Pagination pagination,
        @Valid Salary salary,
        @Valid Defaults defaults
) {
    public record Pagination(
            @Min(0) int defaultPageNumber,
            @Min(1) int defaultPageSize
    ) {}

    public record Salary(
            @Min(2) int highSalaryThreshold
    ) {}

    public record Defaults(
            @NotBlank String currency
    ) {}
}
