package com.hishabnikash.erp.organization.costcenter.dto;

import com.hishabnikash.erp.organization.costcenter.domain.CostCenterStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateCostCenterRequest {

    private UUID departmentId;

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    private CostCenterStatus status;
}
