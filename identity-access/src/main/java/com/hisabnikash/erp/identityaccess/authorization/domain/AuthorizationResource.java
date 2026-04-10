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
@Table(name = "auth_resources")
@Getter
@Setter
@NoArgsConstructor
public class AuthorizationResource extends BaseAuditEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "full_code", nullable = false, unique = true, length = 160)
    private String fullCode;

    @Column(name = "name", nullable = false, length = 160)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private AuthorizationResourceType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private AuthorizationResource parent;

    @Column(name = "service_code", nullable = false, length = 10)
    private String serviceCode;

    @Column(name = "path_depth", nullable = false)
    private short pathDepth;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AuthorizationStatus status = AuthorizationStatus.ACTIVE;

    @Column(name = "metadata", columnDefinition = "text")
    private String metadata;
}
