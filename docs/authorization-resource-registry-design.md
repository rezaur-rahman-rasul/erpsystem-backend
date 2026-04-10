# Centralized Access Control and Resource Registry Design

This design extends the current `identity-access` service from Phase 1 into an enterprise authorization platform for the ERP. It keeps authentication and authorization separated, preserves the current JWT-based gateway flow, and replaces the static in-code permission catalog with a governed resource registry and effective-permission engine.

It is explicitly designed to evolve from the current implementation in:

- `identity-access/src/main/resources/db/migration/V1__create_identity_schema.sql`
- `identity-access/src/main/java/com/hisabnikash/erp/identityaccess/security/permission/PhaseOnePermissions.java`
- `identity-access/src/main/java/com/hisabnikash/erp/identityaccess/auth/application/AuthService.java`
- `gateway/src/main/java/com/hisabnikash/erp/gateway/security/UserContextRelayFilter.java`

## 1. Overall architecture

### 1.1 Logical components

1. Authentication service
   - Owns login, refresh tokens, password lifecycle, session revocation, MFA and SSO later.
   - Issues JWT access tokens.
   - Does not own permission rules beyond identity claims and authorization version metadata.

2. Authorization service
   - Owns resource registry.
   - Owns actions, permissions, roles, role assignments, user overrides, scopes, and policy evaluation.
   - Produces effective permission snapshots for frontend and backend.
   - Exposes APIs for permission check, menu tree, screen policy, and audit.

3. Admin panel
   - Non-technical administration surface for roles, assignments, exceptions, registry search, comparison, and audit.

4. Gateway
   - Validates JWT.
   - Relays user context headers.
   - Can perform coarse route-level checks if a route is mapped to an API resource.
   - Must not be the only enforcement point.

5. Microservices
   - Enforce backend permissions through a shared authorization client or policy enforcement middleware.
   - Never hardcode role names.
   - Call centralized authorization APIs or use cached snapshots.

6. Frontend applications
   - Use effective menu and screen policy payloads from the authorization service.
   - Hide or disable UI elements based on policy.
   - Never assume hidden UI equals secure backend access.

### 1.2 Enterprise authorization pattern

Use a standard policy pattern:

- PAP: Policy administration point = admin panel and registry management
- PDP: Policy decision point = authorization evaluation engine
- PEP: Policy enforcement points = gateway, backend interceptors, frontend policy consumer
- PIP: Policy information points = tenant, legal entity, branch, department, document attributes, user context

### 1.3 Recommended deployment model

- Keep `identity-access` as one microservice for now, but split it internally into `auth` and `authorization` modules.
- If scale demands later, extract authorization into its own `access-control` service without changing resource codes or APIs.
- Persist authorization data centrally in PostgreSQL.
- Cache effective permissions and screen policies in Redis.
- Emit `AuthorizationChanged` events for invalidation.

## 2. Core concepts and design principles

1. Authentication is separate from authorization.
2. Authorization is deny-by-default.
3. Resource codes are business-readable and stable.
4. Internal IDs are technical; business operations use resource codes.
5. Hierarchical codes are for governance, discovery, grouping, and traceability.
6. Do not rely on hierarchy alone to imply access inheritance.
7. Frontend visibility and backend API enforcement must resolve from the same source of truth.
8. Roles are reusable bundles; user overrides are exceptions, not the norm.
9. Scope is first-class: tenant, legal entity, branch, department, and future dimensions.
10. Advanced policies are additive to RBAC, not a replacement for RBAC baseline.
11. All permission changes must be auditable.
12. Resource creation should be centrally governed but service-owned through manifests or registration APIs, not ad hoc admin typing.

## 3. Database schema (tables and relationships)

### 3.1 Core tables

- `user_accounts`
- `roles`
- `role_hierarchy` optional
- `user_role_assignments`
- `actions`
- `resources`
- `resource_permissions`
- `role_permissions`
- `user_permission_overrides`
- `scope_sets`
- `resource_access_requirements`
- `resource_api_mappings`
- `policy_definitions`
- `policy_bindings`
- `audit_logs`
- `authorization_outbox`

### 3.2 Relationship summary

- One `resource` may have one parent `resource`.
- One `resource` may have many `resource_permissions`.
- One `role` may have many `role_permissions`.
- One `user` may have many `user_role_assignments`.
- One `scope_set` may be referenced by many role assignments or user overrides.
- One UI or API `resource` may depend on one or more `resource_permissions` through `resource_access_requirements`.
- One `policy_definition` may be bound to roles, permissions, or resources through `policy_bindings`.

### 3.3 Why this structure

- It normalizes permissions as `resource + action`.
- It allows UI resources and API resources to share the same underlying permission.
- It supports scope without duplicating roles.
- It leaves room for policy-based exceptions later.

## 4. Resource registry model

### 4.1 Resource types

Required:

- `SERVICE`
- `MODULE`
- `SCREEN`
- `BUTTON`
- `FIELD`
- `SECTION`
- `TAB`
- `API`

Optional later:

- `REPORT`
- `WORKFLOW`
- `MENU_GROUP`
- `DASHBOARD_WIDGET`

### 4.2 Resource fields

Each resource must have:

- `id` internal UUID
- `code` local segment code
- `full_code` full hierarchical code
- `name`
- `type`
- `parent_id`
- `status`
- `service_code`
- `path_depth`
- `sort_order`
- `metadata` JSON
- `created_at`, `created_by`, `updated_at`, `updated_by`

### 4.3 Recommended semantics

- `code`: local code segment only, for example `ORDR`, `LIST`, `APRV`, `SALR`.
- `full_code`: full path, for example `SAL.ORDR.LIST.APRV`.
- `parent_id`: points to the immediate parent.
- `service_code`: repeated for easier filtering and partitioning.
- `metadata`: frontend route, icon, component key, API path, HTTP method, field group, display label, default action, deprecation notes, and other extensible properties.

### 4.4 Registry creation model

Recommended model:

1. Technical teams register resources through a manifest or registration API.
2. Authorization service validates naming, uniqueness, parent type, and lifecycle status.
3. Admins do not invent codes manually in production for service-owned resources.
4. Admins may edit display names, sort order, tags, descriptions, and menu metadata where allowed.

### 4.5 Example hierarchy

```text
SAL                         SERVICE
SAL.ORDR                    MODULE
SAL.ORDR.LIST               SCREEN
SAL.ORDR.LIST.APRV          BUTTON
SAL.ORDR.LIST.EXPT          BUTTON
SAL.ORDR.FORM               SCREEN
SAL.ORDR.FORM.CUST          FIELD
SAL.ORDR.FORM.SALR          FIELD
SAL.ORDR.FORM.ADDR_TAB      TAB
SAL.ORDR.API.APRV           API
SAL.ORDR.API.SAVE           API
```

### 4.6 Resource registration example

```json
{
  "serviceCode": "SAL",
  "serviceName": "Sales",
  "version": "2026.04.10",
  "resources": [
    {
      "code": "SAL",
      "fullCode": "SAL",
      "type": "SERVICE",
      "name": "Sales",
      "status": "ACTIVE",
      "metadata": {
        "ownerService": "sales-service"
      }
    },
    {
      "code": "ORDR",
      "fullCode": "SAL.ORDR",
      "type": "MODULE",
      "name": "Sales Orders",
      "parentFullCode": "SAL",
      "status": "ACTIVE",
      "metadata": {
        "menuGroup": "Sales",
        "icon": "shopping-cart",
        "order": 20
      }
    },
    {
      "code": "LIST",
      "fullCode": "SAL.ORDR.LIST",
      "type": "SCREEN",
      "name": "Order List",
      "parentFullCode": "SAL.ORDR",
      "status": "ACTIVE",
      "metadata": {
        "route": "/sales/orders",
        "menu": true,
        "componentKey": "sales-order-list"
      }
    },
    {
      "code": "APRV",
      "fullCode": "SAL.ORDR.LIST.APRV",
      "type": "BUTTON",
      "name": "Approve Order",
      "parentFullCode": "SAL.ORDR.LIST",
      "status": "ACTIVE",
      "metadata": {
        "requiredPermission": "SAL.ORDR.API.APRV#APPROVE",
        "buttonStyle": "primary"
      }
    }
  ]
}
```

## 5. Permission model

### 5.1 Recommended action catalog

Base actions:

- `VIEW`
- `CREATE`
- `EDIT`
- `DELETE`
- `APPROVE`
- `REJECT`
- `EXPORT`
- `PRINT`
- `EXECUTE`

Optional later:

- `POST`
- `REOPEN`
- `CLOSE`
- `CANCEL`
- `IMPORT`
- `ASSIGN`

### 5.2 Store permission as resource + action, not as raw strings

Recommended source-of-truth model:

- table row in `resource_permissions` with `resource_id` and `action_code`

Example:

- resource: `SAL.ORDR.API.APRV`
- action: `APPROVE`
- derived permission key: `SAL.ORDR.API.APRV#APPROVE`

### 5.3 Why resource + action is better than prebuilt strings

Use `resource + action` as the canonical model because:

1. It is normalized and prevents duplicate strings.
2. It supports metadata per permission.
3. It is easier to audit, join, search, and validate.
4. It allows action catalogs and resource-type validation.
5. It supports registry-driven administration.
6. It supports future aliases if a code is renamed.

Still generate a transport-friendly permission key, for example `SAL.ORDR.API.APRV#APPROVE`, for:

- cache keys
- API payloads
- audit output
- debugging
- temporary JWT compatibility if needed

### 5.4 Field-level rule model

Do not create a separate field-state table for the basic model. Derive field state from `VIEW` and `EDIT`:

- hidden = `VIEW` denied or absent
- read-only = `VIEW` allowed and `EDIT` denied or absent
- editable = `VIEW` allowed and `EDIT` allowed

This same rule works for:

- `FIELD`
- `SECTION`
- `TAB`

### 5.5 Effect model

Permission assignments should support:

- `ALLOW`
- `DENY`

Evaluation rule:

1. explicit user deny
2. explicit user allow
3. explicit role deny
4. explicit role allow
5. policy result
6. default deny

If you prefer a stricter model, keep policy evaluation before role allow and only let policy narrow access. That is safer for approval and finance workflows.

### 5.6 Permission inheritance recommendation

Do not automatically grant all child permissions from a parent resource at runtime.

Recommended approach:

- keep permissions explicit
- let admin UI offer bulk-apply to descendants
- let role templates seed common descendant permissions

This avoids accidental oversharing when a module grows.

## 6. Role and user mapping model

### 6.1 Core entities

- `users`
- `roles`
- `permissions`
- `user_role_assignments`
- `role_permissions`

### 6.2 Recommended role assignment structure

Use `user_role_assignments` instead of a plain `user_roles` join table.

Fields:

- `id`
- `user_id`
- `role_id`
- `scope_set_id` nullable for global tenant-wide assignment
- `status`
- `valid_from`
- `valid_to`
- `assigned_by`
- `assigned_reason`

Why:

- supports scoped roles
- supports temporary assignments
- supports audit and expiry

### 6.3 Direct user permission override

Use `user_permission_overrides` with:

- `user_id`
- `permission_id`
- `scope_set_id`
- `effect`
- `reason`
- `valid_to`

Rule:

- allow direct user overrides only through audited admin flows
- show them prominently in UI
- use them sparingly

### 6.4 Multi-tenant and organization scope

Use `scope_sets` as a reusable scope container.

Fields:

- `id`
- `tenant_id`
- `legal_entity_id`
- `branch_id`
- `department_id`
- `cost_center_id` optional later
- `custom_scope_json`
- `scope_hash`

Examples:

- tenant-wide only
- tenant + legal entity
- tenant + legal entity + branch
- tenant + legal entity + branch + department

This is more reusable than copying the same scope columns into every mapping table.

### 6.5 Future ABAC or policy-based extension

Add:

- `policy_definitions`
- `policy_bindings`

Use policy only for advanced cases such as:

- own branch only
- amount limit
- only creator can edit draft
- department head can approve up to threshold

Keep RBAC as the baseline. Policy should refine or constrain it.

## 7. API design for authorization service

All APIs below belong to the authorization boundary, even if they initially live inside the `identity-access` service.

### 7.1 Resource registry APIs

#### Register or sync resources

`POST /api/v1/authz/resources/sync`

Purpose:

- sync service manifests into registry

#### Search resources

`GET /api/v1/authz/resources?query=SAL.ORDR&type=SCREEN&status=ACTIVE`

#### Get resource detail

`GET /api/v1/authz/resources/{fullCode}`

### 7.2 Action and permission catalog APIs

#### List actions

`GET /api/v1/authz/actions`

#### List permissions by resource

`GET /api/v1/authz/resources/{fullCode}/permissions`

### 7.3 Role administration APIs

#### Create role

`POST /api/v1/authz/roles`

#### Clone role

`POST /api/v1/authz/roles/{roleId}/clone`

#### Compare roles

`GET /api/v1/authz/roles/compare?left=FIN_MANAGER&right=FIN_EXEC`

#### Assign permissions to role

`PUT /api/v1/authz/roles/{roleId}/permissions`

#### Assign role to user

`POST /api/v1/authz/users/{userId}/roles`

#### Add user override

`POST /api/v1/authz/users/{userId}/overrides`

### 7.4 Runtime authorization APIs

#### Get user effective permissions

`POST /api/v1/authz/effective-permissions`

Request:

```json
{
  "userId": "8d1e0993-c45e-4e66-9db9-b8d456f07121",
  "context": {
    "tenantId": "TENANT_A",
    "legalEntityId": "f7a2f2bb-658b-4d0a-86c5-81da5170c6f8",
    "branchId": "fb7fd9ee-43ab-47e7-90ec-6a3c3e9fe3b9",
    "departmentId": "4d5d032a-4591-4b8b-9f19-b5d0cf3cbf3f"
  },
  "serviceCode": "SAL"
}
```

#### Check permission

`POST /api/v1/authz/check`

Request:

```json
{
  "userId": "8d1e0993-c45e-4e66-9db9-b8d456f07121",
  "context": {
    "tenantId": "TENANT_A",
    "legalEntityId": "f7a2f2bb-658b-4d0a-86c5-81da5170c6f8",
    "branchId": "fb7fd9ee-43ab-47e7-90ec-6a3c3e9fe3b9"
  },
  "resourceCode": "SAL.ORDR.API.APRV",
  "action": "APPROVE",
  "attributes": {
    "documentAmount": 35000,
    "documentStatus": "PENDING_APPROVAL"
  }
}
```

Response:

```json
{
  "allowed": true,
  "decision": "ALLOW",
  "permissionKey": "SAL.ORDR.API.APRV#APPROVE",
  "evaluationVersion": 27,
  "reasons": [
    "ROLE_ALLOW:FIN_MANAGER",
    "SCOPE_MATCH:TENANT_A/LE-01/BR-01"
  ]
}
```

#### Get menu tree by user

`POST /api/v1/authz/menu`

#### Get screen policy by user

`POST /api/v1/authz/screen-policy`

#### Explain why a decision happened

`POST /api/v1/authz/explain`

This is important for admin usability.

### 7.5 Audit APIs

#### Audit search

`GET /api/v1/authz/audit?entityType=ROLE&entityId=...`

## 8. Frontend integration approach

### 8.1 Frontend usage model

Frontend should not compute permissions locally from role names. It should consume effective policy payloads from authorization APIs.

### 8.2 Menu rendering

Flow:

1. user logs in
2. frontend selects or receives working scope
3. frontend calls `POST /api/v1/authz/menu`
4. authorization service returns only visible modules and screens for that user and context

Rules:

- screen visible if effective `VIEW` is allowed
- module visible if it has at least one visible child
- disabled state can also be returned if a menu item is visible but blocked by policy, though most ERP menus should simply hide inaccessible items

### 8.3 Screen rendering

Frontend calls `POST /api/v1/authz/screen-policy` with `screenCode`.

Response contains:

- screen visibility
- allowed actions
- child button states
- field states
- section and tab states

### 8.4 UI decision rules

- show screen only when screen `VIEW` is allowed
- show button when required permission is allowed
- disable button when visible but execution is blocked by business state policy
- hide field if `VIEW` is denied
- make field read-only if `VIEW` allowed and `EDIT` denied
- make field editable if both `VIEW` and `EDIT` allowed

### 8.5 Important restriction

Frontend is a convenience layer only. Every sensitive action must still be enforced in backend APIs.

## 9. Backend enforcement approach

### 9.1 Principle

Backend is the final enforcement point. Never trust frontend visibility.

### 9.2 Recommended enforcement pattern

Every microservice should use a shared authorization client or middleware:

1. extract user identity and working scope from JWT and gateway headers
2. resolve the API resource code for the endpoint or operation
3. call the authorization decision engine or local cache
4. deny if not allowed
5. log decision metadata for audit and troubleshooting

### 9.3 API resource mapping

Use `resource_api_mappings` so services do not hardcode permission strings.

Example mapping:

- `POST /api/v1/orders/{id}/approve` -> `SAL.ORDR.API.APRV` + `APPROVE`
- `POST /api/v1/orders` -> `SAL.ORDR.API.SAVE` + `CREATE`
- `PUT /api/v1/orders/{id}` -> `SAL.ORDR.API.SAVE` + `EDIT`
- `GET /api/v1/orders` -> `SAL.ORDR.API.LIST` + `VIEW`

### 9.4 Where to enforce

Enforce at:

- gateway for coarse route checks where practical
- service controllers or filters for endpoint checks
- service domain layer for sensitive business-state checks if request path alone is not enough

### 9.5 Dynamic business data checks

Some decisions need runtime attributes:

- amount
- document status
- owner user ID
- cost center
- branch

Use the `attributes` object in `check` requests or local policy inputs. This is the bridge to ABAC later.

## 10. Caching and performance strategy

### 10.1 Recommended practical strategy for a large ERP

Use a hybrid approach:

1. JWT for identity and low-cardinality auth metadata
2. Redis for effective permission snapshots
3. local in-memory cache in each service for repeated checks
4. database as source of truth
5. event-based invalidation on authorization changes

### 10.2 What to keep in JWT

Keep only stable, compact claims:

- `sub`
- `username`
- `tenantId`
- `selectedScope` optional
- `roleCodes` optional
- `authzVersion`

Do not put full screen, button, and field permissions into JWT. That creates:

- token bloat
- stale permissions
- hard revocation problems

### 10.3 Snapshot model

Cache effective permission snapshots by:

- `userId`
- `scopeHash`
- `serviceCode`
- `authzVersion`

Snapshot may include:

- flattened permission keys
- menu tree
- screen policy for hot screens

### 10.4 Invalidation

Increment `authzVersion` or `permission_version` when:

- role permissions change
- user role assignment changes
- user override changes
- scoped assignment changes
- policy binding changes

Then:

- publish `AuthorizationChanged`
- evict Redis keys
- reject stale local cache entries

### 10.5 Check path recommendation

For backend APIs:

- first try local cache
- then Redis snapshot
- then authorization service or database-backed evaluator

For frontend:

- fetch menu and screen policy from authorization service
- cache client-side for session duration
- refetch on scope change or token refresh

## 11. Audit logging strategy

### 11.1 What to audit

Audit these change events:

- resource created, updated, deprecated
- permission created or retired
- role created, cloned, updated, deactivated
- permission assigned to role
- role assigned to user
- user override added or removed
- policy created or updated
- scope assignment changed
- login and logout from auth side

### 11.2 What to capture

- `id`
- `event_time`
- `actor_user_id`
- `actor_username`
- `tenant_id`
- `action_type`
- `entity_type`
- `entity_id`
- `entity_code`
- `before_json`
- `after_json`
- `reason`
- `ip_address`
- `user_agent`
- `trace_id`

### 11.3 Sample audit log entries

```json
[
  {
    "eventTime": "2026-04-10T14:15:32Z",
    "actorUsername": "admin",
    "actionType": "ROLE_PERMISSION_GRANTED",
    "entityType": "ROLE",
    "entityCode": "FIN_MANAGER",
    "reason": "Added approval access for branch finance managers",
    "before": {
      "permissions": [
        "SAL.ORDR.API.LIST#VIEW"
      ]
    },
    "after": {
      "permissions": [
        "SAL.ORDR.API.LIST#VIEW",
        "SAL.ORDR.API.APRV#APPROVE"
      ]
    },
    "traceId": "4ff8bb6bf8b94d34"
  },
  {
    "eventTime": "2026-04-10T14:18:04Z",
    "actorUsername": "admin",
    "actionType": "USER_OVERRIDE_CREATED",
    "entityType": "USER",
    "entityCode": "jdoe",
    "reason": "Temporary export permission until month-end close",
    "before": null,
    "after": {
      "permission": "SAL.ORDR.LIST.EXPT#EXECUTE",
      "effect": "ALLOW",
      "validTo": "2026-04-30T23:59:59Z"
    },
    "traceId": "cba48beaf9654b9f"
  }
]
```

## 12. Admin panel design

### 12.1 Main features

- role creation and editing
- clone role
- compare roles
- permission assignment using tree and matrix views
- search by resource code or name
- assign role to user with scope
- create user-specific exception
- effective access preview
- permission explanation
- audit trail

### 12.2 Recommended admin screens

1. Resource registry explorer
   - tree view by service, module, screen
   - search by `full_code`
   - filter by type and status

2. Permission matrix
   - rows = resources
   - columns = actions
   - bulk assign by module or screen subtree

3. Role workspace
   - overview
   - clone
   - compare
   - scoped grants preview

4. User access workspace
   - direct role assignments
   - scope assignments
   - user overrides
   - effective permissions

5. Audit explorer
   - who changed what and when

### 12.3 Usability recommendations

- show full resource code everywhere
- show human name beside code everywhere
- support search by code prefix
- show scope chips such as `TENANT_A / LE-01 / BR-01`
- show "explicit" versus "effective" permissions
- highlight user overrides in red or amber
- show permission source in tooltips: role, override, policy

## 13. Naming convention standard

### 13.1 General rules

- uppercase only
- dot-separated segments
- use letters, numbers, underscore
- start each segment with a letter
- avoid fixed-length enforcement if readability suffers
- codes should be stable and not tied to display labels

Regex per segment:

`^[A-Z][A-Z0-9_]{1,19}$`

### 13.2 Segment standards

#### Microservice code

- 2 to 6 characters
- examples: `IAM`, `ORG`, `MDM`, `SAL`, `HRM`, `FIN`

#### Module code

- 2 to 12 characters
- business domain oriented
- examples: `USER`, `ORDR`, `EMPL`, `APAY`, `ARCV`

#### Screen code

- 3 to 16 characters
- stable functional identifier
- examples: `LIST`, `FORM`, `DETAIL`, `SETTINGS`, `MAINT`

#### Button code

- 2 to 16 characters
- action or control identifier
- examples: `SAVE`, `APRV`, `RJCT`, `EXPT`, `PRINT_BTN`

#### Field code

- 2 to 20 characters
- semantic data name, not UI label text
- examples: `CUST`, `SALR`, `EMAIL`, `ADDR1`, `TAX_CODE`

#### Action code

- uppercase verbs
- examples: `VIEW`, `CREATE`, `EDIT`, `DELETE`, `APPROVE`, `REJECT`, `EXPORT`, `PRINT`, `EXECUTE`

### 13.3 Recommended code format by type

- service: `SAL`
- module: `SAL.ORDR`
- screen: `SAL.ORDR.LIST`
- button: `SAL.ORDR.LIST.APRV`
- field: `SAL.ORDR.FORM.SALR`
- API: `SAL.ORDR.API.APRV`

### 13.4 Naming guidance

Prefer:

- `SAL.ORDR.LIST`
- `SAL.ORDR.FORM`
- `SAL.ORDR.API.APRV`
- `HRM.EMPL.FORM.SALR`

Avoid:

- display-text-derived codes that change often
- too-short ambiguous codes
- numeric suffixes with no meaning
- mixed separator styles

## 14. Example resources and permissions

### 14.1 Sample resources

| Full code | Type | Name |
| --- | --- | --- |
| `IAM` | SERVICE | Identity and Access |
| `IAM.USER` | MODULE | User Management |
| `IAM.USER.LIST` | SCREEN | User List |
| `IAM.USER.LIST.ADD` | BUTTON | Add User |
| `IAM.USER.FORM.EMAIL` | FIELD | Email |
| `ORG.BRANCH.API.SAVE` | API | Save Branch |
| `SAL.ORDR.API.APRV` | API | Approve Sales Order |
| `SAL.ORDR.LIST.APRV` | BUTTON | Approve Order Button |
| `SAL.ORDR.FORM.SALR` | FIELD | Salary or Amount Field |

### 14.2 Sample permission records

| Permission key | Resource | Action |
| --- | --- | --- |
| `IAM.USER.LIST#VIEW` | `IAM.USER.LIST` | `VIEW` |
| `IAM.USER.LIST.ADD#EXECUTE` | `IAM.USER.LIST.ADD` | `EXECUTE` |
| `IAM.USER.API.SAVE#CREATE` | `IAM.USER.API.SAVE` | `CREATE` |
| `IAM.USER.API.SAVE#EDIT` | `IAM.USER.API.SAVE` | `EDIT` |
| `SAL.ORDR.API.APRV#APPROVE` | `SAL.ORDR.API.APRV` | `APPROVE` |
| `SAL.ORDR.LIST.EXPT#EXECUTE` | `SAL.ORDR.LIST.EXPT` | `EXECUTE` |
| `SAL.ORDR.FORM.SALR#VIEW` | `SAL.ORDR.FORM.SALR` | `VIEW` |
| `SAL.ORDR.FORM.SALR#EDIT` | `SAL.ORDR.FORM.SALR` | `EDIT` |

### 14.3 Sample role-permission mapping

Role: `FIN_MANAGER`

| Permission key | Effect | Scope |
| --- | --- | --- |
| `SAL.ORDR.LIST#VIEW` | `ALLOW` | `TENANT_A / LE-01 / BR-01` |
| `SAL.ORDR.API.LIST#VIEW` | `ALLOW` | `TENANT_A / LE-01 / BR-01` |
| `SAL.ORDR.API.APRV#APPROVE` | `ALLOW` | `TENANT_A / LE-01 / BR-01` |
| `SAL.ORDR.LIST.APRV#EXECUTE` | `ALLOW` | `TENANT_A / LE-01 / BR-01` |
| `SAL.ORDR.FORM.SALR#VIEW` | `ALLOW` | `TENANT_A / LE-01 / BR-01` |
| `SAL.ORDR.FORM.SALR#EDIT` | `DENY` | `TENANT_A / LE-01 / BR-01` |

Interpretation:

- manager can see the salary field
- manager cannot edit the salary field
- therefore field state is read-only

## 15. Example JSON payloads

### 15.1 Effective permissions response

```json
{
  "userId": "8d1e0993-c45e-4e66-9db9-b8d456f07121",
  "scope": {
    "tenantId": "TENANT_A",
    "legalEntityId": "LE-01",
    "branchId": "BR-01"
  },
  "serviceCode": "SAL",
  "authzVersion": 27,
  "permissions": [
    "SAL.ORDR.LIST#VIEW",
    "SAL.ORDR.API.LIST#VIEW",
    "SAL.ORDR.API.APRV#APPROVE",
    "SAL.ORDR.LIST.APRV#EXECUTE",
    "SAL.ORDR.FORM.SALR#VIEW"
  ]
}
```

### 15.2 Menu payload

```json
{
  "serviceCode": "SAL",
  "items": [
    {
      "resourceCode": "SAL.ORDR",
      "type": "MODULE",
      "name": "Sales Orders",
      "icon": "shopping-cart",
      "children": [
        {
          "resourceCode": "SAL.ORDR.LIST",
          "type": "SCREEN",
          "name": "Order List",
          "route": "/sales/orders",
          "visible": true
        }
      ]
    }
  ]
}
```

### 15.3 Screen policy payload

```json
{
  "screenCode": "SAL.ORDR.FORM",
  "visible": true,
  "actions": {
    "CREATE": true,
    "EDIT": true,
    "DELETE": false,
    "APPROVE": false,
    "EXPORT": true,
    "PRINT": true
  },
  "buttons": [
    {
      "resourceCode": "SAL.ORDR.FORM.SAVE",
      "visible": true,
      "enabled": true
    },
    {
      "resourceCode": "SAL.ORDR.FORM.APRV",
      "visible": false,
      "enabled": false
    }
  ],
  "fields": [
    {
      "resourceCode": "SAL.ORDR.FORM.CUST",
      "state": "EDITABLE"
    },
    {
      "resourceCode": "SAL.ORDR.FORM.SALR",
      "state": "READ_ONLY"
    },
    {
      "resourceCode": "SAL.ORDR.FORM.MARGIN",
      "state": "HIDDEN"
    }
  ],
  "tabs": [
    {
      "resourceCode": "SAL.ORDR.FORM.ADDR_TAB",
      "visible": true
    }
  ]
}
```

### 15.4 Role create request

```json
{
  "code": "FIN_MANAGER",
  "name": "Finance Manager",
  "description": "Branch finance manager role",
  "permissions": [
    {
      "permissionKey": "SAL.ORDR.API.APRV#APPROVE",
      "effect": "ALLOW"
    },
    {
      "permissionKey": "SAL.ORDR.FORM.SALR#EDIT",
      "effect": "DENY"
    }
  ]
}
```

### 15.5 User role assignment request

```json
{
  "roleCode": "FIN_MANAGER",
  "scope": {
    "tenantId": "TENANT_A",
    "legalEntityId": "LE-01",
    "branchId": "BR-01"
  },
  "validFrom": "2026-04-10T00:00:00Z",
  "validTo": null,
  "reason": "Assigned as branch finance manager"
}
```

## 16. Example SQL schema

The following SQL is illustrative and fits a PostgreSQL-style implementation.

```sql
create table if not exists actions (
    code varchar(30) primary key,
    name varchar(80) not null,
    applies_to_types varchar(200) not null,
    status varchar(20) not null default 'ACTIVE'
);

create table if not exists resources (
    id uuid primary key,
    code varchar(20) not null,
    full_code varchar(120) not null unique,
    name varchar(160) not null,
    type varchar(20) not null,
    parent_id uuid null references resources (id),
    service_code varchar(10) not null,
    path_depth smallint not null,
    sort_order integer not null default 0,
    status varchar(20) not null default 'ACTIVE',
    metadata jsonb,
    created_at timestamp not null default current_timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint not null default 0,
    constraint uk_resources_parent_code unique (parent_id, code)
);

create index if not exists idx_resources_service on resources (service_code);
create index if not exists idx_resources_type on resources (type);

create table if not exists resource_permissions (
    id uuid primary key,
    resource_id uuid not null references resources (id) on delete cascade,
    action_code varchar(30) not null references actions (code),
    permission_key varchar(180) not null unique,
    status varchar(20) not null default 'ACTIVE',
    created_at timestamp not null default current_timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint not null default 0,
    constraint uk_resource_permissions unique (resource_id, action_code)
);

create table if not exists scope_sets (
    id uuid primary key,
    tenant_id varchar(80) not null,
    legal_entity_id varchar(80),
    branch_id varchar(80),
    department_id varchar(80),
    custom_scope_json jsonb,
    scope_hash varchar(100) not null unique,
    created_at timestamp not null default current_timestamp,
    created_by varchar(100)
);

create table if not exists user_role_assignments (
    id uuid primary key,
    user_id uuid not null references user_accounts (id) on delete cascade,
    role_id uuid not null references roles (id) on delete cascade,
    scope_set_id uuid null references scope_sets (id),
    status varchar(20) not null default 'ACTIVE',
    valid_from timestamp,
    valid_to timestamp,
    assigned_reason varchar(300),
    created_at timestamp not null default current_timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint not null default 0
);

create index if not exists idx_user_role_assignments_user on user_role_assignments (user_id);
create index if not exists idx_user_role_assignments_scope on user_role_assignments (scope_set_id);

create table if not exists role_permissions (
    role_id uuid not null references roles (id) on delete cascade,
    permission_id uuid not null references resource_permissions (id) on delete cascade,
    effect varchar(10) not null check (effect in ('ALLOW', 'DENY')),
    created_at timestamp not null default current_timestamp,
    created_by varchar(100),
    primary key (role_id, permission_id)
);

create table if not exists user_permission_overrides (
    id uuid primary key,
    user_id uuid not null references user_accounts (id) on delete cascade,
    permission_id uuid not null references resource_permissions (id) on delete cascade,
    scope_set_id uuid null references scope_sets (id),
    effect varchar(10) not null check (effect in ('ALLOW', 'DENY')),
    reason varchar(300),
    valid_to timestamp,
    created_at timestamp not null default current_timestamp,
    created_by varchar(100)
);

create table if not exists resource_access_requirements (
    resource_id uuid not null references resources (id) on delete cascade,
    permission_id uuid not null references resource_permissions (id) on delete cascade,
    match_type varchar(10) not null default 'ALL_OF',
    primary key (resource_id, permission_id)
);

create table if not exists resource_api_mappings (
    id uuid primary key,
    resource_id uuid not null references resources (id) on delete cascade,
    http_method varchar(10) not null,
    path_pattern varchar(200) not null,
    action_code varchar(30) not null references actions (code),
    service_code varchar(10) not null,
    unique (http_method, path_pattern, service_code, action_code)
);

create table if not exists policy_definitions (
    id uuid primary key,
    code varchar(80) not null unique,
    name varchar(120) not null,
    effect varchar(10) not null check (effect in ('ALLOW', 'DENY')),
    expression_json jsonb not null,
    status varchar(20) not null default 'ACTIVE',
    created_at timestamp not null default current_timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100)
);

create table if not exists policy_bindings (
    id uuid primary key,
    policy_id uuid not null references policy_definitions (id) on delete cascade,
    role_id uuid null references roles (id) on delete cascade,
    permission_id uuid null references resource_permissions (id) on delete cascade,
    resource_id uuid null references resources (id) on delete cascade,
    scope_set_id uuid null references scope_sets (id)
);

create table if not exists audit_logs (
    id bigint generated always as identity primary key,
    event_time timestamp not null default current_timestamp,
    actor_user_id uuid,
    actor_username varchar(120),
    tenant_id varchar(80),
    action_type varchar(50) not null,
    entity_type varchar(50) not null,
    entity_id varchar(80),
    entity_code varchar(180),
    before_json jsonb,
    after_json jsonb,
    reason varchar(300),
    ip_address varchar(80),
    user_agent varchar(300),
    trace_id varchar(80)
);
```

### 16.1 Sample seed records

```sql
insert into actions (code, name, applies_to_types) values
('VIEW', 'View', 'SERVICE,MODULE,SCREEN,FIELD,SECTION,TAB,API'),
('CREATE', 'Create', 'SCREEN,API'),
('EDIT', 'Edit', 'SCREEN,FIELD,SECTION,TAB,API'),
('DELETE', 'Delete', 'SCREEN,API'),
('APPROVE', 'Approve', 'BUTTON,SCREEN,API'),
('REJECT', 'Reject', 'BUTTON,SCREEN,API'),
('EXPORT', 'Export', 'BUTTON,SCREEN,API'),
('PRINT', 'Print', 'BUTTON,SCREEN,API'),
('EXECUTE', 'Execute', 'BUTTON,API');
```

## 17. Migration strategy from a simple identity-access service to this improved model

### 17.1 Current state in this repository

Current implementation characteristics:

- users, roles, refresh tokens are already present
- `role_permissions` currently stores raw `permission_code` strings
- permissions are statically defined in `PhaseOnePermissions`
- JWT currently includes `authorities`
- organization access assignments already exist separately

### 17.2 Migration strategy

#### Step 1: introduce new authorization tables without breaking login

- add `actions`, `resources`, `resource_permissions`, `scope_sets`, `user_role_assignments`, `user_permission_overrides`
- keep current auth endpoints unchanged
- keep current `roles` and `user_accounts`

#### Step 2: backfill the static permission catalog into the resource registry

- map each existing string permission to a resource + action
- create aliases where the new hierarchical code differs from the old string
- keep old strings temporarily for backward compatibility

Example:

- `identity:user:view` -> `IAM.USER.LIST#VIEW`
- `identity:role:update` -> `IAM.ROLE.API.SAVE#EDIT`
- `enterprise:branch:view` -> `ORG.BRANCH.LIST#VIEW`

#### Step 3: convert `user_roles` to `user_role_assignments`

- migrate existing rows with null scope meaning tenant-wide
- preserve compatibility with a database view if needed

#### Step 4: move permission resolution from static strings to evaluator service

- replace `PhaseOnePermissions.catalog()` with database-backed catalog
- keep a generated permission-key response for current consumers

#### Step 5: issue JWT with `authzVersion`

- keep existing `authorities` claim only for backward compatibility during transition
- gradually reduce token contents

#### Step 6: integrate gateway and services with runtime authorization checks

- start with API resource checks for critical endpoints
- add frontend menu and screen policy APIs

#### Step 7: add field and button policies

- register UI resources
- expose screen policy payloads

#### Step 8: add policy engine for advanced scoped rules

- only after RBAC and scope are stable

### 17.3 Backward compatibility recommendation

For one or two releases:

- continue publishing legacy string permissions in JWT if existing services still use them
- also publish new `authzVersion`
- keep an alias table or generated mapping
- retire legacy string checks only after all services consume the centralized authorization APIs or shared client

## 18. Risks and common mistakes to avoid

1. Putting full effective permissions into JWT.
2. Treating frontend hiding as security.
3. Hardcoding role names like `ADMIN` or `FINANCE_MANAGER` inside microservices.
4. Letting admins create arbitrary resource codes without engineering governance.
5. Using hierarchy to imply automatic access inheritance everywhere.
6. Overusing user-specific overrides until roles become meaningless.
7. Ignoring working scope selection in a multi-company ERP.
8. Duplicating permission evaluation logic independently in every microservice.
9. Not versioning and invalidating cached permission snapshots.
10. Not auditing permission changes with before and after values.
11. Mixing employee master data with login identity.
12. Forgetting API resources while only modeling menus and screens.
13. Renaming resource codes casually and breaking audit traceability.
14. Binding UI buttons to permissions that backend does not enforce.

## 19. Recommended phased implementation plan

### Phase 1: basic RBAC

Goal:

- centralize permission catalog in database
- replace static in-code permission definitions
- keep auth separate from authz

Scope:

- `resources`
- `actions`
- `resource_permissions`
- `roles`
- `user_role_assignments`
- `role_permissions`
- effective permission API
- permission check API for backend
- audit logs for authz changes

Deliverables:

- hierarchical resource registry for services, modules, screens, and APIs
- database-backed permission catalog
- role management UI
- gateway keeps validating JWT
- backend services use centralized `check` endpoint or shared client

### Phase 2: screen, button, and field level control

Goal:

- extend control from coarse API and screen access to UI-level governance

Scope:

- register `BUTTON`, `FIELD`, `SECTION`, `TAB` resources
- add menu API
- add screen policy API
- add `resource_access_requirements`
- add admin tree and matrix permission assignment UI

Deliverables:

- show or hide menus
- show or hide screens
- enable or disable buttons
- hide or lock fields
- keep backend checks aligned with mapped API permissions

### Phase 3: scoped and policy-based authorization

Goal:

- support complex ERP cases without exploding the number of roles

Scope:

- mature `scope_sets`
- user override expiry and approvals
- `policy_definitions`
- `policy_bindings`
- decision explanation API
- amount and state based checks

Deliverables:

- tenant, legal entity, branch, department scoped access
- policy constraints like approval threshold or own-branch only
- explanation and simulation tools for admins

## Final recommendation

Use this target model:

1. Keep JWT focused on identity, not full authorization detail.
2. Make the resource registry the single governed source of truth.
3. Store permissions canonically as `resource + action`, with a derived transport key.
4. Use scoped role assignments instead of plain user-role joins.
5. Use one centralized evaluation engine for frontend and backend.
6. Add policy-based rules only after basic RBAC and screen policy are stable.

That combination is the most practical path for an ERP: readable codes, central governance, strong backend enforcement, admin usability, and controlled growth into enterprise-grade scoped authorization.
