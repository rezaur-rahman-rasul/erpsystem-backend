package com.hisabnikash.erp.masterdata.employee.domain;

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
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee extends CompanyOwnedEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "employee_number", nullable = false, unique = true, length = 40)
    private String employeeNumber;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "designation", length = 120)
    private String designation;

    @Column(name = "active", nullable = false)
    private boolean active;
}
