package com.hisabnikash.erp.masterdata.customer.domain;

import com.hisabnikash.erp.masterdata.common.entity.CompanyOwnedEntity;
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
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
public class Customer extends CompanyOwnedEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "tax_no", length = 100)
    private String taxNumber;

    @Column(name = "active", nullable = false)
    private boolean active;
}
