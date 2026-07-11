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


# Install curl and certificates
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        curl \
        ca-certificates \
        openssl && \
    rm -rf /var/lib/apt/lists/*


# Download AWS RDS / DocumentDB CA bundle
RUN curl -fsSL \
    https://truststore.pki.rds.amazonaws.com/global/global-bundle.pem \
    -o /tmp/global-bundle.pem


# Import AWS certificates into JVM truststore
RUN csplit -z -f /tmp/aws-cert- /tmp/global-bundle.pem \
        '/-----BEGIN CERTIFICATE-----/' '{*}' && \
    for cert in /tmp/aws-cert-*; do \
        keytool \
            -importcert \
            -trustcacerts \
            -alias $(basename $cert) \
            -file $cert \
            -cacerts \
            -storepass changeit \
            -noprompt || true; \
    done && \
    rm -rf /tmp/aws-cert-* /tmp/global-bundle.pem


# Create non-root user
RUN groupadd -r officyna && \
    useradd -r -g officyna officyna


USER officyna


COPY --from=builder /app/target/officyna-service-*.jar app.jar


EXPOSE 8080


HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1


ENTRYPOINT ["java", "-Djavax.net.ssl.trustStore=/opt/java/openjdk/lib/security/cacerts", "-Djavax.net.ssl.trustStorePassword=changeit", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]