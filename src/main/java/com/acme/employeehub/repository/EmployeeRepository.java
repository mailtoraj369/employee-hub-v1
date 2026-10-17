package com.acme.employeehub.repository;

import com.acme.employeehub.entity.DepartmentLocationCount;
import com.acme.employeehub.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    List<Employee> findByLocationContainingIgnoreCase(String country);

    @Query("""
        SELECT e.department AS department,
               e.location AS location,
               COUNT(e.name) AS employeeCount
        FROM Employee e
        WHERE e.salary > :salaryThreshold
          AND e.location = :location
        GROUP BY e.department, e.location
        """)
    List<DepartmentLocationCount> countByDepartmentAndLocation(
            @Param("salaryThreshold") Integer salaryThreshold,
            @Param("location") String location
    );
}
