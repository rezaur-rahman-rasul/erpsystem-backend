CREATE TABLE subsidiaries (
    id UUID PRIMARY KEY,
    parent_legal_entity_id UUID NOT NULL,
    legal_entity_id UUID NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT
);

ALTER TABLE subsidiaries
    ADD CONSTRAINT uk_subsidiary_code UNIQUE (code);

CREATE TABLE fiscal_calendars (
    id UUID PRIMARY KEY,
    legal_entity_id UUID NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT
);

ALTER TABLE fiscal_calendars
    ADD CONSTRAINT uk_fiscal_calendar_code UNIQUE (code);

CREATE TABLE tenant_profiles (
    id UUID PRIMARY KEY,
    tenant_code VARCHAR(80) NOT NULL,
    legal_entity_id UUID NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    brand_name VARCHAR(255),
    support_email VARCHAR(150),
    website_url VARCHAR(255),
    logo_url VARCHAR(255),
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT
);

ALTER TABLE tenant_profiles
    ADD CONSTRAINT uk_tenant_profile_code UNIQUE (tenant_code);
