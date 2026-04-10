package com.hisabnikash.erp.masterdata.taxcode.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateTaxCodeRequest(
        @NotNull UUID legalEntityId,
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 120) String name,
        @DecimalMin("0.0000") @Digits(integer = 6, fraction = 4) BigDecimal rate,
        boolean inclusive,
        boolean active
) {
}
