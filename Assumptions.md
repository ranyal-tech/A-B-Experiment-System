# Assumptions

## System Behavior
- Each user is assigned to a single variant per experiment.
- The same user should always receive the same variant (consistency).

## Traffic & Scale
- The system is designed for moderate traffic.
- The application is stateless and can be scaled horizontally.

## Data Storage
- Redis is used as the primary store for user-variant assignments.
- No persistent database is used in this implementation.
- Loss of data in Redis is acceptable for this assignment.

## Failure Handling
- Redis is assumed to be available during normal operation.
- If Redis is unavailable, the system may fail to fetch or store assignments.
- No fallback mechanism is currently implemented.

## API Assumptions
- Input parameter (userId) is valid.
- No authentication or authorization is implemented.

## Deployment
- The system runs using Docker.
- Redis runs as a separate container and is accessible via service name.