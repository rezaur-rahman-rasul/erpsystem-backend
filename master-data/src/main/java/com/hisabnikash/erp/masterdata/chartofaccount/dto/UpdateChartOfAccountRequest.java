package com.hisabnikash.erp.masterdata.chartofaccount.dto;

import com.hisabnikash.erp.masterdata.chartofaccount.domain.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateChartOfAccountRequest(
        @NotNull UUID legalEntityId,
        @NotBlank @Size(max = 40) String code,
        @NotBlank @Size(max = 150) String name,
        @NotNull AccountType accountType,
        UUID parentAccountId,
        boolean postingAllowed,
        boolean active
) {
}
