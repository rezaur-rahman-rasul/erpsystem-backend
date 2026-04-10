package com.hisabnikash.erp.identityaccess.authorization.domain;

import com.hisabnikash.erp.identityaccess.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "auth_scope_sets")
@Getter
@Setter
@NoArgsConstructor
public class ScopeSet extends BaseAuditEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "tenant_id", nullable = false, length = 80)
    private String tenantId;

    @Column(name = "legal_entity_id", length = 80)
    private String legalEntityId;

    @Column(name = "branch_id", length = 80)
    private String branchId;

    @Column(name = "department_id", length = 80)
    private String departmentId;

    @Column(name = "scope_hash", nullable = false, unique = true, length = 120)
    private String scopeHash;

    @Column(name = "custom_scope_json", columnDefinition = "text")
    private String customScopeJson;
}
