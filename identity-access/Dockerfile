# Build from the repository root, for example: docker build -f identity-access/Dockerfile .
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml ./
COPY cache-support/pom.xml cache-support/pom.xml
COPY identity-access/pom.xml identity-access/pom.xml

RUN mvn -B -ntp -pl identity-access -am -DskipTests dependency:go-offline

COPY cache-support/src cache-support/src
COPY identity-access/src identity-access/src

RUN mvn -B -ntp -pl identity-access -am -DskipTests package \
    && cp identity-access/target/identity-access-service-*.jar /tmp/app.jar

FROM eclipse-temurin:21-jre-jammy

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && groupadd --system --gid 10001 app \
    && useradd --system --uid 10001 --gid app --create-home --home-dir /home/app app \
    && mkdir -p /app \
    && chown app:app /app \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build --chown=app:app /tmp/app.jar /app/app.jar

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 CMD ["curl", "--fail", "http://localhost:8081/actuator/health"]

USER app

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
