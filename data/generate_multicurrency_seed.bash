#!/bin/bash

OUTPUT_FILE="employees_multicurrency_seed.sql"
TOTAL_RECORDS=20000

echo "Generating $TOTAL_RECORDS seed records into $OUTPUT_FILE..."

# Write the database schema header
cat << EOF > "$OUTPUT_FILE"
CREATE TABLE IF NOT EXISTS employees (
    emp_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    department VARCHAR(50),
    salary INT,
    currency VARCHAR(5),
    location VARCHAR(100)
);

INSERT INTO employees (emp_id, name, email, department, salary, currency, location) VALUES
EOF

# High-performance engine to generate randomized bulk text blocks
awk -v total="$TOTAL_RECORDS" '
BEGIN {
    srand(); # Seed the random number generator
    
    # Data arrays
    split("John Jane Alex Emily Michael Sarah David Jessica James Amanda Luke Elena Rajesh Priya Amit Ananya", first_names, " ");
    split("Smith Borg Camilleri Vella Farrugia Zammit Galea Micallef Grech Attard Sharma Patel Kumar Nair", last_names, " ");
    split("Engineering Sales Marketing HR Finance Product Operations", departments, " ");
    
    # Geolocation lookup maps (Index 1=USA, 2=India, 3=Germany, 4=UK)
    split("USA India Germany UK", countries, " ");
    split("USD INR EUR GBP", currencies, " ");
    
    # Approximate current conversion multiplier factors relative to 1 USD
    # Base USD range: 60,000 to 250,000
    split("1.0 87.5 0.92 0.77", conversion_rates, " ");

    fn_count = 16; ln_count = 14; dept_count = 7; country_count = 4;

    for (i = 1; i <= total; i++) {
        # Select random name and department
        fn = first_names[int(rand() * fn_count) + 1];
        ln = last_names[int(rand() * ln_count) + 1];
        dept = departments[int(rand() * dept_count) + 1];
        
        # Pick country index
        c_idx = int(rand() * country_count) + 1;
        loc = countries[c_idx];
        curr = currencies[c_idx];
        rate = conversion_rates[c_idx];
        
        # Calculate base salary in USD ($60k - $250k)
        usd_salary = int(rand() * (250000 - 60000 + 1)) + 60000;
        
        # Convert to native local currency value
        local_salary = int(usd_salary * rate);
        
        name = fn " " ln;
        email = tolower(fn) "." tolower(ln) i "@company.com";
        emp_id = sprintf("EMP%05d", i);
        ending = (i == total) ? ";\n" : ",\n";
        
         printf "('\''%s'\'', '\''%s'\'', '\''%s'\'', '\''%s'\'', %d, '\''%s'\'', '\''%s'\'')%s", emp_id, name, email, dept, local_salary, curr, loc, ending
    }
}' >> "$OUTPUT_FILE"

echo "Done! File '$OUTPUT_FILE' created successfully."
