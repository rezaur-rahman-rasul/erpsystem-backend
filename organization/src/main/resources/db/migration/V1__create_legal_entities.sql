CREATE TABLE legal_entities (
                                id UUID PRIMARY KEY,
                                code VARCHAR(50) NOT NULL,
                                legal_name VARCHAR(255) NOT NULL,
                                trade_name VARCHAR(255),
                                registration_no VARCHAR(100) NOT NULL,
                                tax_no VARCHAR(100),
                                country_code VARCHAR(10) NOT NULL,
                                base_currency_code VARCHAR(10) NOT NULL,
                                fiscal_year_start_month INT NOT NULL,
                                address_line1 VARCHAR(255),
                                address_line2 VARCHAR(255),
                                city VARCHAR(100),
                                state VARCHAR(100),
                                postal_code VARCHAR(50),
                                phone VARCHAR(50),
                                email VARCHAR(150),
                                website VARCHAR(150),
                                status VARCHAR(30) NOT NULL,
                                created_at TIMESTAMP,
                                created_by VARCHAR(100),
                                updated_at TIMESTAMP,
                                updated_by VARCHAR(100),
                                version BIGINT
);

ALTER TABLE legal_entities
    ADD CONSTRAINT uk_legal_entity_code UNIQUE (code);

ALTER TABLE legal_entities
    ADD CONSTRAINT uk_legal_entity_registration_no UNIQUE (registration_no);