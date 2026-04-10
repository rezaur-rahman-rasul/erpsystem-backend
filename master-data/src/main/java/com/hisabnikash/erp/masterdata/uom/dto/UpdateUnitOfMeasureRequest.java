package com.hisabnikash.erp.masterdata.uom.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateUnitOfMeasureRequest(
        @NotBlank @Size(max = 20) String code,
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 80) String category,
        boolean baseUnit,
        @NotNull @DecimalMin("0.000001") BigDecimal conversionFactor,
        boolean active
) {
}
