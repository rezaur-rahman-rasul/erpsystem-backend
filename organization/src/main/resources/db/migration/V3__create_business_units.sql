CREATE TABLE business_units (
    id UUID PRIMARY KEY,
    legal_entity_id UUID NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    manager_employee_id UUID,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT
);

ALTER TABLE business_units
    ADD CONSTRAINT uk_business_unit_code UNIQUE (code);

CREATE INDEX idx_business_units_legal_entity_id ON business_units (legal_entity_id);
