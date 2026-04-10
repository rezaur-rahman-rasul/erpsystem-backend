package com.hisabnikash.erp.enterprisestructure.businessunit.dto;

import com.hisabnikash.erp.enterprisestructure.businessunit.domain.BusinessUnitStatus;
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
