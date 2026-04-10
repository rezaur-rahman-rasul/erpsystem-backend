package com.hisabnikash.erp.masterdata.paymentterm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreatePaymentTermRequest(
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 120) String name,
        @Min(0) int dueDays,
        @Min(0) Integer discountDays,
        @DecimalMin("0.00") BigDecimal discountPercentage,
        boolean active
) {
}
