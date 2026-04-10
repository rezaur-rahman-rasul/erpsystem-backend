package com.hisabnikash.erp.masterdata.integration.organization.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "org_legal_entity_refs")
@Getter
@Setter
public class LegalEntityReference {

    @Id
    private UUID id;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "active", nullable = false)
    private boolean active;
}
