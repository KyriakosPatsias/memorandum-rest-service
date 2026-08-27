# Memorandum REST API service

![Java CI with Gradle](https://github.com/KyriakosPatsias/memorandum-rest-service/workflows/Java%20CI%20with%20Gradle/badge.svg)

A REST API for managing personal notes. Notes have a title, content, category and
favorite flag, and are persisted in Postgres. All endpoints are secured as an OAuth2
resource server, validating JWTs issued by AWS Cognito.

## Tech stack

- Java 21
- Spring Boot 4.1 (Spring Framework 7, Spring Security 7)
- Spring Data JPA + PostgreSQL
- Spring Security OAuth2 resource server (AWS Cognito JWT validation)
- Gradle 9 (wrapper included, no local Gradle install needed)
- JUnit 5 + Testcontainers (Postgres) for integration tests

## API

Base path: `/api/v1/notes`

| Method | Path              | Description       |
|--------|-------------------|--------------------|
| GET    | `/api/v1/notes`     | List all notes     |
| GET    | `/api/v1/notes/{id}`| Get a note by id   |
| POST   | `/api/v1/notes`     | Create a note      |
| PUT    | `/api/v1/notes/{id}`| Update a note      |
| DELETE | `/api/v1/notes/{id}`| Delete a note      |

Every request must include a valid Cognito-issued JWT: `Authorization: Bearer <token>`.

## Prerequisites

- JDK 21
- A running PostgreSQL instance for the `dev`/`prod` profiles
- Docker, if you want to run the test suite (Testcontainers spins up Postgres in a container)

## Configuration

There is no default `application.yml` — you must activate a profile:

- `application-dev.yml`: connects to `localhost:5432/memorandum` (user `postgres`,
  password `mysecretpassword`), `ddl-auto: update`.
- `application-prod.yml`: expects the datasource to be provided externally.

Both profiles point `spring.security.oauth2.resourceserver.jwt.issuer-uri` at a Cognito
user pool. The `JwtDecoder` bean resolves this issuer at startup, so the app **will fail
to start** without network access to it (or without the value overridden).

## Build

```bash
./gradlew build
```

## Run

1. Start a local Postgres matching the `dev` profile:

   ```bash
   docker run --name memorandum-pg -e POSTGRES_PASSWORD=mysecretpassword \
     -e POSTGRES_DB=memorandum -p 5432:5432 -d postgres:15-alpine
   ```

2. Run the app with the `dev` profile:

   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=dev'
   ```

   or build and run the jar directly:

   ```bash
   ./gradlew bootJar
   java -jar build/libs/Memorandum-1.3-SNAPSHOT.jar --spring.profiles.active=dev
   ```

The app listens on port `8080`.

### Testing without Cognito

To hit the API locally without a real Cognito token, edit
[`SecurityConfigurerAdapter`](src/main/java/com/kyriakospatsias/SecurityConfigurerAdapter.java):
comment out the `.authorizeHttpRequests(...)` / `.oauth2ResourceServer(...)` lines and
uncomment the `.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())`
line instead.

## Tests

```bash
./gradlew test
```

Integration tests use Testcontainers to launch a throwaway Postgres container, so Docker
must be running.
