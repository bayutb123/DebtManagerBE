# Debt & Receivable Manager (Backend)

Kotlin + Spring Boot 3 clean-architecture backend for managing personal debts/receivables with Google Sign-In and JWT.

## Tech Stack
- Kotlin 1.9, Spring Boot 3
- Spring Data JPA (Hibernate), PostgreSQL
- Flyway migrations
- JWT auth + Google ID token verification
- JUnit 5 + MockMvc tests

## Architecture
Layered under `src/main/kotlin/com/example/debtmanager`:
- config, security
- domain (model, repository)
- application (dto, service, usecase)
- infrastructure (entity, repository, migration)
- interfaces (controller, request, response)
- common (exception, utils)

## Prerequisites
- JDK 17
- PostgreSQL running locally
  - DB: `debtmanager`
  - User: `postgres`
  - Password: set in `application.yml` (default in example)
- Google OAuth Web Client ID

## Setup
1. Copy `src/main/resources/application-example.yml` to `src/main/resources/application.yml`.
2. Update:
   - `spring.datasource.*` for your Postgres credentials/host
   - `security.google.client-id` with your Google client ID
   - `security.jwt.secret` to a 32+ char secret

## Run
```
./gradlew bootRun
```
Server starts on `http://localhost:8080`.

## Key Endpoints
- `POST /auth/google` `{ "idToken": "<GOOGLE_ID_TOKEN>" }` → returns JWT + user info
- Contacts: CRUD `/contacts`
- Debts: CRUD `/debts`, pay `/debts/{id}/payment`
- Receivables: CRUD `/receivables`, pay `/receivables/{id}/payment`
- Transactions: `GET /transactions`, `GET /transactions/{id}`
- Dashboard: `GET /dashboard/summary`

All except `/auth/google` require `Authorization: Bearer <JWT>`.

## Migrations
Flyway SQL scripts in `src/main/resources/db/migration` (V1–V5).

## Tests
```
./gradlew test
```
Uses H2 in-memory + mocked Google verifier.

## Environment Notes
- For local-only testing without Google, you can mock the verifier via profile or supply a real ID token from Google Sign-In.
- Adjust CORS in `SecurityConfig` if your frontend runs on a different origin.

## Project Structure (paths)
- Entry: `src/main/kotlin/com/example/debtmanager/DebtManagerApplication.kt`
- Config: `config/SecurityConfig.kt`
- Security: `security/JwtTokenProvider.kt`, `security/JwtAuthenticationFilter.kt`
- Domain models: `domain/model/Models.kt`
- Repositories (ports): `domain/repository/Repositories.kt`
- Services/use cases: `application/service`, `application/usecase`
- Controllers: `interfaces/controller/Controllers.kt`
- DTOs: `interfaces/request`, `interfaces/response`
- Persistence adapters: `infrastructure/entity`, `infrastructure/repository`
- Migrations: `src/main/resources/db/migration`

## Quick cURL
```
# Authenticate
curl -X POST http://localhost:8080/auth/google \
  -H "Content-Type: application/json" \
  -d '{"idToken":"<GOOGLE_ID_TOKEN>"}'

# Create contact
curl -X POST http://localhost:8080/contacts \
  -H "Authorization: Bearer <JWT>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","phone":"12345","notes":"friend"}'
```

## License
Proprietary / Internal (adjust as needed).
