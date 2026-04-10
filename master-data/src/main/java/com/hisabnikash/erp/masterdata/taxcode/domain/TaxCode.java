package com.hisabnikash.erp.masterdata.taxcode.domain;

import com.hisabnikash.erp.masterdata.common.entity.CompanyOwnedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tax_codes")
@Getter
@Setter
@NoArgsConstructor
public class TaxCode extends CompanyOwnedEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal rate;

    @Column(name = "inclusive", nullable = false)
    private boolean inclusive;

    @Column(name = "active", nullable = false)
    private boolean active;
}
