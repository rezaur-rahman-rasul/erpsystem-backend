package com.hisabnikash.erp.enterprisestructure.tenantprofile.domain;

import com.hisabnikash.erp.enterprisestructure.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
        name = "tenant_profiles",
        uniqueConstraints = @UniqueConstraint(name = "uk_tenant_profile_code", columnNames = "tenant_code")
)
@Getter
@Setter
public class TenantProfile extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_code", nullable = false, length = 80)
    private String tenantCode;

    @Column(name = "legal_entity_id", nullable = false)
    private UUID legalEntityId;

    @Column(name = "company_name", nullable = false, length = 255)
    private String companyName;

    @Column(name = "brand_name", length = 255)
    private String brandName;

    @Column(name = "support_email", length = 150)
    private String supportEmail;

    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "active", nullable = false)
    private boolean active;
}
