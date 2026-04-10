package com.hisabnikash.erp.identityaccess.authorization.domain;

import com.hisabnikash.erp.identityaccess.common.entity.BaseAuditEntity;
import com.hisabnikash.erp.identityaccess.role.domain.Role;
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
@Table(name = "auth_role_permission_grants")
@Getter
@Setter
@NoArgsConstructor
public class RolePermissionGrant extends BaseAuditEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false)
    private ResourcePermission permission;

    @Enumerated(EnumType.STRING)
    @Column(name = "effect", nullable = false, length = 10)
    private PermissionEffect effect = PermissionEffect.ALLOW;
}
