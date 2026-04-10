package com.hisabnikash.erp.enterprisestructure.settings.domain;

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
        name = "org_settings",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_org_settings_owner",
                columnNames = {"owner_type", "owner_id"}
        )
)
@Getter
@Setter
public class OrganizationSetting extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 30)
    private SettingOwnerType ownerType;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "default_currency", length = 10)
    private String defaultCurrency;

    @Column(name = "default_language", length = 20)
    private String defaultLanguage;

    @Column(name = "date_format", length = 30)
    private String dateFormat;

    @Column(name = "time_format", length = 30)
    private String timeFormat;

    @Column(name = "tax_region", length = 50)
    private String taxRegion;

    @Column(name = "invoice_prefix", length = 30)
    private String invoicePrefix;

    @Column(name = "po_prefix", length = 30)
    private String poPrefix;

    @Column(name = "employee_prefix", length = 30)
    private String employeePrefix;
}
