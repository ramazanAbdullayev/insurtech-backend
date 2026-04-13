FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw package -DskipTests -B

FROM eclipse-temurin:25-jre-alpine AS runtime
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

RUN apk add --no-cache \
    aws-cli \
    jq

COPY --from=builder /app/target/*.jar app.jar

COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

USER spring:spring
EXPOSE 8080

ENTRYPOINT ["/bin/sh", "/app/entrypoint.sh"]

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health \
    | grep -q '"status":"UP"' || exit 1