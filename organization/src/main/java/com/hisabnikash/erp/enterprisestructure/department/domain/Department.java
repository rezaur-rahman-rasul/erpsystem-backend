package com.hisabnikash.erp.enterprisestructure.department.domain;



import com.hisabnikash.erp.enterprisestructure.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "departments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_department_code", columnNames = "code")
        })
@Getter
@Setter
public class Department extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID legalEntityId;

    private UUID branchId;

    private UUID parentDepartmentId;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false)
    private String name;

    private UUID headEmployeeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepartmentStatus status;
}