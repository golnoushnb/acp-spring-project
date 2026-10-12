# ACP Spring Project

A Java backend project developed using **Spring Boot**, with relational database integration, AWS SDK dependencies, and Docker packaging. The repository contains a Maven-based application and a Dockerfile for running the packaged service in a Java 21 container.

## Technology stack

| Component | Technology |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 4.0.2 |
| Web layer | Spring Web |
| Relational database access | Spring JDBC + PostgreSQL driver |
| Cloud integrations | AWS SDK for Java: S3 and DynamoDB |
| JSON processing | Jackson |
| Build tooling | Maven (Maven Wrapper included) |
| Testing | Spring Boot Test |
| Packaging | Docker (Eclipse Temurin 21 JRE) |

The AWS services listed above are declared as dependencies in `pom.xml`; their presence does not, by itself, establish which application features use them.

## Project structure

```text
acp-spring-project/
├── .mvn/wrapper/       # Maven Wrapper configuration
├── src/                # Application source and tests
├── .gitignore
├── Dockerfile           # Runtime container definition
├── mvnw                 # Maven Wrapper (macOS/Linux)
├── mvnw.cmd             # Maven Wrapper (Windows)
└── pom.xml              # Dependencies and build configuration
```

## Getting started

### Prerequisites

- Java 21
- Docker (optional, for container deployment)
- Access to any external services required by your application configuration (for example, a PostgreSQL database or AWS services)

### Clone the repository

```bash
git clone https://github.com/golnoushnb/acp-spring-project.git
cd acp-spring-project
```

### Build

On macOS or Linux:

```bash
./mvnw clean package
```

On Windows:

```powershell
.\mvnw.cmd clean package
```

If execution permission is missing on macOS/Linux, run `chmod +x mvnw` first.

### Run locally

```bash
./mvnw spring-boot:run
```

The application may require environment variables or application properties for PostgreSQL and/or AWS integrations. Set these according to the configuration in `src/main/resources` before running it. Do not commit real credentials.

## Run with Docker

The included `Dockerfile` packages the compiled JAR with Eclipse Temurin 21 JRE and exposes container port `8080`.

First build the JAR, then the image:

```bash
./mvnw clean package
docker build -t acp-spring-project .
```

Run the image:

```bash
docker run --rm -p 8080:8080 acp-spring-project
```

If your application needs a database or AWS configuration, pass the required environment variables with `--env-file` or `-e`, and ensure network connectivity from the container. The port mapping follows the Dockerfile; the actual application port can depend on runtime configuration.

## Tests

Run the configured Maven test suite with:

```bash
./mvnw test
```

## Development notes

- Maven coordinates: `uk.ac.ed.inf:acp-submission-1:0.0.1-SNAPSHOT`.
- The Dockerfile expects `target/acp-submission-1-0.0.1-SNAPSHOT.jar`, so build the project before building the image.
- Keep database passwords, AWS credentials, and environment-specific configuration outside version control.

## Project context

This repository is part of practical backend development coursework. It demonstrates the setup of a Spring Boot service with database and cloud SDK dependencies, Maven build tooling, and Docker packaging.

> **Documentation status:** The framework, dependencies, build commands, and Docker configuration above were verified against the repository's `pom.xml` and `Dockerfile`. Specific API routes, request examples, data models, and the implemented business workflow are not documented here because the application source files could not be inspected reliably.
