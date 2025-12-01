# Webhook SQL Challenge

Spring Boot application for automated webhook-based SQL challenge submission.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Project Structure

```
webhook-sql-challenge/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/challenge/webhook/
│   │   │       ├── config/          # Configuration classes
│   │   │       ├── model/           # Data models
│   │   │       ├── service/         # Service layer
│   │   │       └── WebhookSqlChallengeApplication.java
│   │   └── resources/
│   │       └── application.yml      # Application configuration
│   └── test/
│       └── java/
│           └── com/challenge/webhook/  # Test classes
├── pom.xml                          # Maven dependencies
└── README.md
```

## Configuration

Edit `src/main/resources/application.yml` to configure:

- User details (name, registration number, email)
- API URLs for webhook generation and submission

## Building the Application

Build the executable JAR with all dependencies included:

```bash
mvn clean package
```

This creates a fat JAR at `target/webhook-sql-challenge-1.0.0.jar` (~21MB) containing:
- All application code
- All runtime dependencies
- Embedded Tomcat server

To skip tests during build:

```bash
mvn clean package -DskipTests
```

## Running the Application

### From Executable JAR (Recommended)

```bash
java -jar target/webhook-sql-challenge-1.0.0.jar
```

The JAR is fully self-contained and can be deployed anywhere with Java 17+ installed.

### Using Maven

```bash
mvn spring-boot:run
```

### With Custom Configuration

Override configuration properties at runtime:

```bash
java -jar target/webhook-sql-challenge-1.0.0.jar \
  --challenge.user.name="Your Name" \
  --challenge.user.reg-no="REG12345" \
  --challenge.user.email="your.email@example.com"
```

## Dependencies

- **Spring Boot Starter Web**: REST API and HTTP client support
- **Spring Boot Starter Validation**: Input validation
- **Lombok**: Reduce boilerplate code
- **Jackson**: JSON serialization/deserialization
- **Spring Boot Starter Test**: Unit testing framework
- **jqwik**: Property-based testing framework

## Testing

Run all tests:

```bash
mvn test
```

## How It Works

The application automatically executes the challenge workflow on startup:

1. Generates a webhook by calling the external API
2. Determines which SQL question to solve based on registration number
3. Solves the assigned SQL problem
4. Submits the solution with JWT authentication
