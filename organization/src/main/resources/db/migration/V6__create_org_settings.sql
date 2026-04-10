CREATE TABLE org_settings (
    id UUID PRIMARY KEY,
    owner_type VARCHAR(30) NOT NULL,
    owner_id UUID NOT NULL,
    default_currency VARCHAR(10),
    default_language VARCHAR(20),
    date_format VARCHAR(30),
    time_format VARCHAR(30),
    tax_region VARCHAR(50),
    invoice_prefix VARCHAR(30),
    po_prefix VARCHAR(30),
    employee_prefix VARCHAR(30),
    created_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT
);

ALTER TABLE org_settings
    ADD CONSTRAINT uk_org_settings_owner UNIQUE (owner_type, owner_id);
