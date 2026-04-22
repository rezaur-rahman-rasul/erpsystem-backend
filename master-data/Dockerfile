# Build from the repository root, for example: docker build -f master-data/Dockerfile .
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml ./
COPY cache-support/pom.xml cache-support/pom.xml
COPY master-data/pom.xml master-data/pom.xml

RUN mvn -B -ntp -pl master-data -am -DskipTests dependency:go-offline

COPY cache-support/src cache-support/src
COPY master-data/src master-data/src

RUN mvn -B -ntp -pl master-data -am -DskipTests package \
    && cp master-data/target/master-data-service-*.jar /tmp/app.jar

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

EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 CMD ["curl", "--fail", "http://localhost:8082/actuator/health"]

USER app

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
