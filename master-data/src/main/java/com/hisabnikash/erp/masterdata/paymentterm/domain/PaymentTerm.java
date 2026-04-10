package com.hisabnikash.erp.masterdata.paymentterm.domain;

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
@Table(name = "payment_terms")
@Getter
@Setter
@NoArgsConstructor
public class PaymentTerm extends BaseAuditEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "due_days", nullable = false)
    private int dueDays;

    @Column(name = "discount_days")
    private Integer discountDays;

    @Column(name = "discount_percentage", precision = 10, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "active", nullable = false)
    private boolean active;
}
