package com.hishabnikash.erp.organization.integration.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "identity_org_access_refs")
@Getter
@Setter
public class OrganizationAccessReference {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "legal_entity_id", nullable = false)
    private UUID legalEntityId;

    @Column(name = "branch_id")
    private UUID branchId;

    @Column(name = "primary_access", nullable = false)
    private boolean primaryAccess;
}
