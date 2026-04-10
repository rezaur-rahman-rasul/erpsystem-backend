package com.hisabnikash.erp.masterdata.currency.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCurrencyRequest(
        @NotBlank @Size(max = 10) String code,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 10) String symbol,
        @Min(0) @Max(6) int decimalPlaces,
        boolean active
) {
}
