# ERP System Backend

Microservice-based ERP backend workspace.

## Services

- `gateway`: API gateway, JWT validation, request policy filters, and user context relay
- `identity-access`: authentication, authorization foundation, users, roles, and access registry
- `master-data`: shared ERP reference data
- `organization`: enterprise structure and organization master data

## Project Structure

- `pom.xml`: parent Maven aggregator
- `docker-compose.phase1.yml`: local Phase 1 stack
- `docs/`: architecture and authorization design notes
- `.mvn/`, `mvnw`, `mvnw.cmd`: Maven wrapper

## Build

```powershell
.\mvnw.cmd test
```

## Run

```powershell
docker compose -f docker-compose.phase1.yml up --build
```
