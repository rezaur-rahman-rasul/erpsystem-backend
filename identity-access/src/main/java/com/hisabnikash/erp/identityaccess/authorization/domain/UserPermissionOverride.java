package com.hisabnikash.erp.identityaccess.authorization.domain;

import com.hisabnikash.erp.identityaccess.common.entity.BaseAuditEntity;
import com.hisabnikash.erp.identityaccess.user.domain.UserAccount;
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

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "auth_user_permission_overrides")
@Getter
@Setter
@NoArgsConstructor
public class UserPermissionOverride extends BaseAuditEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false)
    private ResourcePermission permission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scope_set_id")
    private ScopeSet scopeSet;

    @Enumerated(EnumType.STRING)
    @Column(name = "effect", nullable = false, length = 10)
    private PermissionEffect effect = PermissionEffect.ALLOW;

    @Column(name = "reason", length = 300)
    private String reason;

    @Column(name = "valid_to")
    private LocalDateTime validTo;
}
