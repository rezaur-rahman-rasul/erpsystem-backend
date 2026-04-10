create table if not exists roles (
    id uuid primary key,
    code varchar(80) not null unique,
    name varchar(120) not null,
    description varchar(500),
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint
);

create table if not exists role_permissions (
    role_id uuid not null,
    permission_code varchar(120) not null,
    primary key (role_id, permission_code),
    constraint fk_role_permissions_role
        foreign key (role_id) references roles (id) on delete cascade
);

create table if not exists user_accounts (
    id uuid primary key,
    username varchar(60) not null unique,
    email varchar(120) not null unique,
    display_name varchar(150) not null,
    tenant_id varchar(80) not null,
    password_hash varchar(255) not null,
    status varchar(20) not null,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint
);

create table if not exists user_roles (
    user_id uuid not null,
    role_id uuid not null,
    primary key (user_id, role_id),
    constraint fk_user_roles_user
        foreign key (user_id) references user_accounts (id) on delete cascade,
    constraint fk_user_roles_role
        foreign key (role_id) references roles (id) on delete cascade
);

create table if not exists refresh_tokens (
    id uuid primary key,
    token varchar(120) not null unique,
    user_id uuid not null,
    expires_at timestamp not null,
    revoked boolean not null default false,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint,
    constraint fk_refresh_tokens_user
        foreign key (user_id) references user_accounts (id) on delete cascade
);

create index if not exists idx_refresh_tokens_user_id on refresh_tokens (user_id);
create index if not exists idx_user_accounts_tenant on user_accounts (tenant_id);
