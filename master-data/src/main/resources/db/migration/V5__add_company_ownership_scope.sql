create table if not exists org_legal_entity_refs (
    id uuid primary key,
    code varchar(50) not null,
    name varchar(255) not null,
    active boolean not null
);

alter table customers add column if not exists tenant_id varchar(80);
alter table customers add column if not exists legal_entity_id uuid;

alter table suppliers add column if not exists tenant_id varchar(80);
alter table suppliers add column if not exists legal_entity_id uuid;

alter table warehouses add column if not exists tenant_id varchar(80);
alter table warehouses add column if not exists legal_entity_id uuid;

alter table tax_codes add column if not exists tenant_id varchar(80);
alter table tax_codes add column if not exists legal_entity_id uuid;

alter table products add column if not exists tenant_id varchar(80);
alter table products add column if not exists legal_entity_id uuid;

alter table employees add column if not exists tenant_id varchar(80);
alter table employees add column if not exists legal_entity_id uuid;

alter table chart_of_accounts add column if not exists tenant_id varchar(80);
alter table chart_of_accounts add column if not exists legal_entity_id uuid;

alter table customers drop constraint if exists customers_code_key;
alter table suppliers drop constraint if exists suppliers_code_key;
alter table warehouses drop constraint if exists warehouses_code_key;
alter table tax_codes drop constraint if exists tax_codes_code_key;
alter table products drop constraint if exists products_code_key;
alter table employees drop constraint if exists employees_employee_number_key;
alter table chart_of_accounts drop constraint if exists chart_of_accounts_code_key;

alter table customers add constraint uk_customer_legal_entity_code unique (legal_entity_id, code);
alter table suppliers add constraint uk_supplier_legal_entity_code unique (legal_entity_id, code);
alter table warehouses add constraint uk_warehouse_legal_entity_code unique (legal_entity_id, code);
alter table tax_codes add constraint uk_tax_code_legal_entity_code unique (legal_entity_id, code);
alter table products add constraint uk_product_legal_entity_code unique (legal_entity_id, code);
alter table employees add constraint uk_employee_legal_entity_number unique (legal_entity_id, employee_number);
alter table chart_of_accounts add constraint uk_chart_of_account_legal_entity_code unique (legal_entity_id, code);
