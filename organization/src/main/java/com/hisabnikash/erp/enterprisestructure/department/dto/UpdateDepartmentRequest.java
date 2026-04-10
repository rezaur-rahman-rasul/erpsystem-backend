package com.hisabnikash.erp.enterprisestructure.department.dto;

import com.hisabnikash.erp.enterprisestructure.department.domain.DepartmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateDepartmentRequest {

    private UUID branchId;

    @NotBlank
    @Size(max = 255)
    private String name;

    private UUID parentDepartmentId;

    private UUID headEmployeeId;

    @NotNull
    private DepartmentStatus status;
}
