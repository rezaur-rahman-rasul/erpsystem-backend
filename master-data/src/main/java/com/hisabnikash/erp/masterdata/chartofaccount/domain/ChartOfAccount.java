package com.hisabnikash.erp.masterdata.chartofaccount.domain;

import com.hisabnikash.erp.masterdata.common.entity.CompanyOwnedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "chart_of_accounts")
@Getter
@Setter
@NoArgsConstructor
public class ChartOfAccount extends CompanyOwnedEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 40)
    private String code;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 30)
    private AccountType accountType;

    @Column(name = "parent_account_id")
    private UUID parentAccountId;

    @Column(name = "posting_allowed", nullable = false)
    private boolean postingAllowed;

    @Column(name = "active", nullable = false)
    private boolean active;
}
