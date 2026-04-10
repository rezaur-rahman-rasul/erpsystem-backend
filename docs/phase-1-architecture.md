# Phase 1 ERP Microservices

## Current layout

This workspace is now structured as a parent ERP root. The intended root name is `HishabNikash`.

Phase 1 services now exist as sibling directories:

- `gateway/`
- `identity-access/`
- `master-data/`
- `organization/`

## Service responsibilities

- `gateway`: Spring Cloud Gateway entry point, JWT validation, route prefixes, and user-context header forwarding.
- `identity-access`: authentication, refresh-token flow, users, roles, and a Phase 1 permission catalog.
- `master-data`: foundational ERP reference data for currencies, units of measure, and payment terms.
- `organization`: existing enterprise-structure service, now isolated as its own sibling microservice directory.

## Gateway routes

- `/identity/**` -> `identity-access`
- `/master-data/**` -> `master-data`
- `/organization/**` -> `organization`

The gateway strips the first path segment before forwarding. Example:

- `/identity/api/v1/auth/login` -> `identity-access` `/api/v1/auth/login`
- `/organization/api/v1/legal-entities` -> `organization` `/api/v1/legal-entities`

## Shared security contract

All Phase 1 services use the same JWT issuer and secret:

- `issuer`: `enterprise-platform`
- `secret`: `change-this-to-a-long-random-base64-like-secret-key`

`identity-access` issues access tokens with the claims already expected by the `organization` service:

- `sub`
- `username`
- `tenantId`
- `authorities`

## Initial bootstrap data

`identity-access` seeds:

- role: `PLATFORM_ADMIN`
- user: `admin`
- email: `admin@erp.local`
- password: `Admin@12345`

`master-data` seeds:

- currencies: `USD`, `BDT`
- units of measure: `EA`, `KG`
- payment term: `NET30`

## Local build and run

Build each new service with the root Maven wrapper:

```powershell
.\mvnw -f organization\pom.xml test
.\mvnw -f gateway\pom.xml test
.\mvnw -f identity-access\pom.xml test
.\mvnw -f master-data\pom.xml test
```

Run the full Phase 1 stack with:

```powershell
docker compose -f docker-compose.phase1.yml up --build
```

## Notes

- The former root service was moved into `organization/`.
- The physical outer folder rename from `EnterpriseStructureService` to `HishabNikash` is a filesystem-level move outside the original writable root.

## Configuration approach

- Runtime configuration is standardized on a single `application.yml` per service.
- Environment differences are provided through environment variables such as `DB_URL`, `KAFKA_BOOTSTRAP_SERVERS`, and `REDIS_HOST`.
- `application-test.yml` remains the only profile-specific file used across services for test isolation.
