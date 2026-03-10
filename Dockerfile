# ─────────────────────────────────────────────────────────────
# Multi-stage Dockerfile for the automation framework
# Stage 1: Build & cache Maven dependencies
# Stage 2: Run tests (connects to Selenium Grid via env vars)
# ─────────────────────────────────────────────────────────────

FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Cache dependencies first
COPY pom.xml .
RUN mvn dependency:resolve dependency:resolve-plugins -q

# Copy source
COPY . .

# Build (compile only — tests run at container start)
RUN mvn compile -q -DskipTests

# ─────────────────────────────────────────────────────────────
# Runtime stage — lean image for test execution
# ─────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17-alpine

WORKDIR /app

COPY --from=builder /root/.m2 /root/.m2
COPY --from=builder /app /app

# Default env vars (overridable via docker-compose or CLI)
ENV BROWSER=chrome
ENV HEADLESS=true
ENV SELENIUM_GRID_URL=http://selenium-hub:4444/wd/hub
ENV PARALLEL_THREAD_COUNT=3
ENV ENVIRONMENT=dev

# Entry point: run tests
ENTRYPOINT ["mvn", "clean", "test", \
    "-Dbrowser=${BROWSER}", \
    "-Dheadless=${HEADLESS}", \
    "-Dselenium.grid.url=${SELENIUM_GRID_URL}", \
    "-Dparallel.thread.count=${PARALLEL_THREAD_COUNT}", \
    "-Denvironment=${ENVIRONMENT}"]
