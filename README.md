# greet-service

Spring Boot REST service that returns a generated greeting payload (header, description, author, timestamp) using Datafaker. Logging uses Log4j2. The service is packaged and pushed as a container image with Jib, and includes Kubernetes manifests for Deployment, Service, HPA, and Ingress.

| Item | Value |
|------|--------|
| Group / artifact | `org.greet:greet-service:1.0-SNAPSHOT` |
| Parent | Spring Boot `4.1.2` |
| Java | **25** |
| Main class | `org.greet.SpringBootInitializer` |
| Docker image | `ameyas5/greet-service:latest` (see Jib / deploy manifests) |

---

## Table of contents

1. [Quick start](#quick-start)
2. [API](#api)
3. [Ports and context paths](#ports-and-context-paths)
4. [`application.yaml` properties](#applicationyaml-properties)
5. [`pom.xml` overview](#pomxml-overview)
6. [Logging (Log4j2)](#logging-log4j2)
7. [Docker / Jib](#docker--jib)
8. [Kubernetes](#kubernetes)
9. [Project layout](#project-layout)
10. [IntelliJ / JDK 25 notes](#intellij--jdk-25-notes)
11. [Troubleshooting](#troubleshooting)

---

## Quick start

**Requirements:** JDK 25, Maven 3.9+

```bash
# Compile
mvn clean package -DskipTests

# Run (Maven)
mvn spring-boot:run

# Or run main class in IntelliJ: org.greet.SpringBootInitializer
```

| Endpoint | URL |
|----------|-----|
| Greet API | http://localhost:10001/greet-service/api/greet |
| Actuator index | http://localhost:10012/greet-service/actuator |
| Health | http://localhost:10012/greet-service/actuator/health |
| Liveness | http://localhost:10012/greet-service/actuator/health/liveness |
| Readiness | http://localhost:10012/greet-service/actuator/health/readiness |

---

## API

### `GET /api/greet`

Full local URL: `http://localhost:10001/greet-service/api/greet`

Returns a random greeting built by `GreetService` via Datafaker.

**Example response**

```json
{
  "header": "Laudantium nesciunt quae fugiat adipisci nisi.",
  "description": "Harum quod non. Numquam unde nisi eaque quas error natus.",
  "author": "Mrs. Ryan Parker",
  "time": "2026-10-31T07:52:03.984030342"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `header` | string | Fake lorem sentence |
| `description` | string | Fake lorem paragraph |
| `author` | string | Fake full name |
| `time` | date-time | Server local date/time when the response was built |

---

## Ports and context paths

| Concern | Port | Path prefix | Notes |
|---------|------|-------------|--------|
| Application (REST) | `10001` | `/greet-service` | From `server.port` + `server.servlet.context-path` |
| Actuator | `10012` | `/greet-service` | From `management.server.port` + `management.server.base-path` |

Important:

- A **separate management port** does **not** inherit `server.servlet.context-path`.
- Without `management.server.base-path: /greet-service`, Actuator would be at `http://localhost:10012/actuator` (not under `/greet-service`).
- Hitting only `http://localhost:10001/` (no context path / no `/api/greet`) returns Spring’s Whitelabel **404**.

```
Browser / client
       │
       ├─ :10001/greet-service/api/greet ──► GreetController
       │
       └─ :10012/greet-service/actuator/* ──► Spring Boot Actuator
```

---

## `application.yaml` properties

| Property | Value | Description |
|----------|--------|-------------|
| `server.port` | `10001` | HTTP port for the main application |
| `server.servlet.context-path` | `/greet-service` | Context path for app APIs (not applied to the management port) |
| `spring.application.name` | `spring-boot-learn` | Application name (also used in Log4j2 pattern) |
| `spring.threads.virtual.enabled` | `true` | Enables Java virtual threads |
| `management.server.port` | `10012` | Separate port for Actuator |
| `management.server.base-path` | `/greet-service` | Base path on the management port |
| `management.endpoints.web.exposure.include` | `*` | Expose all Actuator web endpoints |
| `management.endpoint.health.show-details` | `always` | Always show health details |

### Optional / commented (not active)

PostgreSQL + JPA settings are present but commented out in `application.yaml`:

| Property (commented) | Example intent |
|----------------------|----------------|
| `spring.datasource.url` | e.g. Cockroach/Postgres at `192.168.29.220:26257/jpa` |
| `spring.datasource.username` / `password` | DB credentials |
| `spring.datasource.driver-class-name` | `org.postgresql.Driver` |
| `spring.jpa.hibernate.ddl-auto` | e.g. `none` |
| `spring.jpa.show-sql` | SQL logging |
| `spring.jpa.properties.hibernate.dialect` | `PostgreSQLDialect` |

To enable persistence you would also need `spring-boot-starter-data-jpa` and a JDBC driver on the classpath (currently not active in `pom.xml`).

### Runtime system properties set in code

`SpringBootInitializer` sets these before startup (used by Log4j2):

| Property | Source |
|----------|--------|
| `hostName` | `InetAddress.getLocalHost().getHostName()` |
| `hostAddress` | `InetAddress.getLocalHost().getHostAddress()` |

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
| `lombok.version` | `1.18.48` | Lombok for JDK 25 (`≤ 1.18.32` fails with `TypeTag :: UNKNOWN`) |
| `project.build.sourceEncoding` | `UTF-8` | Source file encoding |
| `docker.username` | `${env.DOCKER_USERNAME}` | Docker Hub user (override with `-D` or env) |
| `docker.password` | `${env.DOCKER_PASSWORD}` | Docker Hub token/password (override with `-D` or env) |
| `docker.image.tag` | `1.0-snapshot` | Image tag pushed by Jib |

Override Docker credentials without editing the POM:

```bash
# Env vars (PowerShell)
$env:DOCKER_USERNAME = "youruser"
$env:DOCKER_PASSWORD = "yourtoken"
mvn clean install

# Or -D flags
mvn clean install "-Ddocker.username=youruser" "-Ddocker.password=yourtoken"
```

### Dependencies

| Dependency | Role |
|------------|------|
| `spring-boot-starter-web` | REST / embedded servlet container (default Logback **excluded**) |
| `spring-boot-starter-log4j2` | Log4j2 logging |
| `spring-boot-starter-actuator` | Health and other management endpoints (Logback **excluded**) |
| `lombok` `1.18.48` (`provided`) | Boilerplate reduction; also on compiler annotation processors |
| `datafaker` `2.7.0` | Fake greeting content |

### Build plugins

**`maven-compiler-plugin`**

- `release` = `${java.version}` → **25**
- Lombok on `annotationProcessorPaths` at **1.18.48**
- Compiler arg: `-Alombok.addLombokGeneratedAnnotation=true`

**`jib-maven-plugin` `3.5.2`**

| Setting | Value |
|---------|--------|
| Phase / goal | `install` → `jib:build` |
| Base image | `eclipse-temurin:25-jre` |
| Target image | `docker.io/${docker.username}/greet-service:${docker.image.tag}` |
| Extra tag | `latest` |
| Main class | `org.greet.SpringBootInitializer` |
| Declared port | `10001` |
| JVM flags | `-XX:InitialRAMPercentage=20.0`, `-XX:MaxRAMPercentage=90.0` |

```bash
# JAR only (no image push)
mvn clean package -DskipTests

# Build + push image (Jib runs on install)
mvn clean install -Ddocker.username=... -Ddocker.password=...
```

---

## Logging (Log4j2)

Config file: `src/main/resources/log4j2-spring.xml`

| Item | Detail |
|------|--------|
| Active appender | Console (`SYSTEM_OUT`) |
| Root level | `info` |
| Pattern includes | timestamp, level, `spring.application.name`, `hostName`, `hostAddress`, thread, class/method/line, message |

Rolling file and socket appenders exist in the XML but are **commented out**.

Default Logback is excluded from `web` and `actuator` starters so only Log4j2 is on the classpath.

---

## Docker / Jib

Jib builds and pushes **without** a local Docker daemon.

1. Set `DOCKER_USERNAME` / `DOCKER_PASSWORD` (or `-D` equivalents).
2. Run `mvn clean install`.
3. Image published as `docker.io/<user>/greet-service:1.0-snapshot` and `:latest`.

Example run locally after pull:

```bash
docker run --rm -p 10001:10001 -p 10012:10012 ameyas5/greet-service:latest
```

Ensure the process inside the image listens on **10001** / **10012** (from `application.yaml`). Map host ports accordingly.

---

## Kubernetes

Manifests live under `src/main/resources/`:

| File | Resources |
|------|-----------|
| `deploy.yaml` | Namespace, Deployment, Service, HorizontalPodAutoscaler |
| `ingress.yaml` | Ingress (nginx) |

### Summary (`deploy.yaml`)

| Resource | Name | Notes |
|----------|------|--------|
| Namespace | `greet-service-ns` | Isolates all objects |
| Deployment | `greet-service-depl` | **3** replicas, RollingUpdate |
| Image | `ameyas5/greet-service:latest` | `imagePullPolicy: IfNotPresent` |
| Container ports | `10001` (app), `10012` (actuator) | |
| Resources | request `500m` / `1Gi`; limit `1` CPU / `2Gi` | |
| Probes | liveness / readiness / startup on actuator paths | port **10012** |
| Service | `greet-service-svc` | ClusterIP-style ports `10001` and `10012` |
| HPA | `greet-service-hpa` | min 3 / max 10; CPU 30% / memory 40%; scale-down stabilization 300s |

### Ingress (`ingress.yaml`)

| Path | Backend service port |
|------|----------------------|
| `/greet-service/actuator` (more specific, first) | `10012` |
| `/greet-service` | `10001` |

`ingressClassName: nginx`

Apply example:

```bash
kubectl apply -f src/main/resources/deploy.yaml
kubectl apply -f src/main/resources/ingress.yaml
```

---

## Project layout

```
greet-service/
├── pom.xml
├── README.md
└── src/main/
    ├── java/org/greet/
    │   ├── SpringBootInitializer.java       # entry point; sets hostName / hostAddress
    │   ├── config/ApplicationConfiguration.java  # Faker @Bean
    │   ├── controller/GreetController.java  # GET /api/greet
    │   ├── service/GreetService.java
    │   ├── dto/GreetResponseDTO.java
    │   └── entity/Greet.java
    └── resources/
        ├── application.yaml
        ├── log4j2-spring.xml
        ├── deploy.yaml                      # K8s Namespace, Deployment, Service, HPA
        └── ingress.yaml                     # K8s Ingress
```

---

## IntelliJ / JDK 25 notes

- Project SDK and Maven Runner JDK should be **JDK 25** (e.g. Corretto 25).
- Use **Lombok 1.18.48** (or ≥ 1.18.40). Older processors cause:

  `java.lang.ExceptionInInitializerError` / `TypeTag :: UNKNOWN`

- In **Settings → Compiler → Annotation Processors**, ensure the processor path uses `lombok-1.18.48.jar` only (remove any `1.18.32` entry).
- After changing Lombok versions: **Reload Maven project**, then **Invalidate Caches / Restart** if the IDE still fails to compile.

---

## Troubleshooting

| Symptom | Likely cause | What to do |
|---------|--------------|------------|
| Whitelabel 404 on `/` or `:10001` | Missing context path or wrong path | Use `/greet-service/api/greet` |
| 404 on `:10012/greet-service/actuator` | Missing `management.server.base-path` | Keep `base-path: /greet-service` or call `/actuator` without it |
| IDE `TypeTag :: UNKNOWN` | Old Lombok on annotation processor path | Use Lombok **1.18.48**; clear stale `1.18.32` from IntelliJ |
| Jib / Maven SSL `PKIX` errors | JDK truststore / proxy MITM | Import corporate CA into the JDK 25 `cacerts` |
| Docker Hub 401 from Jib | Wrong registry host or bad token | Prefer `docker.io/...`; use a valid PAT; do not commit tokens |
| Probe failures in K8s | Wrong actuator port/path | Probes must hit port **10012** and `/greet-service/actuator/health...` |

---

## Security reminders

- Never commit Docker Hub passwords or PATs. Prefer env vars or local `-D` overrides.
- Actuator currently exposes **all** endpoints (`include: "*"`) with health details always on. Tighten exposure for production.
