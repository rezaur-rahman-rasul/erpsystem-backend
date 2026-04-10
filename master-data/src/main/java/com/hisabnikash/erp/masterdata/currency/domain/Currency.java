package com.hisabnikash.erp.masterdata.currency.domain;

import com.hisabnikash.erp.masterdata.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "currencies")
@Getter
@Setter
@NoArgsConstructor
public class Currency extends BaseAuditEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "symbol", nullable = false, length = 10)
    private String symbol;

    @Column(name = "decimal_places", nullable = false)
    private int decimalPlaces;

    @Column(name = "active", nullable = false)
    private boolean active;
}
