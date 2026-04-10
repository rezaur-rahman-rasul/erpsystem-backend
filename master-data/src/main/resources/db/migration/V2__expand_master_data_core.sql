create table if not exists customers (
    id uuid primary key,
    code varchar(30) not null unique,
    name varchar(150) not null,
    email varchar(150),
    phone varchar(50),
    tax_no varchar(100),
    active boolean not null,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint
);

create table if not exists suppliers (
    id uuid primary key,
    code varchar(30) not null unique,
    name varchar(150) not null,
    email varchar(150),
    phone varchar(50),
    tax_no varchar(100),
    active boolean not null,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint
);

create table if not exists warehouses (
    id uuid primary key,
    code varchar(30) not null unique,
    name varchar(150) not null,
    branch_id uuid not null,
    location_code varchar(50),
    active boolean not null,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint
);

create table if not exists tax_codes (
    id uuid primary key,
    code varchar(30) not null unique,
    name varchar(120) not null,
    rate numeric(10, 4) not null,
    inclusive boolean not null,
    active boolean not null,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint
);
