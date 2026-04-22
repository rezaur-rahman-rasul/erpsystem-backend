# Build from the repository root, for example: docker build -f organization/Dockerfile .
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml ./
COPY cache-support/pom.xml cache-support/pom.xml
COPY organization/pom.xml organization/pom.xml

RUN mvn -B -ntp -pl organization -am -DskipTests dependency:go-offline

COPY cache-support/src cache-support/src
COPY organization/src organization/src

RUN mvn -B -ntp -pl organization -am -DskipTests package \
    && cp organization/target/organization-service-*.jar /tmp/app.jar

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

EXPOSE 8083

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 CMD ["curl", "--fail", "http://localhost:8083/actuator/health"]

USER app

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
