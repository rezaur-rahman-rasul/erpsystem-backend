package com.hisabnikash.erp.identityaccess.organizationaccess.domain;

import com.hisabnikash.erp.identityaccess.common.entity.BaseAuditEntity;
import com.hisabnikash.erp.identityaccess.user.domain.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "organization_access_assignments",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_org_access_user_legal_branch",
                columnNames = {"user_id", "legal_entity_id", "branch_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class OrganizationAccessAssignment extends BaseAuditEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(name = "legal_entity_id", nullable = false)
    private UUID legalEntityId;

    @Column(name = "branch_id")
    private UUID branchId;

    @Column(name = "primary_access", nullable = false)
    private boolean primaryAccess;
}
