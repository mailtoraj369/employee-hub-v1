# API Performance Report

## Scope

This report covers the EmployeeController API endpoint used to fetch paginated employees, excluding the AI controller.

Endpoint under test:
- GET /api/employees?pageNumber=0&pageSize=20

## Test methodology

- Benchmark tool: Python standard library (`urllib` + `ThreadPoolExecutor`)
- Concurrency model: 10 workers, 100 requests per worker
- Total requests: 1,000
- Success criteria: HTTP 2xx response only
- Environment: local Spring Boot application running on the development machine

This is a standard latency and throughput check using industry-recognized metrics: mean, median (P50), p90, p95, p99, p99.9, max, error rate, and throughput.

## Results

- Success: 1,000 / 1,000
- Error rate: 0.00%
- Duration: 5.17 seconds
- Throughput: 193.53 requests/second

Latency summary (milliseconds):
- Min: 5.62 ms
- Mean: 46.47 ms
- P50: 16.61 ms
- P90: 102.52 ms
- P95: 157.87 ms
- P99: 467.42 ms
- P99.9: 1586.84 ms
- Max: 1586.84 ms

## Interpretation

- The endpoint is stable under a moderate concurrent load with no errors.
- P95 at 157.87 ms indicates the endpoint is generally responsive for a lightweight read API.
- P99 at 467.42 ms shows tail-latency spikes, which may be caused by local machine variability, SQLite access patterns, or occasional concurrent contention.
- Throughput of 193.53 req/s is solid for this local development environment and is appropriate for a simple employee listing endpoint.

## Performance assessment

Overall assessment: healthy for a local SQLite-backed employee listing endpoint.

The API is suitable for normal operational use in a small to medium workload, but the following should be monitored in a production or staging environment:

- P95 and P99 latency over time
- DB query times under higher concurrency
- CPU and memory usage as request volume grows
- Repeatability of tail latency spikes

## Recommendation

Continue to monitor this endpoint with application-level metrics and add API observability (request duration, DB query timings, saturation, and error tracking) before scaling beyond local development load.

For production readiness, a target of keeping P95 under 200 ms and P99 under 1 second would be a reasonable operational threshold for this endpoint.
