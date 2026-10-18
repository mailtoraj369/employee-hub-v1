# Employee Hub

Employee Hub is a Spring Boot REST API for working with employee data stored in SQLite. It exposes searchable employee records, pagination support, and aggregate reporting for departments by location and salary threshold.

## Project gist

- Java 21 + Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- SQLite database
- Springdoc OpenAPI / Swagger UI
- Config-driven pagination and salary validation
- Service layer abstraction between controller and repository

This project is designed to serve as a lightweight employee directory and reporting backend. It lets you:

- retrieve employees with pagination
- count total employees
- filter employees by country/location
- summarize department counts for a given location and salary threshold

## BDD / Cucumber preview

The repository includes BDD-style coverage for the core employee reporting flows.

- Feature file: `src/test/resources/features/employee-queries.feature`
- Step definitions: `src/test/java/com/acme/employeehub/cucumber/EmployeeSteps.java`
- Test runner: `src/test/java/com/acme/employeehub/cucumber/EmployeeCucumberTest.java`

Example feature preview:

```gherkin
Feature: Employee queries
  Scenario: Get all employees
    Given the employee records are loaded
    When I request all employees with page number 0 and page size 10
    Then I should receive 4 employees

  Scenario: Get highest paid employees by location and department
    Given the employee records are loaded
    When I request the highest paid employees by location and department
    Then I should receive the top paid employee for each location and department

  Scenario: Get employee count by country
    Given the employee records are loaded
    When I request the employee count by country
    Then I should receive counts for USA, India, and Canada
```

Run just the BDD suite with:

```bash
./mvnw test -Dtest=EmployeeCucumberTest
```

## Docker

Before building the Docker image, create the packaged application artifact first:

```bash
./mvnw clean install
```

Then build the image:

```bash
docker build -t employee-hub .
```

Run the container with the default profile and the app port mapped to `9090`:

```bash
docker run --rm -p 9090:9090 -e SPRING_PROFILES_ACTIVE=dev employee-hub
```

Override the profile and port when needed:

```bash
docker run --rm -p 8081:8081 -e SPRING_PROFILES_ACTIVE=prod -e SERVER_PORT=8081 employee-hub
```

Notes:
- The app default profile is `dev`.
- The default app port is `9090`.
- `SPRING_PROFILES_ACTIVE` and `SERVER_PORT` can be overridden at runtime.
- The container image exposes port `9090` by default, but you can map it to any host port with `-p HOST_PORT:CONTAINER_PORT`.

## Run locally

```bash
./mvnw spring-boot:run
```

By default, the app loads the `dev` profile and starts on port `9090`.

You can override the profile and port at runtime:

```bash
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
SPRING_PROFILES_ACTIVE=dev SERVER_PORT=8081 ./mvnw spring-boot:run
```

The default local URL is:

```text
http://localhost:9090
```

If you override the server port, use the custom value instead, for example:

```text
http://localhost:8081
```

## Swagger / OpenAPI URLs

Once the app is running, Swagger docs are available at:

- Swagger UI: http://localhost:8080/swagger-ui.html
- Swagger UI (newer path): http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml

If Spring Security is active in your environment, the Swagger page may require the generated credentials shown in the application startup logs.

## API endpoints

Base path:

```text
/api/employees
```

### 1) Get all employees

```http
GET /api/employees?pageNumber=0&pageSize=30
```

Returns a paginated list of employee records.

Query parameters:
- `pageNumber` (optional, default: `0`)
- `pageSize` (optional, default: `30`)

Validation:
- `pageNumber` must be `>= 0`
- `pageSize` must be `>= 1`

Example response:

```json
[
  {
    "empId": "EMP00001",
    "name": "Emily Zammit",
    "email": "emily.zammit1@company.com",
    "department": "Marketing",
    "salary": 9992062,
    "currency": "INR",
    "location": "India"
  }
]
```

### 2) Get employee count

```http
GET /api/employees/count
```

Returns the total number of employees in the database.

Example response:

```json
20000
```

### 3) Get employees by country/location

```http
GET /api/employees/country/{country}
```

Example:

```http
GET /api/employees/country/USA
```

This performs a case-insensitive match on the employee `location` field.

### 4) Get highest paid employees by location and department

```http
GET /api/employees/highest-paid
```

Returns the highest-paid employee for each `(location, department)` group, ordered by salary descending.

Example response:

```json
[
  {
    "empId": "EMP_02",
    "name": "Bob",
    "email": "bob@acme.com",
    "department": "Engineering",
    "salary": 950000,
    "currency": "USD",
    "location": "USA"
  },
  {
    "empId": "EMP_04",
    "name": "Dan",
    "email": "dan@acme.com",
    "department": "Sales",
    "salary": 880000,
    "currency": "USD",
    "location": "USA"
  }
]
```

### 5) Get employee count by country

```http
GET /api/employees/country-counts
```

Returns the total employee count grouped by country/location.

Example response:

```json
[
  {
    "location": "Canada",
    "employeeCount": 1
  },
  {
    "location": "India",
    "employeeCount": 2
  },
  {
    "location": "USA",
    "employeeCount": 4
  }
]
```

### 6) Get department counts by location and salary threshold

```http
GET /api/employees/department-counts?location=USA&salaryThreshold=500000
```

Returns the number of employees in each department in the specified location whose salary is greater than the provided threshold.

Query parameters:
- `location` (required)
- `salaryThreshold` (optional, default configured threshold)

Example response:

```json
[
  {
    "department": "Engineering",
    "location": "USA",
    "employeeCount": 143
  },
  {
    "department": "Finance",
    "location": "USA",
    "employeeCount": 52
  }
]
```

## Configuration notes

The app uses configuration properties under `employees` in `application.yaml`:

```yaml
employees:
  pagination:
    default-page-number: 0
    default-page-size: 30
  salary:
    high-salary-threshold: 1000000
  defaults:
    currency: USD
```

This means:
- default page number is `0`
- default page size is `30`
- the high-salary threshold used for department aggregation is `1000000`
- default currency is `USD`

## Tech overview

This project follows a common Spring Boot layered structure:

- `controller` - REST endpoints
- `service` - business logic and validation
- `repository` - JPA access to SQLite data
- `entity` - database model
- `dto` - API response shapes
- `config` - app configuration properties
- `exception` - centralized error handling

## Notes

- The database is SQLite and is configured through `application.yaml`.
- The application expects a valid employee dataset to be available in the configured SQLite file.
- Swagger is enabled by default through springdoc.

