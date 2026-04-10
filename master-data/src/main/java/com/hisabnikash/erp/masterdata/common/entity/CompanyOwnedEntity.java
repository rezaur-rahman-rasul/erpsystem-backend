package com.hisabnikash.erp.masterdata.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class CompanyOwnedEntity extends BaseAuditEntity {

    @Column(name = "tenant_id", length = 80)
    private String tenantId;

    @Column(name = "legal_entity_id")
    private UUID legalEntityId;
}
