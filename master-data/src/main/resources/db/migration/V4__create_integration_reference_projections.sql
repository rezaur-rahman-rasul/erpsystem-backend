create table if not exists org_branch_refs (
    id uuid primary key,
    legal_entity_id uuid not null,
    code varchar(50) not null,
    name varchar(255) not null,
    active boolean not null
);

create table if not exists identity_org_access_refs (
    id uuid primary key,
    user_id uuid not null,
    legal_entity_id uuid not null,
    branch_id uuid,
    primary_access boolean not null
);
