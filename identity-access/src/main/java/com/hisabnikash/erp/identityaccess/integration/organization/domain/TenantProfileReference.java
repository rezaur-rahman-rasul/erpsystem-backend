package com.hisabnikash.erp.identityaccess.integration.organization.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "org_tenant_profile_refs")
@Getter
@Setter
public class TenantProfileReference {

    @Id
    private UUID id;

    @Column(name = "tenant_code", nullable = false, length = 80)
    private String tenantCode;

    @Column(name = "legal_entity_id", nullable = false)
    private UUID legalEntityId;

    @Column(name = "company_name", nullable = false, length = 255)
    private String companyName;

    @Column(name = "active", nullable = false)
    private boolean active;
}
