create table if not exists auth_actions (
    code varchar(30) primary key,
    name varchar(80) not null,
    applies_to_types varchar(200) not null,
    status varchar(20) not null default 'ACTIVE'
);

create table if not exists auth_resources (
    id uuid primary key,
    code varchar(20) not null,
    full_code varchar(160) not null unique,
    name varchar(160) not null,
    type varchar(30) not null,
    parent_id uuid,
    service_code varchar(10) not null,
    path_depth smallint not null,
    sort_order integer not null default 0,
    status varchar(20) not null default 'ACTIVE',
    metadata text,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint,
    constraint fk_auth_resources_parent
        foreign key (parent_id) references auth_resources (id) on delete set null,
    constraint uk_auth_resources_parent_code unique (parent_id, code)
);

create index if not exists idx_auth_resources_service_code on auth_resources (service_code);
create index if not exists idx_auth_resources_type on auth_resources (type);

create table if not exists auth_resource_permissions (
    id uuid primary key,
    resource_id uuid not null,
    action_code varchar(30) not null,
    permission_key varchar(200) not null unique,
    service_code varchar(10) not null,
    description varchar(300) not null,
    status varchar(20) not null default 'ACTIVE',
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint,
    constraint fk_auth_resource_permissions_resource
        foreign key (resource_id) references auth_resources (id) on delete cascade,
    constraint fk_auth_resource_permissions_action
        foreign key (action_code) references auth_actions (code),
    constraint uk_auth_resource_permissions_resource_action unique (resource_id, action_code)
);

create index if not exists idx_auth_resource_permissions_service_code
    on auth_resource_permissions (service_code);

create table if not exists auth_permission_aliases (
    id uuid primary key,
    permission_id uuid not null,
    alias_code varchar(200) not null unique,
    alias_type varchar(20) not null,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint,
    constraint fk_auth_permission_aliases_permission
        foreign key (permission_id) references auth_resource_permissions (id) on delete cascade
);

create table if not exists auth_role_permission_grants (
    id uuid primary key,
    role_id uuid not null,
    permission_id uuid not null,
    effect varchar(10) not null,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint,
    constraint fk_auth_role_permission_grants_role
        foreign key (role_id) references roles (id) on delete cascade,
    constraint fk_auth_role_permission_grants_permission
        foreign key (permission_id) references auth_resource_permissions (id) on delete cascade,
    constraint uk_auth_role_permission_grants_role_permission unique (role_id, permission_id)
);

create index if not exists idx_auth_role_permission_grants_role_id
    on auth_role_permission_grants (role_id);

create table if not exists auth_scope_sets (
    id uuid primary key,
    tenant_id varchar(80) not null,
    legal_entity_id varchar(80),
    branch_id varchar(80),
    department_id varchar(80),
    scope_hash varchar(120) not null unique,
    custom_scope_json text,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint
);

create table if not exists auth_user_role_assignments (
    id uuid primary key,
    user_id uuid not null,
    role_id uuid not null,
    scope_set_id uuid,
    status varchar(20) not null default 'ACTIVE',
    valid_from timestamp,
    valid_to timestamp,
    assigned_reason varchar(300),
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint,
    constraint fk_auth_user_role_assignments_user
        foreign key (user_id) references user_accounts (id) on delete cascade,
    constraint fk_auth_user_role_assignments_role
        foreign key (role_id) references roles (id) on delete cascade,
    constraint fk_auth_user_role_assignments_scope
        foreign key (scope_set_id) references auth_scope_sets (id) on delete set null
);

create index if not exists idx_auth_user_role_assignments_user_id
    on auth_user_role_assignments (user_id);

create table if not exists auth_user_permission_overrides (
    id uuid primary key,
    user_id uuid not null,
    permission_id uuid not null,
    scope_set_id uuid,
    effect varchar(10) not null,
    reason varchar(300),
    valid_to timestamp,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint,
    constraint fk_auth_user_permission_overrides_user
        foreign key (user_id) references user_accounts (id) on delete cascade,
    constraint fk_auth_user_permission_overrides_permission
        foreign key (permission_id) references auth_resource_permissions (id) on delete cascade,
    constraint fk_auth_user_permission_overrides_scope
        foreign key (scope_set_id) references auth_scope_sets (id) on delete set null
);

create index if not exists idx_auth_user_permission_overrides_user_id
    on auth_user_permission_overrides (user_id);
