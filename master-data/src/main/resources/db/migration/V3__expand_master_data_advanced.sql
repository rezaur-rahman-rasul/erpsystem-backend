create table if not exists products (
    id uuid primary key,
    code varchar(40) not null unique,
    name varchar(150) not null,
    description varchar(500),
    unit_of_measure_id uuid not null,
    active boolean not null,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint
);

create table if not exists employees (
    id uuid primary key,
    employee_number varchar(40) not null unique,
    full_name varchar(150) not null,
    email varchar(150),
    phone varchar(50),
    designation varchar(120),
    active boolean not null,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint
);

create table if not exists chart_of_accounts (
    id uuid primary key,
    code varchar(40) not null unique,
    name varchar(150) not null,
    account_type varchar(30) not null,
    parent_account_id uuid,
    posting_allowed boolean not null,
    active boolean not null,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint
);
