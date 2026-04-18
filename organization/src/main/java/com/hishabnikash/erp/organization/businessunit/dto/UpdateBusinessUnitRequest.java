package com.hishabnikash.erp.organization.businessunit.dto;

import com.hishabnikash.erp.organization.businessunit.domain.BusinessUnitStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateBusinessUnitRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 500)
    private String description;

    private UUID managerEmployeeId;

    @NotNull
    private BusinessUnitStatus status;
}
