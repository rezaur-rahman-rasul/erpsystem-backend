CREATE TABLE identity_user_refs (
    id UUID PRIMARY KEY,
    username VARCHAR(60) NOT NULL,
    email VARCHAR(120) NOT NULL,
    display_name VARCHAR(150) NOT NULL,
    tenant_id VARCHAR(80) NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE identity_org_access_refs (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    legal_entity_id UUID NOT NULL,
    branch_id UUID,
    primary_access BOOLEAN NOT NULL
);

CREATE TABLE master_data_warehouse_refs (
    id UUID PRIMARY KEY,
    branch_id UUID NOT NULL,
    code VARCHAR(30) NOT NULL,
    name VARCHAR(150) NOT NULL,
    active BOOLEAN NOT NULL
);
