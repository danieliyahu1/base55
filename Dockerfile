# Stage 1: Build the Java application
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package

# Stage 2: Install Node.js
FROM node:20-bullseye AS npm_installer

# This stage is empty as we are only using it as a source of Node.js binaries.
# The 'node:20-alpine' image already contains 'node', 'npm', and 'npx'.

# Stage 2.1: UVX stage using prebuilt derived image
FROM ghcr.io/astral-sh/uv:python3.12-bookworm-slim AS uvx_installer

# Stage 3: Create the final production image
FROM openjdk:21-jdk AS runner

WORKDIR /app

# Copy the entire Node.js environment from the 'npm_installer' stage
# This makes 'node' and 'npx' available to your application at runtime.
COPY --from=npm_installer /usr/local/ /usr/local/

# Copy UVX environment from uvx_installer
COPY --from=uvx_installer /usr/local/ /usr/local/

# Copy the built Java application JAR from the 'builder' stage
COPY --from=builder /app/target/base55-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 4000

ENTRYPOINT ["java", "-Dspring.shell.interactive.enabled=true", "-Dspring.shell.interactive.force=true", "-jar", "app.jar"]
