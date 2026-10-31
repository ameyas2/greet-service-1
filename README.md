# greet-service

Spring Boot REST service that returns a generated greeting payload (header, description, author, timestamp) using Datafaker.

| Item | Value |
|------|--------|
| Group / artifact | `org.greet:greet-service:1.0-SNAPSHOT` |
| Parent | Spring Boot `4.1.2` |
| Java | **25** |
| Main class | `org.greet.SpringBootInitializer` |

---

## Quick start

**Requirements:** JDK 25, Maven 3.9+

```bash
# Run locally (IntelliJ or Maven)
mvn spring-boot:run
```

| Endpoint | URL |
|----------|-----|
| Greet API | [http://localhost:10001/greet-service/api/greet](http://localhost:10001/greet-service/api/greet) |
| Actuator | [http://localhost:10012/greet-service/actuator](http://localhost:10012/greet-service/actuator) |
| Health | [http://localhost:10012/greet-service/actuator/health](http://localhost:10012/greet-service/actuator/health) |

> Actuator runs on a **separate port** (`10012`). It does not inherit `server.servlet.context-path`; use `management.server.base-path` instead (configured below).

---

## `application.yaml` properties

| Property | Value | Description |
|----------|--------|-------------|
| `server.port` | `10001` | HTTP port for the main application |
| `server.servlet.context-path` | `/greet-service` | Context path for app APIs (not applied to the management port) |
| `spring.application.name` | `spring-boot-learn` | Application name |
| `spring.threads.virtual.enabled` | `true` | Enables virtual threads |
| `management.server.port` | `10012` | Separate port for Actuator |
| `management.server.base-path` | `/greet-service` | Base path on the management port (so URLs look like `/greet-service/actuator/...`) |
| `management.endpoints.web.exposure.include` | `*` | Expose all Actuator endpoints |
| `management.endpoint.health.show-details` | `always` | Always show health details |

Commented (unused) blocks in the same file cover optional PostgreSQL / JPA settings.

---

## `pom.xml` overview

### Coordinates & parent

```xml
<groupId>org.greet</groupId>
<artifactId>greet-service</artifactId>
<version>1.0-SNAPSHOT</version>

<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>4.1.2</version>
</parent>
```

### Maven properties

| Property | Value | Purpose |
|----------|--------|---------|
| `java.version` | `25` | Compiler release / Spring Boot Java level |
| `lombok.version` | `1.18.48` | Lombok for JDK 25 (`1.18.32` and older fail with `TypeTag :: UNKNOWN`) |
| `project.build.sourceEncoding` | `UTF-8` | Source file encoding |
| `docker.username` | `${env.DOCKER_USERNAME}` | Docker Hub user (override with `-D` or env) |
| `docker.password` | `${env.DOCKER_PASSWORD}` | Docker Hub token/password (override with `-D` or env) |
| `docker.image.tag` | `1.0-snapshot` | Image tag pushed by Jib |

Override Docker credentials without editing the POM:

```bash
# Env vars
export DOCKER_USERNAME=youruser
export DOCKER_PASSWORD=yourtoken
mvn clean install

# Or -D flags
mvn clean install -Ddocker.username=youruser -Ddocker.password=yourtoken
```

### Dependencies

| Dependency | Role |
|------------|------|
| `spring-boot-starter-web` | REST / embedded Tomcat (default Logback logging **excluded**) |
| `spring-boot-starter-log4j2` | Log4j2 logging |
| `spring-boot-starter-actuator` | Health and other management endpoints (Logback excluded) |
| `lombok` `1.18.48` (`provided`) | Boilerplate reduction; also listed under compiler annotation processors |
| `datafaker` `2.7.0` | Fake greeting content |

### Build plugins

**`maven-compiler-plugin`**
- `release` = `${java.version}` (25)
- Lombok on `annotationProcessorPaths` at `1.18.48`
- Compiler arg: `-Alombok.addLombokGeneratedAnnotation=true`

**`jib-maven-plugin` `3.5.2`**
- Bound to the `install` phase (`jib:build`)
- Base image: `eclipse-temurin:25-jre`
- Target: `docker.io/${docker.username}/greet-service:${docker.image.tag}` (+ `latest`)
- Container port metadata: `10001`
- JVM flags: initial RAM 20%, max RAM 90%

Package only (skip image push):

```bash
mvn clean package -DskipTests
```

Build and push image (runs on `install`):

```bash
mvn clean install -Ddocker.username=... -Ddocker.password=...
```

---

## Project layout

```
src/main/java/org/greet/
  SpringBootInitializer.java      # entry point
  config/ApplicationConfiguration.java
  controller/GreetController.java # GET /api/greet
  service/GreetService.java
  dto/GreetResponseDTO.java
  entity/Greet.java
src/main/resources/
  application.yaml
  log4j2-spring.xml
```

---

## Notes

- Prefer **Lombok ≥ 1.18.40** with JDK 25 in both Maven and IntelliJ annotation processors.
- App APIs live under `/greet-service` on port **10001**; Actuator under `/greet-service` on port **10012**.
- Do not commit Docker Hub tokens; use env vars or local Maven `-D` overrides.
