package com.hisabnikash.erp.masterdata.uom.domain;

import com.hisabnikash.erp.masterdata.common.entity.BaseAuditEntity;
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
@Table(name = "units_of_measure")
@Getter
@Setter
@NoArgsConstructor
public class UnitOfMeasure extends BaseAuditEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "category", nullable = false, length = 80)
    private String category;

    @Column(name = "base_unit", nullable = false)
    private boolean baseUnit;

    @Column(name = "conversion_factor", nullable = false, precision = 19, scale = 6)
    private BigDecimal conversionFactor;

    @Column(name = "active", nullable = false)
    private boolean active;
}
