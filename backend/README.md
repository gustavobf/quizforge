# QuizForge

QuizForge is a backend API for creating and managing quiz questions and alternatives, built with **Spring Boot** and **Java 21**.

## About

This project follows a layered, hexagonal-style structure (Ports and Adapters) to keep business rules isolated from framework and infrastructure details.

### Main Features

- Create and manage questions
- Create and manage answer alternatives
- Input validation for request data
- REST API endpoints
- PostgreSQL persistence and Testcontainers integration tests
- Finish exams with separate correct, wrong and unanswered question totals
- Cancel unfinished exams without including them in statistics

## Hybrid Execution

There are two independent modes. Their PostgreSQL volumes are intentionally separate.

| Mode | Docker project | API | Database |
| --- | --- | --- | --- |
| Full Docker stack | `quizforge-stack` | Container on port 8081 | Internal `postgres:5432`, volume `quizforge-stack_postgres-data` |
| IntelliJ / local Java | `quizforge-srv` (PostgreSQL only) | Local process on port 8081 | `localhost:5433/quizforge_dev`, volume `quizforge-srv_dev-postgres-data` |

### Full Docker Stack

Run from the workspace root (the parent of `quizforge-srv`):

```sh
docker compose up -d --build
```

This starts `quizforge-fed`, `quizforge-srv` and `quizforge-postgres` under
`quizforge-stack`. The backend uses the `docker` profile and the datasource
environment variables defined in the root Compose file. Frontend: port 3000;
API: port 8081. Database and JWT settings have local defaults in the Compose
file; override them with environment variables for deployments.

Stop the stack without deleting volumes:

```sh
docker compose stop
```

### IntelliJ / Local Java

The Compose file inside `quizforge-srv` starts only PostgreSQL, not another API:

```sh
docker compose -f quizforge-srv/docker-compose.yml up -d postgres
```

The existing database is already in volume `quizforge-srv_dev-postgres-data`.
To start the currently existing container without recreating it:

```sh
docker start 1b75a11c22aa_quizforge-postgres
```

In IntelliJ, run `QuizforgeApplication` with **Active profiles: `dev`**.
The `dev` profile is also the default when no active profile is supplied.
Spring Boot does not start or manage Compose in this mode. Remove stale
`SPRING_DATASOURCE_*` or `SPRING_DOCKER_COMPOSE_*` overrides from the IntelliJ
run configuration if they point to the stack or to another database.

Run the frontend locally from `quizforge-fed` with `npm run dev`. Its default
proxy uses `localhost:8081`. Do not run the local API and the stack API at the
same time: both use port 8081. Do not use `docker compose down -v`, which deletes
database volumes.

## Run Locally

### Prerequisites

- Java 21+
- Git
- Docker (PostgreSQL and integration tests)

### Commands

Linux/macOS:

```bash
./mvnw verify
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Windows (PowerShell):

```powershell
.\mvnw.cmd verify
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

These commands expose the API at `http://localhost:8081`, as expected by the frontend.

## API

The backend exposes `/auth`, `/subjects`, `/questions/import`, `/template`,
`/exams`, `/history`, `/users` and `/admin/users`. The `/api` prefix belongs
to the frontend proxy, not to the backend controllers.

Example body for `POST /exams`:

```json
{
  "title": "Weekly review",
  "subjectId": 1,
  "quantity": 10
}
```

## Database

The project uses PostgreSQL. Configuration is in `src/main/resources/application.yml`.
Both development and Docker use `update`, not `create-drop`, and automatic SQL
initialization is disabled. Neither mode drops the existing schema at shutdown.

Selected alternative IDs are persisted in `user_answer_alternatives`. Existing
attempts whose selected IDs were never stored cannot be reconstructed retroactively.
The integration test uses a separate PostgreSQL container and exercises the real
HTTP API, including spreadsheet import, ownership, cancellation and statistics.

## Exam Contracts

`POST /exams/{id}/finish` accepts `{"answers":{"101":[1],"102":[]}}`.
Missing or empty answers are unanswered questions. The result exposes
`correctAnswers`, `wrongAnswers`, `unansweredQuestions` and a boolean `answered`
on each question. History details also expose `answered` and persisted selections.

`DELETE /exams/{id}` returns 204 and removes only an unfinished exam owned by
the current user. Completed exams cannot be cancelled. Finish and cancel use the
same database lock to avoid conflicting concurrent changes. Cancelled attempts
do not appear in history, summaries or subject statistics.

Authentication failures return 401; insufficient permissions return 403.
Allowed CORS origins can be overridden with `APP_CORS_ALLOWED_ORIGINS`, a
comma-separated list of explicit origins. The defaults remain local ports 3000
and 8080; do not use wildcard origins with credentials.

## Architecture

- **Domain**: core entities and business rules
- **Application**: use cases and ports
- **Adapter**: HTTP and persistence adapters
- **Infrastructure**: framework-specific configuration and technical concerns

## License

This project is licensed under the MIT License.

---