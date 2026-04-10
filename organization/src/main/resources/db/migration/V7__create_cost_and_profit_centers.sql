CREATE TABLE cost_centers (
    id UUID PRIMARY KEY,
    legal_entity_id UUID NOT NULL,
    department_id UUID,
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

ALTER TABLE cost_centers
    ADD CONSTRAINT uk_cost_center_code UNIQUE (code);

CREATE TABLE profit_centers (
    id UUID PRIMARY KEY,
    legal_entity_id UUID NOT NULL,
    business_unit_id UUID,
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

ALTER TABLE profit_centers
    ADD CONSTRAINT uk_profit_center_code UNIQUE (code);
