    package com.acme.employeehub.repository;

    import com.acme.employeehub.entity.DepartmentLocationCount;
    import com.acme.employeehub.entity.Employee;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
    import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Sort;
    import org.springframework.test.context.ActiveProfiles;

    import java.util.List;

    import static org.assertj.core.api.Assertions.assertThat;
    import static org.assertj.core.api.AssertionsForClassTypes.tuple;


    @DataJpaTest
    @ActiveProfiles("test")
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    class EmployeeRepositoryTest {

        @Autowired
        private EmployeeRepository repository;

        @BeforeEach
        void clearDatabase() {
            repository.deleteAll();
        }

        @Test
        void findByCountry_returnsOnlyEmployeesInThatCountry() {
            repository.save(new Employee("EMP_01", "Alice", "testAlice@yl.com" ,"Engineering", 600_000,"USD","USA" ));
            repository.save(new Employee("EMP_02", "Bob", "testBob@yl.com", "Engineering", 700_000, "INR", "India"));
            repository.save(new Employee("EMP_03", "Cara", "testCara@yl.com", "Sales", 400_000, "USD", "USA"));

            List<Employee> result = repository.findByLocationContainingIgnoreCase("USA");

            assertThat(result)
                    .extracting(Employee::getName)
                    .containsExactlyInAnyOrder("Alice", "Cara");
        }

        @Test
        void findAll_returnsRequestedPage() {
            repository.save(new Employee("EMP_01", "Alice", "testAlice@yl.com" ,"Engineering", 600_000,"USD","USA" ));
            repository.save(new Employee("EMP_02", "Bob", "testBob@yl.com", "Engineering", 700_000, "INR", "India"));
            repository.save(new Employee("EMP_03", "Cara", "testCara@yl.com", "Sales", 400_000, "USD", "USA"));

            Page<Employee> page = repository.findAll(
                    PageRequest.of(0, 2, Sort.by("name").ascending())
            );

            assertThat(page.getContent())
                    .extracting(Employee::getName)
                    .containsExactly("Alice", "Bob");

            assertThat(page.getTotalElements()).isEqualTo(3);
            assertThat(page.getTotalPages()).isEqualTo(2);
        }

        @Test
        void departmentCounts_filtersByCountryAndSalary() {
            repository.save(new Employee("EMP_01", "Alice", "testAlice@yl.com" ,"Engineering", 600_000,"USD","USA" ));
            repository.save(new Employee("EMP_02", "Bob", "testBob@yl.com", "Engineering", 700_000, "INR", "India"));
            repository.save(new Employee("EMP_03", "Cara", "testCara@yl.com", "Sales", 400_000, "USD", "USA"));
            repository.save(new Employee("EMP_04", "Dan", "testDan@yl.com", "Finance", 900_000, "CAD", "Canada"));

            List<DepartmentLocationCount> rows =
                    repository.countByDepartmentAndLocation( 300_000, "USA");

            assertThat(rows)
                    .extracting(
                            DepartmentLocationCount::getDepartment,
                            DepartmentLocationCount::getLocation,
                            DepartmentLocationCount::getEmployeeCount
                    )
                    .containsExactlyInAnyOrder(
                            tuple("Engineering", "USA", 1L),
                            tuple("Sales", "USA", 1L)
                    );
        }

            @Test
            void countEmployeesByCountry_returnsCountsPerLocation() {
                repository.save(new Employee("EMP_01", "Alice", "testAlice@yl.com", "Engineering", 600_000, "USD", "USA"));
                repository.save(new Employee("EMP_02", "Bob", "testBob@yl.com", "Engineering", 700_000, "INR", "India"));
                repository.save(new Employee("EMP_03", "Cara", "testCara@yl.com", "Sales", 400_000, "USD", "USA"));
                repository.save(new Employee("EMP_04", "Dan", "testDan@yl.com", "Finance", 900_000, "CAD", "Canada"));

                List<com.acme.employeehub.entity.CountryEmployeeCount> rows = repository.countEmployeesByCountry();

                assertThat(rows)
                        .extracting(
                                com.acme.employeehub.entity.CountryEmployeeCount::getLocation,
                                com.acme.employeehub.entity.CountryEmployeeCount::getEmployeeCount
                        )
                        .containsExactlyInAnyOrder(
                                tuple("USA", 2L),
                                tuple("India", 1L),
                                tuple("Canada", 1L)
                        );
            }

            @Test
            void findHighestPaidEmployeesByLocationAndDepartment_returnsTopEmployeePerGroup() {
                repository.save(new Employee("EMP_01", "Alice", "alice@yl.com", "Engineering", 600_000, "USD", "USA"));
                repository.save(new Employee("EMP_02", "Bob", "bob@yl.com", "Engineering", 950_000, "USD", "USA"));
                repository.save(new Employee("EMP_03", "Cara", "cara@yl.com", "Sales", 700_000, "USD", "USA"));
                repository.save(new Employee("EMP_04", "Dan", "dan@yl.com", "Sales", 880_000, "USD", "USA"));
                repository.save(new Employee("EMP_05", "Eve", "eve@yl.com", "Engineering", 820_000, "INR", "India"));
                repository.save(new Employee("EMP_06", "Frank", "frank@yl.com", "Finance", 750_000, "CAD", "Canada"));

                List<Employee> result = repository.findHighestPaidEmployeesByLocationAndDepartment();

                assertThat(result)
                        .extracting(Employee::getName)
                        .containsExactly("Bob", "Dan", "Eve", "Frank");
            }
        }