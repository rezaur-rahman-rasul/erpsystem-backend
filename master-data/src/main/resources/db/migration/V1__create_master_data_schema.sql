create table if not exists currencies (
    id uuid primary key,
    code varchar(10) not null unique,
    name varchar(100) not null,
    symbol varchar(10) not null,
    decimal_places integer not null,
    active boolean not null,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint
);

create table if not exists units_of_measure (
    id uuid primary key,
    code varchar(20) not null unique,
    name varchar(120) not null,
    category varchar(80) not null,
    base_unit boolean not null,
    conversion_factor numeric(19, 6) not null,
    active boolean not null,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint
);

create table if not exists payment_terms (
    id uuid primary key,
    code varchar(30) not null unique,
    name varchar(120) not null,
    due_days integer not null,
    discount_days integer,
    discount_percentage numeric(10, 2),
    active boolean not null,
    created_at timestamp,
    created_by varchar(100),
    updated_at timestamp,
    updated_by varchar(100),
    version bigint
);
