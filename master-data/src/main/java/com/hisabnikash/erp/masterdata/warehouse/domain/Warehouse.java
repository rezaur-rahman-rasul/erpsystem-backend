package com.hisabnikash.erp.masterdata.warehouse.domain;

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
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
public class Warehouse extends CompanyOwnedEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "branch_id", nullable = false)
    private UUID branchId;

    @Column(name = "location_code", length = 50)
    private String locationCode;

    @Column(name = "active", nullable = false)
    private boolean active;
}
