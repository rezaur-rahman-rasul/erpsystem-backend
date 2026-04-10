CREATE TABLE locations (
    id UUID PRIMARY KEY,
    legal_entity_id UUID NOT NULL,
    branch_id UUID,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(50),
    country_code VARCHAR(10),
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT
);

ALTER TABLE locations
    ADD CONSTRAINT uk_location_code UNIQUE (code);

CREATE INDEX idx_locations_legal_entity_id ON locations (legal_entity_id);
CREATE INDEX idx_locations_branch_id ON locations (branch_id);
