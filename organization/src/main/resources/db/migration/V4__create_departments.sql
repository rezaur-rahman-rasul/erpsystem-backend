CREATE TABLE departments (
    id UUID PRIMARY KEY,
    legal_entity_id UUID NOT NULL,
    branch_id UUID,
    parent_department_id UUID,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    head_employee_id UUID,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version BIGINT
);

ALTER TABLE departments
    ADD CONSTRAINT uk_department_code UNIQUE (code);

CREATE INDEX idx_departments_legal_entity_id ON departments (legal_entity_id);
CREATE INDEX idx_departments_branch_id ON departments (branch_id);
CREATE INDEX idx_departments_parent_department_id ON departments (parent_department_id);
