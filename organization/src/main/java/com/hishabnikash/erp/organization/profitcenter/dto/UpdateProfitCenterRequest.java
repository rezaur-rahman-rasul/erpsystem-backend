package com.hishabnikash.erp.organization.profitcenter.dto;

import com.hishabnikash.erp.organization.profitcenter.domain.ProfitCenterStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateProfitCenterRequest {

    private UUID businessUnitId;

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    private ProfitCenterStatus status;
}
