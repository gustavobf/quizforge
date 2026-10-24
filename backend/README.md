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

## Run with Docker

Start the complete stack from the workspace root:

```sh
docker compose up --build
```

This starts the frontend, API and PostgreSQL. Open the frontend at
`http://localhost:3000`; the API is available at `http://localhost:8081`.

Stop the stack without deleting database data:

```sh
docker compose down
```

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

The Docker stack uses PostgreSQL. Hibernate updates the schema automatically;
there is no need to create tables manually. Stopping the stack preserves the
database volume.

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