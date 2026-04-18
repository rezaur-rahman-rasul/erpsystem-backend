package com.hishabnikash.erp.organization.legalentity.domain;

import com.hishabnikash.erp.organization.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "legal_entities",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_legal_entity_code", columnNames = "code"),
                @UniqueConstraint(name = "uk_legal_entity_registration_no", columnNames = "registration_no")
        }
)
@Getter
@Setter
public class LegalEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "legal_name", nullable = false, length = 255)
    private String legalName;

    @Column(name = "trade_name", length = 255)
    private String tradeName;

    @Column(name = "registration_no", nullable = false, length = 100)
    private String registrationNumber;

    @Column(name = "tax_no", length = 100)
    private String taxNumber;

    @Column(name = "country_code", nullable = false, length = 10)
    private String countryCode;

    @Column(name = "base_currency_code", nullable = false, length = 10)
    private String baseCurrencyCode;

    @Column(name = "fiscal_year_start_month", nullable = false)
    private Integer fiscalYearStartMonth;

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "postal_code", length = 50)
    private String postalCode;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "website", length = 150)
    private String website;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private LegalEntityStatus status;
}