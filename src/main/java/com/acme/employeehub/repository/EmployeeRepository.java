package com.acme.employeehub.repository;

import com.acme.employeehub.entity.CountryEmployeeCount;
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

    @Query(value = """
        SELECT location,
               COUNT(*) AS employeeCount
        FROM employees
        GROUP BY location
        ORDER BY location
        """, nativeQuery = true)
    List<CountryEmployeeCount> countEmployeesByCountry();

    @Query(value = """
    WITH ranked AS (
        SELECT emp_id,
               name,
               email,
               department,
               salary,
               currency,
               location,
               DENSE_RANK() OVER (
                   PARTITION BY location, department
                   ORDER BY salary DESC
               ) AS salary_rank
        FROM employees
    )
    SELECT emp_id, name, email, department, salary, currency, location
    FROM ranked
    WHERE salary_rank = 1
    ORDER BY salary DESC, location, department
    """, nativeQuery = true)
    List<Employee> findHighestPaidEmployeesByLocationAndDepartment();
}
