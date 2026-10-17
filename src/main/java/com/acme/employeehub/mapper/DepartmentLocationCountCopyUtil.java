package com.acme.employeehub.mapper;

import com.acme.employeehub.dto.DepartmentLocationCountDto;
import com.acme.employeehub.entity.DepartmentLocationCount;

public final class DepartmentLocationCountCopyUtil {
    private DepartmentLocationCountCopyUtil() {}

    public static DepartmentLocationCountDto toDto(
            DepartmentLocationCount result
    ) {
        if (result == null) {
            return null;
        }

        return new DepartmentLocationCountDto(
                result.getDepartment(),
                result.getLocation(),
                result.getEmployeeCount()
        );
    }
}
