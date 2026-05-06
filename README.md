# FIAE-Exam-Part-1-Backend

Quarkus REST backend for the FIAE (Fachinformatiker Anwendungsentwicklung) exam learning platform. Serves flashcard content sourced from the [FIAE-Exam-Part-1-Content](https://github.com/seanconroy-dev/FIAE-Exam-Part-1-Content) GitHub repository, with support for both a seed JSON dataset and Markdown-based cards with YAML frontmatter.

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | [Quarkus 3.34.3](https://quarkus.io/) |
| Language | Java 21 |
| REST | Quarkus REST (RESTEasy Reactive) + Jackson |
| Validation | Hibernate Validator |
| Content source | GitHub API + Raw GitHub content URLs |
| YAML parsing | Jackson Dataformat YAML |

## Project Structure

```
fiae-exam-part-1-backend/
├── src/main/java/com/seanconroy/fiae/
│   ├── resource/
│   │   ├── CardResource.java        # /api/cards endpoints
│   │   └── AssetResource.java       # /api/assets/{filename} image serving
│   ├── service/
│   │   ├── CardService.java         # Loads cards from seed/cards.json
│   │   ├── MarkdownCardService.java # Parses Markdown cards with YAML frontmatter
│   │   └── GitHubContentService.java# Fetches/lists files from GitHub repository
│   ├── dto/
│   │   ├── CardDto.java             # id, title, description, module
│   │   ├── MarkdownCardDto.java     # Full markdown card (frontmatter + body)
│   │   ├── CardContentDto.java      # card.type, question, answer, image, examples
│   │   ├── ListResponseDto.java     # Generic list wrapper
│   │   ├── MarkdownCardListResponseDto.java
│   │   └── ErrorResponseDto.java
│   ├── validation/
│   │   └── QueryParamValidator.java # Rejects unknown query parameters
│   └── exception/
│       ├── GlobalExceptionMapper.java
│       ├── NotFoundExceptionMapper.java
│       ├── BadRequestExceptionMapper.java
│       └── ConstraintViolationExceptionMapper.java
└── src/main/resources/
    ├── application.properties
    ├── seed/cards.json              # Static seed card data
    └── content/                    # Local content directory (dev/test)
```

## API Endpoints

### Cards (seed data — `src/main/resources/seed/cards.json`)

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/cards?module={module}` | Get cards filtered by module (**required**) |
| `GET` | `/api/cards/all` | Get all seed cards |
| `GET` | `/api/cards/{id}` | Get a single card by ID |

### Markdown Cards (sourced live from GitHub)

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/cards/markdown` | Get all markdown cards (optionally filter with `?module=`) |
| `GET` | `/api/cards/markdown/{slug}` | Get a single markdown card by slug |
| `GET` | `/api/cards/markdown/count` | Returns the count of available markdown files |
| `GET` | `/api/cards/test` | Test endpoint — fetches a single card directly from GitHub raw URL |

### Assets

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/assets/{filename}` | Serve image assets (png, jpeg, webp, gif) from the configured `content.root` directory |

### Query Parameter Validation

Unknown query parameters are rejected with a `400 Bad Request` response. Only explicitly declared parameters (e.g. `module`) are accepted per endpoint.

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Path to the local content directory (used by AssetResource)
content.root=/path/to/FIAE-Exam-Part-1-Content

# CORS — allowed origins
quarkus.http.cors=true
quarkus.http.cors.origins=https://seanconroy-dev.github.io,http://localhost:4321
quarkus.http.cors.methods=GET,POST,PUT,DELETE,OPTIONS
quarkus.http.cors.headers=accept,authorization,content-type,x-requested-with
```

The Markdown card service reads content directly from the GitHub repository via the GitHub REST API and raw content URLs — no local clone of the content repo is required at runtime for the cards API.

## Running Locally

**Prerequisites:** Java 21, Maven

```shell
cd fiae-exam-part-1-backend
./mvnw quarkus:dev
```

The app starts on `http://localhost:8080`. Quarkus Dev UI is available at `http://localhost:8080/q/dev/`.

## Building

```shell
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

Über-jar:

```shell
./mvnw package -Dquarkus.package.jar.type=uber-jar
java -jar target/*-runner.jar
```

Native executable (requires GraalVM, or use container build):

```shell
./mvnw package -Dnative
# or without GraalVM:
./mvnw package -Dnative -Dquarkus.native.container-build=true
./target/fiae-exam-part-1-backend-1.0.0-SNAPSHOT-runner
```

## Content Repository

Markdown card files are fetched from:  
[`seanconroy-dev/FIAE-Exam-Part-1-Content`](https://github.com/seanconroy-dev/FIAE-Exam-Part-1-Content)

Cards are Markdown files (`ap1-*.md`) with YAML frontmatter containing:

```yaml
---
id: "..."
slug: "..."
title: "..."
module: "..."
tags: [...]
topics: [...]
status: "..."
created: "YYYY-MM-DD"
updated: "YYYY-MM-DD"
card:
  type: "..."
  question: "..."
  answer: "..."
  image: "assets/filename.png"   # optional
  answerImage: "..."             # optional
  examples: [...]                # optional
---

Markdown body content here...
```

Image paths in frontmatter are automatically resolved to raw GitHub URLs. 
