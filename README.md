

# WebFlux Notification Server

Reactive notification service built with Spring Boot WebFlux, R2DBC, Redis Pub/Sub, and Server-Sent Events.

This service supports:

- creating notifications over HTTP
- storing notifications in Postgres
- publishing notification events to Redis
- streaming notifications to connected clients over SSE
- filtering streamed notifications by user, role, or global audience

## Stack

- Java 21
- Spring Boot
- Spring WebFlux
- Spring Data R2DBC
- Spring Data Redis Reactive
- Postgres
- Redis
- OpenAPI Generator
- Docker Compose

## Local dependencies

The service expects these local dependencies:

- Postgres on `localhost:5432`
- Redis on `localhost:6379`

The repository includes `compose.yaml` for both.

Before starting local infrastructure, make sure you have a Docker runtime available and running, for example:

- Colima
- Docker Desktop

## Start local infrastructure

```bash
docker-compose up -d postgres redis
```

Check running containers:

```bash
docker-compose ps
```

## Run the application

```bash
./gradlew bootRun
```

Base URL:

```text
http://localhost:8080/api
```

## Build and test

```bash
./gradlew openApiGenerate compileJava test --no-daemon
```

## OpenAPI and Swagger

OpenAPI spec:

```text
src/main/resources/openapi/open-api-swagger.yaml
```

Swagger UI:

```text
http://localhost:8080/api/docs
```

OpenAPI JSON:

```text
http://localhost:8080/api/api-docs
```

## API overview

### Create notification

`POST /api/notifications`

Example request:

```bash
curl -X POST http://localhost:8080/api/notifications \
  -H 'Content-Type: application/json' \
  --data '{
    "title": "System Maintenance",
    "message": "Planned maintenance starts at 10 PM UTC.",
    "type": "MAINTENANCE",
    "priority": "HIGH",
    "audienceType": "ROLE",
    "severity": "INFO",
    "targets": ["OPS", "ADMIN"]
  }'
```

Behavior:

- notification is stored in Postgres
- `status` defaults to `ACTIVE`
- `expiresAt` defaults to `createdAt + 15 days`
- created notification is published to Redis

### Stream notifications over SSE

`POST /api/notifications/stream`

Example request:

```bash
curl -N -X POST http://localhost:8080/api/notifications/stream \
  -H 'Content-Type: application/json' \
  -H 'Accept: text/event-stream' \
  --data '{
    "id": "user-123",
    "roles": ["OPS", "ADMIN"]
  }'
```

Example SSE event:

```text
event: notification
data: {"id":"6","title":"SSE Smoke Test","message":"hello from redis",...}
```

## Delivery rules

Notifications are delivered according to `audienceType`:

- `GLOBAL`
  - sent to every connected subscriber

- `USER`
  - sent when the connected user id exists in `targets`

- `ROLE`
  - sent when the connected user has at least one role present in `targets`

## Event flow

End-to-end flow:

1. client calls `POST /api/notifications`
2. service stores notification in Postgres
3. service maps notification to `NotificationEvent`
4. event is published to Redis channel configured by `app.redis.notification-channel`
5. listener consumes Redis pub/sub events
6. listener maps each event once to outbound `NotificationResponse`
7. matching SSE subscribers receive the response payload

## Redis design

Redis Pub/Sub uses a dedicated event contract:

- `NotificationEvent`

This keeps Redis payloads decoupled from:

- domain objects
- API response models

Redis serialization is configured centrally in:

```text
src/main/java/com/dev/org/config/RedisConfiguration.java
```

## Important implementation notes

- Redis subscription is lazy and only becomes active when clients subscribe to the SSE stream
- SSE fanout precomputes `NotificationResponse` once per event to reduce repeated mapping work
- tests do not require a live Redis server for `contextLoads()`

## Useful endpoints

- Health

```text
http://localhost:8080/actuator/health
```

- Swagger UI

```text
http://localhost:8080/api/docs
```

- OpenAPI JSON

```text
http://localhost:8080/api/api-docs
```

## Project layout

```text
src/main/java/com/dev/org/
  config/
  controller/
  domain/
  entity/
  event/
  mapper/
  repository/
  service/

src/main/resources/
  application.yml
  openapi/
  schema.sql
```

## Notes for local troubleshooting

If SSE or Redis publishing fails with connection errors, verify Redis is running:

```bash
docker-compose ps
```

If Swagger does not reflect the latest code, restart the running app process and refresh `/api/api-docs`.
