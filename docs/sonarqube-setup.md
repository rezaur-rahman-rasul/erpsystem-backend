# SonarQube Setup

This workspace is wired for SonarQube analysis through Maven from the repository root.

## What is configured

- Root `pom.xml` defines the SonarQube project metadata for the full workspace.
- Each module generates a JaCoCo XML coverage report during `verify`.
- SonarQube reads those reports from:
  - `cache-support/target/site/jacoco/jacoco.xml`
  - `gateway/target/site/jacoco/jacoco.xml`
  - `identity-access/target/site/jacoco/jacoco.xml`
  - `master-data/target/site/jacoco/jacoco.xml`
  - `organization/target/site/jacoco/jacoco.xml`

## Prerequisites

- A reachable SonarQube server
- A SonarQube token with permission to analyze the target project
- Java and Maven wrapper available in this repo

## Run analysis

PowerShell:

```powershell
$env:SONAR_HOST_URL = "http://localhost:9000"
$env:SONAR_TOKEN = "<your-token>"
.\mvnw.cmd -DskipITs verify sonar:sonar "-Dsonar.host.url=$env:SONAR_HOST_URL" "-Dsonar.token=$env:SONAR_TOKEN"
```

Bash:

```bash
export SONAR_HOST_URL="http://localhost:9000"
export SONAR_TOKEN="<your-token>"
./mvnw -DskipITs verify sonar:sonar -Dsonar.host.url="$SONAR_HOST_URL" -Dsonar.token="$SONAR_TOKEN"
```

## Notes

- The configured SonarQube project key is `com.hisabnikash.erp:hishabnikash`.
- Run the command from the `HishabNikash` repository root so the multi-module coverage paths resolve correctly.
- `verify` must run before `sonar:sonar`, otherwise coverage will not be imported.
