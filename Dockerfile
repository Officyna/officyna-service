# --- Stage 1: Build ---
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Download dependencies first (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Build application
COPY src ./src
RUN mvn clean package -DskipTests -q

# --- Stage 2: Runtime ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Install curl for healthcheck, then create non-root user
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        curl \
        ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# Baixa o certificado da AWS
RUN curl -fsSL \
    https://truststore.pki.rds.amazonaws.com/global/global-bundle.pem \
    -o /tmp/global-bundle.pem

# Importa no truststore da JVM
RUN keytool \
    -importcert \
    -trustcacerts \
    -alias aws-documentdb \
    -file /tmp/global-bundle.pem \
    -cacerts \
    -storepass changeit \
    -noprompt

RUN groupadd -r officyna && useradd -r -g officyna officyna

USER officyna

COPY --from=builder /app/target/officyna-service-*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]