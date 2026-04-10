# ERP Module Target State

## Ownership decisions

- `gateway` owns front-door concerns: routing, token validation, throttling, tenant-aware policies, versioning.
- `identity-access` owns IAM: authentication, sessions, RBAC, permission catalog, SSO, organization/company access.
- `master-data` owns shared business reference data: customers, suppliers, products, chart of accounts, warehouses, currencies, tax codes.
- `organization` owns enterprise structure: legal entities, business units, branches, departments, cost centers, profit centers, subsidiaries, locations, company settings, tenant profile.

## Boundary clarification

- `branch` remains organization-owned. Warehouses in `master-data` may reference `branchId`, but branch master data should not be duplicated across services.
- `employees` are implemented as master-data business records in this phase. They remain separate from identity accounts and HR workflows.

## Current target implementation order

1. `master-data` core: customers, suppliers, warehouses, tax codes.
2. `organization` finance structure: cost centers, profit centers.
3. `master-data` advanced: products, chart of accounts, employee master records.
4. `identity-access` advanced IAM: organization/company access assignments and SSO.
5. `gateway` advanced policies: throttling, tenant-aware policies, version handling.

## Implemented in this tranche

- `master-data`: customers, suppliers, warehouses, tax codes, products, chart of accounts, employees
- `organization`: cost centers, profit centers, subsidiaries, fiscal calendars, tenant profiles
- `identity-access`: organization/company access assignments
- `gateway`: request throttling, tenant-aware policy enforcement, version header relay and route metadata

## Still pending after this tranche

- `identity-access`: SSO
- `gateway`: dynamic tenant-to-backend routing if you need per-tenant infrastructure or version-to-service remapping beyond the current path/header policy
