create table if not exists org_legal_entity_refs (
    id uuid primary key,
    code varchar(50) not null,
    legal_name varchar(255) not null,
    active boolean not null
);

create table if not exists org_branch_refs (
    id uuid primary key,
    legal_entity_id uuid not null,
    code varchar(50) not null,
    name varchar(255) not null,
    active boolean not null
);

create table if not exists org_tenant_profile_refs (
    id uuid primary key,
    tenant_code varchar(80) not null,
    legal_entity_id uuid not null,
    company_name varchar(255) not null,
    active boolean not null
);
