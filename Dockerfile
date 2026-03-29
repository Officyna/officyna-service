# --- Stage 1: Build ---
FROM maven:3.9.9-eclipse-temurin-25 AS builder
WORKDIR /app

# Download dependencies first (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Build application
COPY src ./src
RUN mvn clean package -DskipTests -q

# --- Stage 2: Runtime ---
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

# Non-root user for security
RUN groupadd -r officyna && useradd -r -g officyna officyna
USER officyna

COPY --from=builder /app/target/officyna-service-*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]