package com.hisabnikash.erp.identityaccess.authorization.domain;

import com.hisabnikash.erp.identityaccess.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "auth_permission_aliases")
@Getter
@Setter
@NoArgsConstructor
public class PermissionAlias extends BaseAuditEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false)
    private ResourcePermission permission;

    @Column(name = "alias_code", nullable = false, unique = true, length = 200)
    private String aliasCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "alias_type", nullable = false, length = 20)
    private PermissionAliasType aliasType = PermissionAliasType.LEGACY;
}
