package com.hishabnikash.erp.organization.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateDepartmentRequest {

    @NotNull
    private UUID legalEntityId;

    private UUID branchId;

    private UUID parentDepartmentId;

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 255)
    private String name;

    private UUID headEmployeeId;
}
