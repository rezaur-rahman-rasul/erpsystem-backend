package com.hisabnikash.erp.enterprisestructure.subsidiary.domain;

import com.hisabnikash.erp.enterprisestructure.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "subsidiaries",
        uniqueConstraints = @UniqueConstraint(name = "uk_subsidiary_code", columnNames = "code")
)
@Getter
@Setter
public class Subsidiary extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "parent_legal_entity_id", nullable = false)
    private UUID parentLegalEntityId;

    @Column(name = "legal_entity_id", nullable = false)
    private UUID legalEntityId;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private SubsidiaryStatus status;
}
