# QuizForge

QuizForge is a backend API for creating and managing quiz questions and alternatives, built with **Spring Boot** and **Java 21**.

## About

This project follows a layered, hexagonal-style structure (Ports and Adapters) to keep business rules isolated from framework and infrastructure details.

### Main Features

- Create and manage questions
- Create and manage answer alternatives
- Input validation for request data
- REST API endpoints
- H2 database support for development

## Run Locally

### Prerequisites

- Java 21+
- Git

### Commands

Linux/macOS:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

Windows (PowerShell):

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

By default, the API should be available at `http://localhost:8080`.

## API

Based on the current codebase, question management endpoints are exposed from the web adapter (for example, `/api/questions`).

Example request body for question creation:

```json
{
  "title": "What is the capital of Brazil?",
  "description": "Geography question",
  "alternatives": [
    { "text": "Sao Paulo", "isCorrect": false },
    { "text": "Brasilia", "isCorrect": true },
    { "text": "Rio de Janeiro", "isCorrect": false }
  ]
}
```

## Database

The project uses H2 for development. Configuration is in `src/main/resources/application.yml`.

## Architecture

- **Domain**: core entities and business rules
- **Application**: use cases and ports
- **Adapter**: HTTP and persistence adapters
- **Infrastructure**: framework-specific configuration and technical concerns

## License

This project is licensed under the MIT License.

---