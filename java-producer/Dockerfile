FROM eclipse-temurin:21-jre

# Install OpenTelemetry Java Agent
RUN wget https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar -O /app/opentelemetry-javaagent.jar

WORKDIR /app

# Copy the JAR file
COPY target/java-producer-*.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Set timezone
ENV TZ=UTC

EXPOSE 8080

# Run with OpenTelemetry agent
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]
