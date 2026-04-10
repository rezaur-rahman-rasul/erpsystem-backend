package com.hisabnikash.erp.enterprisestructure.branch.domain;

import com.hisabnikash.erp.enterprisestructure.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "branches",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_branch_code", columnNames = "code")
        })
@Getter
@Setter
public class Branch extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID legalEntityId;

    private UUID businessUnitId;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false)
    private String name;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String countryCode;

    private String phone;
    private String email;
    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BranchStatus status;
}