## Multi-stage Dockerfile for building and running the Spring Boot app
# Stage 1: build with Maven + JDK 21
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy only the files required for dependency download first to leverage caching
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Copy source and build
COPY src ./src

# Ensure mvnw is executable
RUN chmod +x mvnw || true

# Build the application (skip tests for faster builds)
RUN ./mvnw -B -DskipTests package

# Stage 2: create a minimal runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the jar from the build stage. Use a glob to accommodate versioned jar name.
COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

# Use the PORT env var provided by Render at runtime
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar --server.port=${PORT:-8080}"]
