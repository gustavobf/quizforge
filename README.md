# QuizForge

A lightweight quiz platform for creating questions, managing answer choices, taking exams, and reviewing results.

## Run with Docker

From the repository root, start the frontend, API, and PostgreSQL database:

```sh
docker compose up --build
```

- Web app: http://localhost:3000
- API: http://localhost:8081

Stop the stack without deleting database data with `docker compose down`.
The Compose defaults are for local development; override credentials and `JWT_SECRET` for deployments.