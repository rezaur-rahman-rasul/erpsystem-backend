CREATE TABLE branches (
    id UUID PRIMARY KEY,
    legal_entity_id UUID NOT NULL,
    business_unit_id UUID,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(50),
    country_code VARCHAR(10),
    phone VARCHAR(50),
    email VARCHAR(150),
    timezone VARCHAR(50),
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT
);

ALTER TABLE branches
    ADD CONSTRAINT uk_branch_code UNIQUE (code);
