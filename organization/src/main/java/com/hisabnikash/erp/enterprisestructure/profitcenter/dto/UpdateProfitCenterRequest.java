package com.hisabnikash.erp.enterprisestructure.profitcenter.dto;

import com.hisabnikash.erp.enterprisestructure.profitcenter.domain.ProfitCenterStatus;
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
